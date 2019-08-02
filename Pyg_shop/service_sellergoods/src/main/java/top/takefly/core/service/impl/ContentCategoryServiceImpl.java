package top.takefly.core.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.ad.ContentCategoryDao;
import top.takefly.core.pojo.ad.ContentCategory;
import top.takefly.core.pojo.ad.ContentCategoryQuery;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.service.ContentCategoryService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private ContentCategoryDao contentCategoryDao;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<ContentCategory> findAll() {
		return contentCategoryDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<ContentCategory> page=   (Page<ContentCategory>) contentCategoryDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(ContentCategory contentCategory) {
		contentCategoryDao.insert(contentCategory);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(ContentCategory contentCategory){
		contentCategoryDao.updateByPrimaryKey(contentCategory);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public ContentCategory findOne(Long id){
		return contentCategoryDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			contentCategoryDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(ContentCategory contentCategory, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		ContentCategoryQuery example=new ContentCategoryQuery();
		ContentCategoryQuery.Criteria criteria = example.createCriteria();
		
		if(contentCategory!=null){			
						if(contentCategory.getName()!=null && contentCategory.getName().length()>0){
				criteria.andNameLike("%"+contentCategory.getName()+"%");
			}
	
		}
		
		Page<ContentCategory> page= (Page<ContentCategory>)contentCategoryDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}
	
}
