package top.takefly.core.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import top.takefly.core.dao.good.BrandDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.good.Brand;
import top.takefly.core.pojo.good.BrandQuery;
import top.takefly.core.service.BrandService;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;

    @Override
    public Brand findBrandById(Long id) {

        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    /**
     * 分页查询
     *
     * @param page 当前页码
     * @param size 每页的条数
     * @return
     */
    @Override
    public PageResult findPage(Integer page, Integer size) {
        //创建分页结果集封装对象

        List<Brand> totalBrands = brandDao.selectByExample(null);

        PageHelper.startPage(page, size);
        List<Brand> brands = brandDao.selectByExample(null);
        //封装结果集
        PageResult pageResult = new PageResult(totalBrands.size(), brands);
        return pageResult;
    }

    @Override
    public void save(Brand brand) {
        //这个插入数据的方式是非空才保存，如果为空不保存
        if (brand != null) {
            brandDao.insertSelective(brand);
        }
    }

    @Override
    public Brand findOne(Long id) {
        Brand brand = brandDao.selectByPrimaryKey(id);
        return brand;
    }

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delDetach(Long[] ids) {
        if (ids.length > 0) {
            for (Long id : ids) {
                brandDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public PageResult searchBrand(Brand brand, Integer page, Integer size) {
        //设置查询参数
        BrandQuery brandQuery = brandQuery = new BrandQuery();;
        if (brand != null) {
            BrandQuery.Criteria criteria = brandQuery.createCriteria();
            if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
        }
        //查询总品牌数
        List<Brand> totalBrands = brandDao.selectByExample(brandQuery);
        //分页
        PageHelper.startPage(page, size);
        List<Brand> brands = brandDao.selectByExample(brandQuery);
        return new PageResult(totalBrands.size(), brands);
    }

    @Override
    public List<Map> selectBrandToTypeTemplate() {
        List<Map> maps = brandDao.selectBrandToTypeTemplate();
        return maps;
    }


}
