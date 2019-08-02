package top.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import top.takefly.core.dao.good.GoodsDao;
import top.takefly.core.dao.good.GoodsDescDao;
import top.takefly.core.dao.item.ItemCatDao;
import top.takefly.core.dao.item.ItemDao;
import top.takefly.core.pojo.good.Goods;
import top.takefly.core.pojo.good.GoodsDesc;
import top.takefly.core.pojo.item.Item;
import top.takefly.core.pojo.item.ItemCat;
import top.takefly.core.pojo.item.ItemQuery;
import top.takefly.core.service.ItemPageService;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${serverdir}")
    private String serverdir;

    @Override
    public boolean genItemHtml(Long id) throws Exception {
        if (id != null && id > 0) {
            //1.获取config
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //2.获取对应的模板
            Template template = configuration.getTemplate("item.ftl");
            //3.创建数据封装包
            Map dataSource = new HashMap();
            //3.1.1封装商品基本信息
            Goods goods = goodsDao.selectByPrimaryKey(id);
            dataSource.put("goods", goods);
            //3.1.2封装
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
            dataSource.put("goodsDesc", goodsDesc);

            //3.2商品类型面包屑的生成
            String itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName();

            dataSource.put("itemCat1", itemCat1);
            dataSource.put("itemCat2", itemCat2);
            dataSource.put("itemCat3", itemCat3);

            //3.3读取SKU信息
            ItemQuery query = new ItemQuery();
            ItemQuery.Criteria criteria = query.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            //按默认排序
            query.setOrderByClause("is_default");
            List<Item> itemList = itemDao.selectByExample(query);
            dataSource.put("items" , itemList);
            //创建输出流,解决页面乱码，通过设置流的编码
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(pagedir + id + ".html")), "UTF-8"));
            Writer out2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(serverdir + id + ".html")), "UTF-8"));
            //基于模板的文本输出
            template.process(dataSource, out);
            template.process(dataSource, out2);
            //关闭流资源
            out.close();
            out2.close();
            return true;
        }
        return false;
    }

    /**
     * 删除对应商品的页面
     * @param id
     */
    @Override
    public void deletePage(Long id){
        if(id!= null && id > 0){
            new File(pagedir + id + ".html").delete();
            new File(serverdir + id + ".html").delete();
        }
    }
}
