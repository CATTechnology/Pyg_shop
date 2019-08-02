package top.takefly.buyer.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import top.takefly.core.dao.address.AddressDao;
import top.takefly.core.dao.item.ItemDao;
import top.takefly.core.pojo.address.Address;
import top.takefly.core.pojo.address.AddressQuery;
import top.takefly.core.pojo.entity.Cart;
import top.takefly.core.pojo.item.Item;
import top.takefly.core.pojo.order.OrderItem;
import top.takefly.core.service.CartService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: Pyg_shop
 * @description: 实现cartService用于实现购物车的商品计算逻辑
 * @author: 戴灵飞
 * @create: 2019-07-06 17:20
 **/
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1. 根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //2. 判断商品是否存在不存在, 抛异常
        if (item == null) {
            throw new RuntimeException("商品不存在!!!!");
        }
        //3. 判断商品状态是否为1已审核, 状态不对抛异常
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品已下架!!!");
        }
        //4.获取商家ID
        String sellerId = item.getSellerId();
        //5.根据商家ID查询购物车列表中是否存在该商家的购物车
        Cart cart = searchCartInCartList(cartList, sellerId);
        //6.判断如果购物车列表中不存在该商家的购物车
        if (cart == null) {
            //6.a.1 新建购物车对象
            cart = createCart(item, num);
            //6.a.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);
        } else {
            //6.b.1如果购物车列表中存在该商家的购物车 (查询购物车明细列表中是否存在该商品)
            OrderItem orderItem = searchOrderItem(cart.getOrderItemList(), item);
            //6.b.2判断购物车明细是否为空
            if (orderItem == null) {
                //6.b.3为空，新增购物车明细
                OrderItem newOrderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(newOrderItem);
            }else{
                //6.b.4不为空，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                BigDecimal decimal = new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum());
                orderItem.setTotalFee(decimal);
                //6.b.5如果购物车明细中数量操作后小于等于0，则移除
                if(orderItem.getNum() <= 0 ){
                    cart.getOrderItemList().remove(orderItem);
                }

                //6.b.6如果购物车中购物车明细列表为空,则移除
                if(cart.getOrderItemList().size() < 0){
                    cartList.remove(cart);
                }
            }
        }
        //7. 返回购物车列表对象
        return cartList;
    }

    /**
     * 返回购物车对象
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        BoundHashOperations cartListOps = redisTemplate.boundHashOps("CartList");
        List<Cart> cartList = (List<Cart>) cartListOps.get(username);

        if(cartList == null){
            cartList = new ArrayList<>();
        }

        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        //将商品信息和并
        BoundHashOperations hashCart = redisTemplate.boundHashOps("CartList");
        //将cartList转换为字符串
        if(cartList==null){
            cartList = new ArrayList<>();
        }
        hashCart.put(username , cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        //遍历一个集合
        for(Cart cart:cartList2){
            for(OrderItem orderItem:cart.getOrderItemList()){
                cartList1 = addGoodsToCartList(cartList1 , orderItem.getItemId() , orderItem.getNum());
            }
        }
        return cartList1;
    }

    @Override
    public List<Address> findUserListByUserId(String name) {
        AddressQuery query = new AddressQuery();
        query.setOrderByClause("is_default desc");
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(name);
        List<Address> addresses = addressDao.selectByExample(query);

        return addresses;
    }

    @Override
    public void add(Address address) {
        addressDao.insertSelective(address);
    }

    private OrderItem searchOrderItem(List<OrderItem> orderItemList, Item item) {
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().equals(item.getId())) {
                return orderItem;
            }
        }
        return null;
    }

    private Cart createCart(Item item, Integer num) {
        Cart cart = new Cart();
        cart.setSellerName(item.getSeller());
        cart.setSellerId(item.getSellerId());
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = createOrderItem(item, num);
        orderItemList.add(orderItem);
        cart.setOrderItemList(orderItemList);
        return cart;
    }

    private OrderItem createOrderItem(Item item, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());

        return orderItem;
    }

    private Cart searchCartInCartList(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

}
