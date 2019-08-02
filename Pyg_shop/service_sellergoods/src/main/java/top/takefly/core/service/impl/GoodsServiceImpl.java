package top.takefly.core.service.impl;

import java.util.*;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import top.takefly.core.dao.good.GoodsDao;
import top.takefly.core.dao.good.GoodsDescDao;
import top.takefly.core.dao.item.ItemDao;
import top.takefly.core.dao.seller.SellerDao;
import top.takefly.core.pojo.entity.GoodsAndGoodsDescAndItem;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.good.Goods;
import top.takefly.core.pojo.good.GoodsDesc;
import top.takefly.core.pojo.good.GoodsQuery;
import top.takefly.core.pojo.item.Item;
import top.takefly.core.pojo.item.ItemQuery;
import top.takefly.core.pojo.seller.Seller;
import top.takefly.core.service.GoodsService;
import top.takefly.core.service.ItemPageService;
import top.takefly.core.service.ItemSearchService;
import top.takefly.core.service.ItemService;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private SellerDao sellerDao;

    /**
     * 查询全部
     */
    @Override
    public List<Goods> findAll() {
        return goodsDao.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Goods> page = (Page<Goods>) goodsDao.selectByExample(null);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        goodsDao.insert(goods);
    }


    /**
     * 修改
     *
     * @param goodsAndGoodsDescAndItem
     */
    @Override
    public void update(GoodsAndGoodsDescAndItem goodsAndGoodsDescAndItem) {
        //更新产品
        Goods goods = goodsAndGoodsDescAndItem.getGoods();
        goodsDao.updateByPrimaryKeySelective(goodsAndGoodsDescAndItem.getGoods());

        //更新goodsDesc
        goodsDescDao.updateByPrimaryKey(goodsAndGoodsDescAndItem.getGoodsDesc());

        //更新item，要先删除之前的
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getId());
        //删除之前考虑先删除solr里面的
        List<Item> items = itemDao.selectByExample(query);
        //首先删除里面的对应商品
        if (items.size() > 0) {
            Long[] ids = new Long[items.size()];
            for (int i = 0; i < items.size(); i++) {
                ids[i] = items.get(i).getId();
            }
            //itemSearchService.BatchDeleteById(ids);
            itemDao.deleteByExample(query);
        }
        //再次插入
        insertItemList(goodsAndGoodsDescAndItem, goods);
        //再次将新的item插入
        List<Item> items2 = itemDao.selectByExample(query);
        if (items2.size() > 0) {
            //itemSearchService.addItemToSolr(items2);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public GoodsAndGoodsDescAndItem findOne(Long id) {
        GoodsAndGoodsDescAndItem goodsAndGoodsDescAndItem = new GoodsAndGoodsDescAndItem();
        //首先要查询该商品
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsAndGoodsDescAndItem.setGoods(goods);

        //查询goodsDesc
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goods.getId());
        goodsAndGoodsDescAndItem.setGoodsDesc(goodsDesc);

        //查询item根据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getId());
        List<Item> items = itemDao.selectByExample(query);
        goodsAndGoodsDescAndItem.setItemList(items);

        return goodsAndGoodsDescAndItem;
    }

    /**
     * 批量删除
     */
    @Autowired
    private Destination queueSolrDeleteDestination;

    @Autowired
    private Destination topicPageDeleteDestination;

    @Override
    public void delete(Long[] ids, String status) {
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            //删除只是逻辑删除
            goods.setIsDelete(status);
            goodsDao.updateByPrimaryKeySelective(goods);
            //逻辑上删除商品后要在solr索引库中删除该商品
            ItemQuery query = new ItemQuery();
            ItemQuery.Criteria criteria = query.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<Item> items = itemDao.selectByExample(query);
            if (items.size() > 0) {
                Long[] itemIds = new Long[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    itemIds[i] = items.get(i).getId();
                }
                //删除商品项索引
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(itemIds);
                    }
                });
            }
            //删除商品详情页
            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(id+"");
                }
            });
        }
    }

    /**
     * 批量删除
     */

    /*@Reference
    private ItemPageService itemPageService;*/

    @Autowired
    private Destination topicPageAndSolrDestination;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void updateAuditStatus(Long[] ids, String status) throws Exception {
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsDao.updateByPrimaryKeySelective(goods);

            //判断此时的状态
            if ("1".equals(status)) {
                //判断正确时应该导入到索引库
                //itemPageService.genItemHtml(id);
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(id+"");
                    }
                });
            }
        }

        //判断此时的状态是否为1
        //但是此时为了解耦要解除与itemSearchService的耦合
        if("1".equals(status)){
            for(Long id:ids){
                List<Item> items = itemService.findByPrimaryKey(id);
                String text = JSON.toJSONString(items);
                jmsTemplate.send(queueSolrAddDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(text);
                    }
                });
            }
        }
    }


    @Override
    public PageResult findPage(Goods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        GoodsQuery example = new GoodsQuery();
        GoodsQuery.Criteria criteria = example.createCriteria();

        if (goods != null) {
            /*if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //必须是当前用户才能操作当前数据
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }*/
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            /*if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }*/
            criteria.andIsDeleteIsNull();
        }

        Page<Goods> page = (Page<Goods>) goodsDao.selectByExample(example);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    @Override
    //@Transactional
    public void saveEntity(GoodsAndGoodsDescAndItem goodsAndGoodsDescAndItem) {
        //首先插入goods获取
        Goods goods = goodsAndGoodsDescAndItem.getGoods();
        goods.setAuditStatus("1");
        goodsDao.insert(goods);
        //获取此时的goods的id
        Long id = goods.getId();
        GoodsDesc goodsDesc = goodsAndGoodsDescAndItem.getGoodsDesc();
        goodsDesc.setGoodsId(id);
        //保存goodsDesc
        goodsDescDao.insert(goodsDesc);
        //保存item
        //保存对应的商品
        insertItemList(goodsAndGoodsDescAndItem, goods);
    }

    private void insertItemList(GoodsAndGoodsDescAndItem goodsAndGoodsDescAndItem, Goods goods) {
        //Goods goods = goodsAndGoodsDescAndItem.getGoods();
        if ("1".equals(goods.getIsEnableSpec())) {
            List<Item> itemList = goodsAndGoodsDescAndItem.getItemList();
            StringBuffer sb = new StringBuffer();
            for (Item item : itemList) {
                //插入商品到指定的
                item.setGoodsId(goods.getId());
                //设置属性值
                setItemValue(item, goods, goodsAndGoodsDescAndItem);
                //获取标题
                sb.append(goods.getGoodsName());
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    System.out.println(entry.getValue());
                    sb.append(" ").append(entry.getValue());
                }
                item.setTitle(sb.toString());
                //清空列表
                sb.delete(0, sb.length());

                itemDao.insertSelective(item);
            }
        } else {
            Item item = new Item();
            setItemValue(item, goods, goodsAndGoodsDescAndItem);
            item.setTitle(goods.getGoodsName());
            item.setPrice(goods.getPrice());//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            itemDao.insertSelective(item);
        }
    }

    @Override
    public String findName(Long id) {
        Goods goods = goodsDao.selectByPrimaryKey(id);
        return goods.getSellerId();
    }

    @Autowired
    private ItemService itemService;

    //@Reference
    //private ItemSearchService itemSearchService;
    @Autowired
    private Destination queueSolrAddDestination;

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsDao.updateByPrimaryKeySelective(goods);

            //判断此时状态是否为审核通过
            if ("1".equals(status)) {
                List<Item> items = itemService.findByPrimaryKey(id);
                for (Item item : items) {
                    item.setSpecMap(JSON.parseObject(item.getSpec(), Map.class));
                }
                //将商品更新到索引库中
                //itemSearchService.addItemToSolr(items);
                String text = JSON.toJSONString(items);
                jmsTemplate.send(queueSolrAddDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(text);
                    }
                });

                //生成页面
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(id+"");
                    }
                });
            }else{
                List<Item> items = itemService.findByPrimaryKey(id);
                for (Item item : items) {
                    item.setSpecMap(JSON.parseObject(item.getSpec(), Map.class));
                }
                Long [] itemIds = new Long[items.size()];
                for(int i = 0;i< items.size() ;i++){
                    itemIds[i] = items.get(i).getId();
                }
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(itemIds);
                    }
                });
            }
        }
    }

    public void setItemValue(Item item, Goods goods, GoodsAndGoodsDescAndItem goodsAndGoodsDescAndItem) {
        item.setSellerId(goods.getSellerId());
        item.setCategoryid(goods.getCategory3Id());
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());

        //查询店铺名称
        Seller seller = sellerDao.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(seller.getNickName());

        //设置图片
        List<Map> ImgMapList = JSON.parseArray(goodsAndGoodsDescAndItem.getGoodsDesc().getItemImages(), Map.class);
        if (ImgMapList.size() > 0) {
            item.setImage((String) ImgMapList.get(0).get("url"));
        }
    }

}
