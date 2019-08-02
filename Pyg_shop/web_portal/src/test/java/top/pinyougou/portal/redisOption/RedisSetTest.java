package top.pinyougou.portal.redisOption;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-redis.xml")
public class RedisSetTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Before
    public void setValue(){
        redisTemplate.delete("mySet1");
        redisTemplate.delete("mySet2");
        redisTemplate.delete("mySet3");
        BoundSetOperations mySet1 = redisTemplate.boundSetOps("mySet1");
        BoundSetOperations mySet2 = redisTemplate.boundSetOps("mySet2");
        BoundSetOperations mySet3 = redisTemplate.boundSetOps("mySet3");
        mySet1.add(1, "2" , 3 , 4 , "我爱你" , 200D , 1000L , 22.3);
        mySet2.add(1, "2" , 3 , 4 );

    }

    @Test
    public void getValue(){
        BoundSetOperations mySet1 = redisTemplate.boundSetOps("mySet1");
        BoundSetOperations mySet2 = redisTemplate.boundSetOps("mySet2");
        BoundSetOperations mySet3 = redisTemplate.boundSetOps("mySet3");
        System.out.println(mySet1.members());
        System.out.println(mySet2.members());
        System.out.println(mySet1.isMember(2));
        System.out.println(mySet1.isMember("2"));
        System.out.println(mySet1.isMember(3));
    }
}
