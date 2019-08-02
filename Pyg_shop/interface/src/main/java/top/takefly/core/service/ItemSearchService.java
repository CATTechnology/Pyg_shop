package top.takefly.core.service;

import top.takefly.core.pojo.item.Item;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 根据前端条件查询
     *
     */
    public Map search(Map searchMap);

    /**
     * 向solr中存入对象
     */
    public void addItemToSolr(List<Item> itemList);


    /**
     * 根据id批量删除
     * @param id
     */
    public void BatchDeleteById(Long[] id);
}
