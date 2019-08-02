package top.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojo.utils.CookieUtil;
import top.takefly.core.pojo.address.Address;
import top.takefly.core.pojo.entity.Cart;
import top.takefly.core.pojo.entity.Result;
import top.takefly.core.pojo.user.User;
import top.takefly.core.service.CartService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: Pyg_shop
 * @description: 购物车控制层
 * @author: 戴灵飞
 * @create: 2019-07-06 17:22
 **/
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;


    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListStr == null || "".equals(cartListStr)) {
            cartListStr = "[]";
        }
        List<Cart> cookie_cartList = JSON.parseArray(cartListStr, Cart.class);
        if (name != null && "anonymousUser".equals(name)) {
            System.out.println("从cookie获取购物车信息");
            return cookie_cartList;
        } else {
            System.out.println("从redis获取购物车信息");
            List<Cart> redis_cartList = cartService.findCartListFromRedis(name);
            //首先判断cooKie是否为空
            if (cookie_cartList != null && cookie_cartList.size() > 0) {
                //将cookie中的购物车信息合并，并且清空
                List<Cart> carts = cartService.mergeCartList(cookie_cartList, redis_cartList);
                CookieUtil.deleteCookie(request, response, "cartList");
                return carts;
            }

            return redis_cartList;
        }
    }

    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8089/");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //如果是用户登录之后
            //将之前的cookie中的信息和现在的redis中的信息合并
            List<Cart> cartList = findCartList();
            List<Cart> newCartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (name != null && "anonymousUser".equals(name)) {
                //将数据再次存入cookie
                String cartListStr = JSON.toJSONString(newCartList);
                CookieUtil.setCookie(request, response, "cartList", cartListStr, 3600 * 24 , "UTF-8");
            } else {
                //保存到redis里面
                cartService.saveCartListToRedis(name, cartList);
            }
            return new Result(true, "添加成功....");
        } catch (Exception e) {
            return new Result(true, "添加失败!!!!");
        }
    }

    @RequestMapping("/findUserListByUserId")
    public List<Address> findUserListByUserId() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Address> userList = cartService.findUserListByUserId(name);

        return userList;
    }

    @RequestMapping("/add.do")
    public Result add(@RequestBody Address address) {
        try {
            String user_id = SecurityContextHolder.getContext().getAuthentication().getName();
            address.setUserId(user_id);
            cartService.add(address);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败！！！");
        }
    }

}
