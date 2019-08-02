package top.takefly.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import top.takefly.core.pojo.good.Brand;
import top.takefly.core.pojo.item.Item;
import top.takefly.core.pojo.specification.SpecificationOption;
import top.takefly.core.service.ItemSearchService;

import java.util.*;


@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map search(Map searchMap) {
        Map resultMap = new HashMap<>(16);
        //设置关键字高亮显示
        resultMap.putAll(getHighlighyList(searchMap));

        //商品分组查询
        List categoryList = getCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);

        //从redis缓存中读取品牌和规格数据
        //首先判断当前是否选中商品分类，默认为第一个
        Map bandAndSpecMap = getBrandList(searchMap, categoryList);
        resultMap.putAll(bandAndSpecMap);

        return resultMap;
    }

    @Override
    public void addItemToSolr(List<Item> itemList) {
        if(itemList.size()>0){
            //动态域配置
            for(Item item:itemList){
                item.setSpecMap(JSON.parseObject(item.getSpec() , Map.class));
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }
    }

    @Override
    public void BatchDeleteById(Long[] ids) {
        Query query =new SimpleQuery("*:*");
        Criteria criteria = new Criteria("id").in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 获取品牌
     *
     * @param searchMap
     * @return
     */
    private Map getBrandList(Map searchMap, List categoryList) {
        Map<String, Object> map = new HashMap<>(16);
        //首先判断是否搜索中包含分类
        String category = (String) searchMap.get("category");
        if (category.length() != 0 && !"".equals(category)) {
            map = getBrandAndSpecMap(category);
            map.put("category", category);
        } else {
            if (categoryList.size() > 0) {
                String o = (String) categoryList.get(0);
                //将选中的category赋值到resultMap里面
                map = getBrandAndSpecMap(o);
                map.put("category", o);
            } else {
                System.out.println("居然没有分类，不敢相信！！！");
            }
        }

        return map;
    }

    private Map getBrandAndSpecMap(String category) {
        Map<String, Object> map = new HashMap<>(16);
        //先查出模板id
        BoundHashOperations typeTemplates = redisTemplate.boundHashOps("typeTemplates");
        Long typeTemplateId = (Long) typeTemplates.get(category);

        //根据模板id来获取品牌和规格
        List<Brand> brandList = (List<Brand>) redisTemplate.boundHashOps("brandList").get(typeTemplateId);
        map.put("brandList", brandList);

        //根据模板id获取规格数据
        List<SpecificationOption> specList = (List<SpecificationOption>) redisTemplate.boundHashOps("specList").get(typeTemplateId);
        map.put("specList", specList);

        return map;
    }

    /**
     * 获取分类列表
     *
     * @param searchMap
     * @return
     */
    private List getCategoryList(Map searchMap) {
        //分组查询
        Query query = new SimpleQuery();
        GroupOptions options = new GroupOptions();
        options.addGroupByField("item_category");
        query.setGroupOptions(options);
        String keywords = (String) searchMap.get("keywords");
        if (keywords.length() > 0 || !"".equals(keywords)) {
            //去除keywords里面所有的空格
            Criteria criteria = new Criteria("item_keywords").is(((String) searchMap.get("keywords")).replaceAll(" " , ""));
            query.addCriteria(criteria);
        }

        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        GroupResult<Item> itemCategory = items.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = itemCategory.getGroupEntries();
        List<GroupEntry<Item>> contents = groupEntries.getContent();
        List<String> categoryList = new ArrayList<>();
        for (GroupEntry<Item> groupEntry : contents) {
            String groupValue = groupEntry.getGroupValue();
            System.out.println(groupValue);
            categoryList.add(groupValue);
        }

        return categoryList;
    }

    /**
     *
     * 高亮显示关键字
     * @param searchMap
     * @return
     */
    private Map getHighlighyList(Map searchMap) {
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮显示的行
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");

        query.setHighlightOptions(highlightOptions);

        //1.1按关键字查询
        String keywords = (String) searchMap.get("keywords");
        if (!"".equals(keywords) && keywords.length() != 0) {
            Criteria criteria = new Criteria("item_keywords").is(((String) searchMap.get("keywords")).replaceAll(" ", ""));
            query.addCriteria(criteria);
        }

        //1.2分类过滤查询
        String category = (String) searchMap.get("category");
        if (!"".equals(category) && category.length() != 0) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria = new Criteria("item_category").is(category);
            filterQuery.addCriteria(criteria);
            query.addFilterQuery(filterQuery);
        }

        //1.3品牌过滤查询
        String brand = (String) searchMap.get("brand");
        if (!"".equals(brand) && brand.length() != 0) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria = new Criteria("item_brand").is(brand);
            filterQuery.addCriteria(criteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4規格过滤查询
        Map<String, String> specOps = (Map<String, String>) searchMap.get("specOps");
        if (specOps != null && specOps.size() != 0) {
            for(String key:specOps.keySet()){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria = new Criteria("item_spec_"+key).is(specOps.get(key));
                filterQuery.addCriteria(criteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6按价格区间过滤
        String startPrice = (String) searchMap.get("startPrice");
        String endPrice = (String) searchMap.get("endPrice");
        if(startPrice != null && !"".equals(startPrice) && endPrice!=null && !"".equals(endPrice)){
            String start = "0";
            String end = "~";
            if(!start.equals(startPrice)){
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(startPrice);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

            if(!end.equals(endPrice)){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(endPrice);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.7排序
        String sort = (String) searchMap.get("sort");
        String sortDirection  = (String) searchMap.get("sortDirection");
        if(sort!= null && sortDirection!=null && !"".equals(sort) && !"".equals(sortDirection)){
            Sort sortFilter = null;
            if(sortDirection.equals("asc")){
                sortFilter = new Sort(Sort.Direction.ASC , "item_"+sort);
            }else{
                sortFilter = new Sort(Sort.Direction.DESC , "item_"+sort);
            }
            query.addSort(sortFilter);
        }

        //1.8设置分页
        Integer currentPage = (Integer) searchMap.get("currentPage");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        Integer zero = 0;
        if(currentPage == null || zero.equals(currentPage)){
            currentPage = 1;
        }
        if(pageSize == null || zero.equals(pageSize)){
            pageSize = 40;
        }
        query.setOffset((currentPage -1)*pageSize);
        query.setRows(pageSize);

        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        //1.5获取查询到的高亮结果
        /**
         * 获取高亮显示的行比较难
         */
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        for (HighlightEntry<Item> highlightEntry : highlighted) {
            //获取操作的商品
            Item item = highlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
                //设置高亮显示的行
                item.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        HashMap hashMap = new HashMap(16);

        List<Item> content = items.getContent();
        int totalPages = items.getTotalPages();
        hashMap.put("totalPage" , totalPages);
        hashMap.put("total" , items.getTotalElements());
        hashMap.put("rows", content);

        return hashMap;
    }




}
