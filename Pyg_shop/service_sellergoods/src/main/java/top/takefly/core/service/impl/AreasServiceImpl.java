package top.takefly.core.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.address.AreasDao;
import top.takefly.core.pojo.address.Areas;
import top.takefly.core.pojo.address.AreasQuery;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.service.AreasService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class AreasServiceImpl implements AreasService {

	@Autowired
	private AreasDao areasDao;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<Areas> findAll() {
		return areasDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<Areas> page=   (Page<Areas>) areasDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Areas areas) {
		areasDao.insert(areas);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Areas areas){
		areasDao.updateByPrimaryKey(areas);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Areas findOne(Long id){
		return areasDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			areasDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(Areas areas, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		AreasQuery example=new AreasQuery();
		AreasQuery.Criteria criteria = example.createCriteria();
		
		if(areas!=null){			
						if(areas.getAreaid()!=null && areas.getAreaid().length()>0){
				criteria.andAreaidLike("%"+areas.getAreaid()+"%");
			}
			if(areas.getArea()!=null && areas.getArea().length()>0){
				criteria.andAreaLike("%"+areas.getArea()+"%");
			}
			if(areas.getCityid()!=null && areas.getCityid().length()>0){
				criteria.andCityidLike("%"+areas.getCityid()+"%");
			}
	
		}
		
		Page<Areas> page= (Page<Areas>)areasDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}
	
}
