package top.takefly;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pojo.utils.FastDFSClient;
import top.takefly.core.dao.item.ItemDao;
import top.takefly.core.pojo.item.Item;
import top.takefly.core.pojo.item.ItemQuery;

import java.io.File;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class UploadImage {

    FastDFSClient fastDFSClient = null;

    @Autowired
    private ItemDao itemDao;

    @Before
    public void init() throws Exception{
        fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
    }

    @Test
    public void upload() throws Exception {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andIdBetween(1369424L, 1370369L);
        List<Item> items = itemDao.selectByExample(query);
        String baseUrl ="D:";
        String serverUrl = "http://119.23.64.69/";
        for(Item item:items){
            String image = item.getImage();
            String extName = image.substring(image.lastIndexOf("."));
            String url = baseUrl+image;
            if(!new File(url).exists()){
                url = "D:/img/0c0a0436593659ffcb231b068a231933.jpg";
            }
            String realImage = serverUrl + fastDFSClient.uploadFile(url, extName);
            System.out.println(realImage);
            item.setImage(realImage);
            itemDao.updateByPrimaryKey(item);
        }

        //将该修改后的存入数据库中
    }
    
}
