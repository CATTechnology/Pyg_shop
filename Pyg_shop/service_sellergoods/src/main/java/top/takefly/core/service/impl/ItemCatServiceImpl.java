package top.takefly.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import top.takefly.core.dao.item.ItemCatDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.item.ItemCat;
import top.takefly.core.pojo.item.ItemCatQuery;
import top.takefly.core.service.ItemCatService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ItemCat> page = (Page<ItemCat>) itemCatDao.selectByExample(null);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(ItemCat itemCat) {
        itemCatDao.insert(itemCat);
//        addRedis(itemCat);
    }

    private void addRedis(ItemCat itemCat){
        //添加缓存
        BoundHashOperations itemCats = redisTemplate.boundHashOps("itemCats");
        itemCats.put(itemCat.getName() , itemCat.getTypeId());
    }


    /**
     * 修改
     */
    @Override
    public void update(ItemCat itemCat) {
//        updateRedis(itemCat);
        itemCatDao.updateByPrimaryKey(itemCat);
    }

    private void updateRedis(ItemCat itemCat){
        //首先要查询到原来的
        ItemCat oldItemCat = itemCatDao.selectByPrimaryKey(itemCat.getId());
        BoundHashOperations itemCats = redisTemplate.boundHashOps("itemCats");
        //判断是否包含
        if(itemCats.hasKey(oldItemCat.getName())){
            itemCats.delete(oldItemCat.getName());

            //添加新数据到缓存
            itemCats.put(itemCat.getName() , itemCat.getTypeId());
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
//            deleteRedisById(id);
            itemCatDao.deleteByPrimaryKey(id);
        }
    }

    private void deleteRedisById(Long id){
        //删除数据，清理缓存
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(id);
        BoundHashOperations itemCats = redisTemplate.boundHashOps("itemCats");
        itemCats.delete(itemCat.getName());
    }


    @Override
    public PageResult findPage(ItemCat itemCat, int pageNum, int pageSize) {


        PageHelper.startPage(pageNum, pageSize);

        ItemCatQuery example = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (itemCat.getName() != null && itemCat.getName().length() > 0) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }

        }

        Page<ItemCat> page = (Page<ItemCat>) itemCatDao.selectByExample(example);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    /**
     * 缓存品牌和规格数据
     * 当运营商缓存查询商品分类的时候缓存数据
     */
    public void cacheItemCats(){
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        for(ItemCat itemCat:itemCats){
            redisTemplate.boundHashOps("itemCats").put(itemCat.getName() , itemCat.getTypeId());
        }
    }

    /**
     * 通过parentID实现查询
     *
     * @param parentId
     * @param page
     * @param rows
     * @param itemCat
     * @return
     */
    @Override
    public PageResult findByParentId(Long parentId, Integer page, Integer rows, ItemCat itemCat) {
        //调用redis缓存
//        cacheItemCats();
        //首先查找
        //分页
        PageHelper.startPage(page, rows);

        ItemCatQuery itemcatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemcatQuery.createCriteria();
        if (parentId != null) {
            //设置上一级id
            criteria.andParentIdEqualTo(parentId);
        }
        //添加查询属性
        if (itemCat != null) {
            if (itemCat.getName() != null && itemCat.getName().length() > 0) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }
            if (itemCat.getTypeId() != null) {
                criteria.andTypeIdEqualTo(itemCat.getTypeId());
            }
        }
        Page<ItemCat> pages = (Page<ItemCat>) itemCatDao.selectByExample(itemcatQuery);
        //封装结果集
        return new PageResult((int) pages.getTotal(), pages.getResult());
    }

    @Override
    public List<ItemCat> findByParentIdUpdate(Long parentId) {
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCats = itemCatDao.selectByExample(query);
        return itemCats;
    }




}
