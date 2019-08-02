package top.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.takefly.core.pojo.entity.Result;
import top.takefly.core.service.SeckillOrderService;

/**
 * @program: Pyg_shop
 * @description: 秒杀订单操作控制器
 * @author: 戴灵飞
 * @create: 2019-07-21 17:41
 **/
@RequestMapping("/seckillOrder")
@RestController
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //判断此时的用户名是否为anonymously
            if("ANONYMOUSLY".equalsIgnoreCase(username)){
                return new Result(false , "用户没登录....");
            }
            seckillOrderService.submitOrder(seckillId, username);
            return new Result(true , "订单生成成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false , "订单生成失败.....,原因:"+e.getMessage());
        }
    }
}
