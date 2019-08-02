package top.takefly.pinyougou.SolrUtil;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;
import top.takefly.core.dao.item.ItemDao;
import top.takefly.core.pojo.item.Item;
import top.takefly.core.pojo.item.ItemQuery;

import java.util.List;
import java.util.Map;

@Component("AddItemToSolr")
public class AddItemToSolr {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private SolrTemplate solrTemplate;

    public void batchAddItemToSolr(){
        //只有审核通过的商品才能上架
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");

        List<Item> items = itemDao.selectByExample(query);

        System.out.println("===========商品列表======");
        for(Item item:items){
            item.setSpecMap(JSON.parseObject(item.getSpec() , Map.class));
            System.out.println(item.getTitle());
        }
        //转换一下动态域
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }

    public void clearSolr(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        AddItemToSolr addItemToSolr = (AddItemToSolr) context.getBean("AddItemToSolr");
        addItemToSolr.batchAddItemToSolr();
        //addItemToSolr.clearSolr();
    }
}
