package top.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/getName")
    public Map getName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String , String> nameMap = new HashMap<String , String>();
        nameMap.put("name" , name);
        return nameMap;
    }
}
