package top.takefly.core.pojo.entity;

import top.takefly.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @program: Pyg_shop
 * @description: 购物车信息
 * @author: 戴灵飞
 * @create: 2019-07-06 17:02
 **/
public class Cart implements Serializable {
    private String sellerId;
    private String sellerName;
    private List<OrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
