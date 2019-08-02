package top.pinyougou.portal.redisOption;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-redis.xml")
public class RedisValueTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Before
    public void setValue(){
        redisTemplate.boundValueOps("name").set("zhangsan");
    }

    @Test
    public void getValue(){
        BoundValueOperations valueOption = redisTemplate.boundValueOps("name");
        Object o = valueOption.get();
        System.out.println(o);

        //添加值
        valueOption.append("--age:20");
        Object o2 = valueOption.get();
        System.out.println(o2);

        //删除值
        redisTemplate.delete("name");
        Object o3 = valueOption.get();
        System.out.println(o3);

        //
    }

    @Test
    public void demo2(){
        redisTemplate.delete("str");
        BoundValueOperations option = redisTemplate.boundValueOps("str");
        option.append("a");
        option.append("b");
        option.append("c");
        option.append("d");
        //Object str = redisTemplate.boundValueOps("str").get( 0 , 3);
        //System.out.println(str);
        System.out.println(option.get(0,3));
        System.out.println(option.get());
        //System.out.println(option.get(0 , 2));
    }

    @Test
    public void demo3(){
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps("bvo");
        boundValueOperations.append("a");
        boundValueOperations.append("b");
        System.out.println("获取从指定位置开始，到指定位置为止的值:" + boundValueOperations.get(0,2));
    }

}
