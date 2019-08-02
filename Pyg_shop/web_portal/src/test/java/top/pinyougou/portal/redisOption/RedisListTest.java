package top.pinyougou.portal.redisOption;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-redis.xml")
public class RedisListTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Before
    public void setValue(){
        redisTemplate.delete("myList");
        BoundListOperations myList = redisTemplate.boundListOps("myList");
        StringBuilder hello_1 = new StringBuilder("JAVA");
        StringBuilder hello_2 = new StringBuilder("MATH");
        for(int i = 0 ; i < 10 ; i++){
            hello_1.replace(hello_1.lastIndexOf("A") +1, hello_1.length() , new Integer(i).toString());
            hello_2.replace(hello_2.lastIndexOf("H") +1, hello_2.length() , new Integer(i).toString());
            myList.leftPush(hello_1.toString());
            myList.rightPush(hello_2.toString());
        }
    }

    @Test
    public void getValue(){
        List myList = redisTemplate.boundListOps("myList").range(0, -1);
        Random random = new Random();
        int[] arrs = new int[100000000];
        for(int i = 0 ; i < 100000000 ; i ++){
            int value = random.nextInt(10000000);
            arrs[i] = value;
        }
        Arrays.sort(arrs);
        for(Object i:arrs){
            System.out.print(i+"\t");
        }
        System.out.println(arrs.length);
        //System.out.println(myList);
    }
}
