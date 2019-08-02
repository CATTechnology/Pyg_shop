package top.takefly.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.specification.SpecificationDao;
import top.takefly.core.dao.specification.SpecificationOptionDao;
import top.takefly.core.pojo.entity.AddSpecification;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.specification.Specification;
import top.takefly.core.pojo.specification.SpecificationOption;
import top.takefly.core.pojo.specification.SpecificationOptionQuery;
import top.takefly.core.pojo.specification.SpecificationQuery;
import top.takefly.core.service.SpecificationService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    /**
     * 查询全部
     */
    @Override
    public List<Specification> findAll() {
        return specificationDao.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Specification> page = (Page<Specification>) specificationDao.selectByExample(null);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(AddSpecification addSpecification) {
        Specification specification = addSpecification.getSpecification();
        System.out.println(specification);
        specificationDao.insertSelective(specification);
        //specificationDao.selec

        //获取id
        Long id = specification.getId();
        //获取相关联的规格选项
        List<SpecificationOption> specificationOptions = addSpecification.getSpecificationOptions();
        for (SpecificationOption specificationOption : specificationOptions) {
            specificationOption.setSpecId(id);
            specificationOptionDao.insert(specificationOption);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(AddSpecification addSpecification) {
        //首先更新specification表
        specificationDao.updateByPrimaryKey(addSpecification.getSpecification());

        //删除链接表的里面数据，然后再填入
        //1.先删除
        SpecificationOptionQuery optionQuery = null;
        Long id = addSpecification.getSpecification().getId();
        if (id != null) {
            optionQuery = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(optionQuery);
            //2.填入
            List<SpecificationOption> specificationOptions = addSpecification.getSpecificationOptions();
            if(specificationOptions.size() > 0){
                for(SpecificationOption specificationOption:specificationOptions){
                    specificationOption.setSpecId(id);
                    specificationOptionDao.insertSelective(specificationOption);
                }
            }
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public AddSpecification findOne(Long id) {
        //首先找到当前的规格
        Specification specification = specificationDao.selectByPrimaryKey(id);

        //查询相应的规格参数
        SpecificationOptionQuery optionQuery = null;
        if (id != null) {
            optionQuery = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
            criteria.andSpecIdEqualTo(id);
        }
        List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(optionQuery);
        return new AddSpecification(specification, specificationOptions);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationDao.deleteByPrimaryKey(id);
            //同时删除相关联的规格参数
            SpecificationOptionQuery example = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);

            specificationOptionDao.deleteByExample(example);
        }
    }


    @Override
    public PageResult findPage(Specification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        SpecificationQuery example = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<Specification> page = (Page<Specification>) specificationDao.selectByExample(example);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectSpecificationToTypeTemplate() {
        return specificationDao.selectSpecificationToTypeTemplate();
    }

}
