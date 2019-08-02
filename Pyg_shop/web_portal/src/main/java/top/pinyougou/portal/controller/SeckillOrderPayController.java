package top.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojo.utils.IdWorker;
import top.takefly.core.pojo.entity.Result;
import top.takefly.core.pojo.seckill.SeckillOrder;
import top.takefly.core.service.SeckillOrderService;
import top.takefly.core.service.WeiXinPayService;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: Pyg_shop
 * @description: 秒杀支付控制器
 * @author: 戴灵飞
 * @create: 2019-07-21 19:41
 **/
@RequestMapping("/seckillOrderPay")
@RestController
public class SeckillOrderPayController {

    @Reference
    private WeiXinPayService weiXinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @Autowired
    private IdWorker idWorker;

    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前的用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SeckillOrder seckillOrder = seckillOrderService.findOrderFromRedis(username);
        if(seckillOrder != null){
            return weiXinPayService.createNative(seckillOrder.getId()+"", "1");
        }else{
            return new HashMap();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = null;
        int times = 0;
        try {
            while (true) {
                //首先要查一次
                Map<String,String> map = weiXinPayService.queryPayStatus(out_trade_no);
                //判断是否抛出异常
                if (map == null) {
                    result = new Result(false, "支付失败!!!");
                    break;
                }
                //判断是否支付成功
                if ("SUCCESS".equals(map.get("trade_state"))) {
                    result = new Result(true, "支付成功");
                    //调用订单服务更新订单状态
                    seckillOrderService.saveOrderFromRedis(userId ,Long.parseLong(out_trade_no), map.get("transaction_id"));
                    break;
                }
                //暂停3秒
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //如果5分钟之内未支付就跳出循环
                times++;
                if (times == 100) {
                    result = new Result(false, "支付超时");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
