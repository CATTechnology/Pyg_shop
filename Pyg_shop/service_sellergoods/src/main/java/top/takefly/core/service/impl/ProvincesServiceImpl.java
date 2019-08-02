package top.takefly.core.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.address.ProvincesDao;
import top.takefly.core.pojo.address.Provinces;
import top.takefly.core.pojo.address.ProvincesQuery;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.service.ProvincesService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ProvincesServiceImpl implements ProvincesService {

	@Autowired
	private ProvincesDao provincesDao;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<Provinces> findAll() {
		return provincesDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<Provinces> page=   (Page<Provinces>) provincesDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Provinces provinces) {
		provincesDao.insert(provinces);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Provinces provinces){
		provincesDao.updateByPrimaryKey(provinces);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Provinces findOne(Long id){
		return provincesDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			provincesDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(Provinces provinces, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		ProvincesQuery example=new ProvincesQuery();
		ProvincesQuery.Criteria criteria = example.createCriteria();
		
		if(provinces!=null){			
						if(provinces.getProvinceid()!=null && provinces.getProvinceid().length()>0){
				criteria.andProvinceidLike("%"+provinces.getProvinceid()+"%");
			}
			if(provinces.getProvince()!=null && provinces.getProvince().length()>0){
				criteria.andProvinceLike("%"+provinces.getProvince()+"%");
			}
	
		}
		
		Page<Provinces> page= (Page<Provinces>)provincesDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}
	
}
