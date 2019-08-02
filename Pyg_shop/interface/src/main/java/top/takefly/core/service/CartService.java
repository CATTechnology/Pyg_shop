package top.takefly.core.service;

import top.takefly.core.pojo.address.Address;
import top.takefly.core.pojo.entity.Cart;
import top.takefly.core.pojo.user.User;

import java.util.List;

/**
 * 购物车服务
 */
public interface CartService {

    /**
     * 添加商品到购物车列表
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num );

    /**
     * 从redis中读取购物车
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车信息保存到redis中
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username , List<Cart> cartList);

    /**
     * 将两个购物车合并为一个购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1 , List<Cart> cartList2);

    /**
     * 通过userId来查找用户信息
     * @param name
     * @return
     */
    public List<Address> findUserListByUserId(String name);

    public void add(Address address);
}
