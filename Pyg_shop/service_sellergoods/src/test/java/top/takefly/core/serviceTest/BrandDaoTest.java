package top.takefly.core.serviceTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.takefly.core.dao.good.BrandDao;
import top.takefly.core.pojo.good.Brand;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class BrandDaoTest {

    @Autowired
    private BrandDao brandDao;

    @Test
    public void findBrandById() {
        Brand brand = brandDao.selectByPrimaryKey(1L);
        System.out.println(brand);
    }

    @Test
    public void findAll(){
        List<Brand> brands = brandDao.selectByExample(null);
        System.out.println(brands);
    }
}
