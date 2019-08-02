package top.takefly.data.redis;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class RedisDemo2 {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void init(){
        BoundValueOperations sequence = redisTemplate.boundValueOps("sequence");
        sequence.set(0);
        BoundHashOperations myHash = redisTemplate.boundHashOps("myHash");
        myHash.put("primaryKey" , 1);
    }

    @Before
    public void incrValue(){
        //修改redisTemplate
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));;
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        BoundValueOperations sequence = redisTemplate.boundValueOps("sequence");
        BoundHashOperations myHash = redisTemplate.boundHashOps("myHash");
        myHash.increment("primaryKey" , 1);
        //每次增长一个值
        sequence.increment(1);
    }

    @Test
    public void getValue(){
        BoundValueOperations sequence = redisTemplate.boundValueOps("sequence");
        System.out.println("sequence:"+sequence.get());

        BoundHashOperations myHash = redisTemplate.boundHashOps("myHash");
        Object primaryKey = myHash.get("primaryKey");
        System.out.println("primaryKey:"+primaryKey);
    }


}
