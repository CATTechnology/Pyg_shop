package top.takefly.core.service;

import top.takefly.core.pojo.entity.AddSpecification;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface SpecificationService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<Specification> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(AddSpecification addSpecification);


    /**
     * 修改
     */
    public void update(AddSpecification addSpecification);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public AddSpecification findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(Specification specification, int pageNum, int pageSize);
    /**
     * 响应模板的下拉列表的数据支撑
     * @return
     */
    public List<Map> selectSpecificationToTypeTemplate();
}
