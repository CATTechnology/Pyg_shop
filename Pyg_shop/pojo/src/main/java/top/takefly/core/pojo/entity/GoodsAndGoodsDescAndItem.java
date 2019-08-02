package top.takefly.core.pojo.entity;

import top.takefly.core.pojo.good.Goods;
import top.takefly.core.pojo.good.GoodsDesc;
import top.takefly.core.pojo.item.Item;
import top.takefly.core.pojo.item.ItemCat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GoodsAndGoodsDescAndItem implements Serializable {

    private Goods goods;
    private GoodsDesc goodsDesc;
    private List<Item> itemList = new ArrayList<Item>();

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "GoodsAndGoodsDescAndItem{" +
                "goods=" + goods +
                ", goodsDesc=" + goodsDesc +
                ", itemList=" + itemList +
                '}';
    }
}
