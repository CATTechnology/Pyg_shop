package top.takefly.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import top.takefly.core.dao.good.BrandDao;
import top.takefly.core.dao.specification.SpecificationOptionDao;
import top.takefly.core.dao.template.TypeTemplateDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.good.Brand;
import top.takefly.core.pojo.specification.Specification;
import top.takefly.core.pojo.specification.SpecificationOption;
import top.takefly.core.pojo.specification.SpecificationOptionQuery;
import top.takefly.core.pojo.template.TypeTemplate;
import top.takefly.core.pojo.template.TypeTemplateQuery;
import top.takefly.core.service.TypeTemplateService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BrandDao brandDao;

    /**
     * 查询全部
     */
    @Override
    public List<TypeTemplate> findAll() {
        return typeTemplateDao.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TypeTemplate> page = (Page<TypeTemplate>) typeTemplateDao.selectByExample(null);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TypeTemplate typeTemplate) {
        TypeTemplate oldTypeTemplate = typeTemplateDao.selectByPrimaryKey(typeTemplate.getId());

        BoundHashOperations typeTemplates = redisTemplate.boundHashOps("typeTemplates");
        typeTemplates.delete(oldTypeTemplate.getName());
        
        typeTemplates.put(typeTemplate.getName() , typeTemplate.getId());

        typeTemplateDao.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //清除在redis中的缓存
            TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
            BoundHashOperations typeTemplates = redisTemplate.boundHashOps("typeTemplates");
            typeTemplates.delete(typeTemplate.getName());

            typeTemplateDao.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TypeTemplate typeTemplate, int pageNum, int pageSize) {
        //将数据缓存到redis
        cacheBrandAndSpec();

        PageHelper.startPage(pageNum, pageSize);

        TypeTemplateQuery example = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }

        Page<TypeTemplate> page = (Page<TypeTemplate>) typeTemplateDao.selectByExample(example);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    /**
     * 通过模板id,缓存品牌和规格
     */



    public void cacheBrandAndSpec(){
        //首先查询所有的模板
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        //将模板缓存
        BoundHashOperations redisTypeTemplate = redisTemplate.boundHashOps("typeTemplates");
        for(TypeTemplate typeTemplate:typeTemplates){
            //缓存模板
            redisTypeTemplate.put(typeTemplate.getName() , typeTemplate.getId());
            //缓存品牌
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId() , brandList);
            //缓存规格
            cacheSpecList(typeTemplate);
        }
    }

    /**
     * 缓存品牌
     * @param typeTemplate
     */
    private void cacheSpecList(TypeTemplate typeTemplate){
        List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        for(Map map:specList){
            Integer id = (Integer) map.get("id");
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = query.createCriteria();
            criteria.andSpecIdEqualTo(id.longValue());
            List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(query);
            map.put("specOps" ,specificationOptions);
        }

        //缓存
        redisTemplate.boundHashOps("specList").put(typeTemplate.getId() , specList);
    }

    /**
     * 缓存规格
     * 此方法过时
     * @param typeTemplate
     */
    @Deprecated
    private void cacheBrand(TypeTemplate typeTemplate){
        List<Map> maps = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
        List<Brand> brands = new ArrayList<>();
        for(Map map:maps){
            Long id = (Long) map.get("id");
            Brand brand = brandDao.selectByPrimaryKey(id);
            brands.add(brand);
        }

        //将获得的数据添加到缓存里面
        BoundHashOperations redisBrands = redisTemplate.boundHashOps("brands");
        redisBrands.put(typeTemplate.getId() , brands);
    }

    @Override
    public List<Map> findAllToMap() {

        return typeTemplateDao.findAllToMap();
    }

    @Override
    public TypeTemplate findOneUpdate(Long id) {
        TypeTemplate typeTemplate = findOne(id);
        //查询规格参数
        List<Map> specList = getSpecList(typeTemplate);
        String specIds = JSON.toJSONString(specList);
        typeTemplate.setSpecIds(specIds);
        return typeTemplate;
    }

    private List<Map> getSpecList(TypeTemplate typeTemplate){
        List<Map> maps = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        for(Map map:maps){
            Integer specOptionId = (Integer) map.get("id");
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = query.createCriteria();
            criteria.andSpecIdEqualTo(specOptionId.longValue());

            List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(query);
            map.put("specOptions" , specificationOptions);
        }

        //Arrays.copyOf()

        return maps;
    }



}
