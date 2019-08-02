package top.pinyougou.portal.redisOption;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-redis.xml")
public class RedisHashTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Before
    public void setValue(){
        redisTemplate.delete("myHash");
        BoundHashOperations myHash = redisTemplate.boundHashOps("myHash");
        myHash.put("name" , "zhangsan");
        myHash.put("age" ,  "20");
        HashMap<String, String> map = new HashMap<>();
        map.put("stuNum" , "16020240233");
        map.put("ID" , "123456789");
        myHash.putAll(map);
    }

    @Test
    public void getValue(){
        BoundHashOperations myHash = redisTemplate.boundHashOps("myHash");
        Object name = myHash.get("name");
        Object age = myHash.get("age");
        ArrayList<String> arr = new ArrayList<>();
        arr.add("stuNum");
        arr.add("ID");
        List list = myHash.multiGet(arr);
        myHash.put("" , "nullValue");
        System.out.println(name);
        System.out.println(age);
        System.out.println("-------------------");
        System.out.println(list);
        System.out.println(myHash.get(""));

        System.out.println(myHash.size());
    }
}
