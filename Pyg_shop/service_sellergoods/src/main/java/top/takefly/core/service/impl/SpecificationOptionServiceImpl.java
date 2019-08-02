package top.takefly.core.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.specification.SpecificationOptionDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.specification.SpecificationOption;
import top.takefly.core.pojo.specification.SpecificationOptionQuery;
import top.takefly.core.service.SpecificationOptionService;
/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationOptionServiceImpl implements SpecificationOptionService {

	@Autowired
	private SpecificationOptionDao specificationOptionDao;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<SpecificationOption> findAll() {
		return specificationOptionDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<SpecificationOption> page=   (Page<SpecificationOption>) specificationOptionDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(SpecificationOption specificationOption) {
		specificationOptionDao.insert(specificationOption);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(SpecificationOption specificationOption){
		specificationOptionDao.updateByPrimaryKey(specificationOption);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public SpecificationOption findOne(Long id){
		return specificationOptionDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationOptionDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(SpecificationOption specificationOption, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		SpecificationOptionQuery example=new SpecificationOptionQuery();
		SpecificationOptionQuery.Criteria criteria = example.createCriteria();
		
		if(specificationOption!=null){			
						if(specificationOption.getOptionName()!=null && specificationOption.getOptionName().length()>0){
				criteria.andOptionNameLike("%"+specificationOption.getOptionName()+"%");
			}
	
		}
		
		Page<SpecificationOption> page= (Page<SpecificationOption>)specificationOptionDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}
	
}
