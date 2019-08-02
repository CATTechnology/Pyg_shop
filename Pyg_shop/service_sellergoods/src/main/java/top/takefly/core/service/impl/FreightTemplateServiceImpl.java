package top.takefly.core.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.template.FreightTemplateDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.template.FreightTemplate;
import top.takefly.core.pojo.template.FreightTemplateQuery;
import top.takefly.core.service.FreightTemplateService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class FreightTemplateServiceImpl implements FreightTemplateService {

	@Autowired
	private FreightTemplateDao freightTemplateDao;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<FreightTemplate> findAll() {
		return freightTemplateDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<FreightTemplate> page=   (Page<FreightTemplate>) freightTemplateDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(FreightTemplate freightTemplate) {
		freightTemplateDao.insert(freightTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(FreightTemplate freightTemplate){
		freightTemplateDao.updateByPrimaryKey(freightTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public FreightTemplate findOne(Long id){
		return freightTemplateDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			freightTemplateDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(FreightTemplate freightTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		FreightTemplateQuery example=new FreightTemplateQuery();
		FreightTemplateQuery.Criteria criteria = example.createCriteria();
		
		if(freightTemplate!=null){			
						if(freightTemplate.getSellerId()!=null && freightTemplate.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+freightTemplate.getSellerId()+"%");
			}
			if(freightTemplate.getIsDefault()!=null && freightTemplate.getIsDefault().length()>0){
				criteria.andIsDefaultLike("%"+freightTemplate.getIsDefault()+"%");
			}
			if(freightTemplate.getName()!=null && freightTemplate.getName().length()>0){
				criteria.andNameLike("%"+freightTemplate.getName()+"%");
			}
			if(freightTemplate.getSendTimeType()!=null && freightTemplate.getSendTimeType().length()>0){
				criteria.andSendTimeTypeLike("%"+freightTemplate.getSendTimeType()+"%");
			}
	
		}
		
		Page<FreightTemplate> page= (Page<FreightTemplate>)freightTemplateDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}
	
}
