package top.takefly.core.service;

import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.entity.Result;
import top.takefly.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    public Brand findBrandById(Long id);

    /**
     * 查询所有品牌信息
     * @return
     */
    public List<Brand> findAll();

    /**
     * 分页显示品牌信息
     * @param page
     * @param size
     * @return
     */
    public PageResult findPage(Integer page, Integer size);

    /**
     * 保存品牌信息
     * @param brand
     */
    public void save(Brand brand);

    public Brand findOne(Long id);

    /**
     * 修改品牌信息
     * @param brand
     */
    public void update(Brand brand);

    public void delDetach(Long[] ids);

    public PageResult searchBrand(Brand brand, Integer page, Integer size);
    /**
     * 响应模板的下拉列表的数据支撑
     *
     * @return
     */
    public List<Map> selectBrandToTypeTemplate();
}
