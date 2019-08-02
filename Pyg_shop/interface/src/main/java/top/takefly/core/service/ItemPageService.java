package top.takefly.core.service;

public interface ItemPageService {
    /**
     * 根据商品id来创建订单详情页
     * @param id
     */
    public boolean genItemHtml(Long id) throws Exception;

    /**
     * 删除商品详情页
     * @param id
     */
    public void deletePage(Long id);
}
