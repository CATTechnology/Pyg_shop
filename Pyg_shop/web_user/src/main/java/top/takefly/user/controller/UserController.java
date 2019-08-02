package top.takefly.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.takefly.core.pojo.entity.Result;
import top.takefly.core.pojo.user.User;
import top.takefly.core.service.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: Pyg_shop
 * @description: 用户注册
 * @author: 戴灵飞
 * @create: 2019-06-29 11:58
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/add")
    public Result add(@RequestBody User user , String code){
        try{
            BoundHashOperations registerCode = redisTemplate.boundHashOps("registerCode");
            String redisRegisterCode = (String) registerCode.get(user.getPhone());
            if(code == null || "".equals(code) || !code.equals(redisRegisterCode)){
                return new Result(true , "验证码有误!!!!");
            }
            user.setCreated(new Date());
            user.setUpdated(new Date());
            user.setStatus("1");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println("用户"+user.getUsername()+"的密码:"+user.getPassword());
            userService.add(user);

            return new Result(true , "注册成功..");
        }catch (Exception e){
            return new Result(true , "注册失败..");
        }
    }

    @RequestMapping("/sendRegisterCode")
    public void sendRegisterCode(@RequestBody String phone){
        userService.sendRegisterCode(phone);
    }

    @RequestMapping("/gerLoginName")
    public Map gerLoginName(){
        Map map = new HashMap();
        map.put("username" , SecurityContextHolder.getContext().getAuthentication().getName());
        return map;
    }
}
