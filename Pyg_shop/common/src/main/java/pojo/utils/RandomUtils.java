package pojo.utils;

import java.util.Random;

/**
 * @program: Pyg_shop
 * @description: 生成验证码的
 * @author: 戴灵飞
 * @create: 2019-06-29 15:50
 **/
public class RandomUtils {

    /**
     * 生成随机数
     * @return
     */
    public static String genRn() {
        Random r = new Random();
        String rn = "";
        for(int i=0;i<6;i++){
            rn+=r.nextInt(10);
        }
        return rn;
    }
}
