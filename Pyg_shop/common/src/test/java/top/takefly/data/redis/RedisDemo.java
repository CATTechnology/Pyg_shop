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
public class RedisDemo {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GenericToStringSerializer valueSerializer;

    @Autowired
    private StringRedisSerializer keySerializer;

    @Test
    public void initHashValue() {
        BoundHashOperations primaryKey = redisTemplate.boundHashOps("primaryHashKey");
        primaryKey.put("incr", new Long(1));
    }

    @Test
    public void initValue() {
        BoundValueOperations primaryKey = redisTemplate.boundValueOps("primaryValueKey");
        primaryKey.set(1L);

    }

    @Test
    public void deleteKey(){
        redisTemplate.delete("primaryKey");
    }

    //@Before
    public void incrValue() {
        //修改redisTemplate
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();

        BoundHashOperations primaryKey = redisTemplate.boundHashOps("primaryKey");
        //每次增长一个值
        Long incr = primaryKey.increment("incr", 1);
        System.out.println(incr);
    }

    @Test
    public void getHashValue() {
        BoundHashOperations primaryKey = redisTemplate.boundHashOps("primaryHashKey");

        for (int i = 0; i < 10; i++) {
            primaryKey.increment("incr", 1);
            //每次增长一个值
            Object incr = primaryKey.get("incr");
            System.out.println(incr);
        }
    }

    @Test
    public void getValueTest(){
        BoundValueOperations primaryKey = redisTemplate.boundValueOps("primaryValueKey");
        for (int i = 0; i < 10; i++) {
            Long increment = primaryKey.increment(1L);
            System.out.println(increment);
        }
    }



}
