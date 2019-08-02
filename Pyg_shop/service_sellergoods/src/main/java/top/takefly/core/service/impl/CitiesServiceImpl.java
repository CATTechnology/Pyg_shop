package top.takefly.core.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.address.CitiesDao;
import top.takefly.core.pojo.address.Cities;
import top.takefly.core.pojo.address.CitiesQuery;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.service.CitiesService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class CitiesServiceImpl implements CitiesService {

	@Autowired
	private CitiesDao citiesDao;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<Cities> findAll() {
		return citiesDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<Cities> page=   (Page<Cities>) citiesDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Cities cities) {
		citiesDao.insert(cities);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Cities cities){
		citiesDao.updateByPrimaryKey(cities);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Cities findOne(Long id){
		return citiesDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			citiesDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(Cities cities, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		CitiesQuery example=new CitiesQuery();
		CitiesQuery.Criteria criteria = example.createCriteria();
		
		if(cities!=null){			
						if(cities.getCityid()!=null && cities.getCityid().length()>0){
				criteria.andCityidLike("%"+cities.getCityid()+"%");
			}
			if(cities.getCity()!=null && cities.getCity().length()>0){
				criteria.andCityLike("%"+cities.getCity()+"%");
			}
			if(cities.getProvinceid()!=null && cities.getProvinceid().length()>0){
				criteria.andProvinceidLike("%"+cities.getProvinceid()+"%");
			}
	
		}
		
		Page<Cities> page= (Page<Cities>)citiesDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}
	
}
