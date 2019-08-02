package top.takefly.core.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import top.takefly.core.dao.ad.ContentDao;
import top.takefly.core.pojo.ad.Content;
import top.takefly.core.pojo.ad.ContentQuery;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.service.ContentService;
/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private ContentDao contentDao;

	/**
	 * redis缓存api
	 */
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<Content> findAll() {
		return contentDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<Content> page=   (Page<Content>) contentDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Content content) {
		BoundHashOperations contents = redisTemplate.boundHashOps("contents");
		if(contents.size() > 0){
			contents.delete(content.getCategoryId());
		}
		contentDao.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Content content){
		//更新之前要先获取原来的
		Content oldContent = contentDao.selectByPrimaryKey(content.getId());
		Long categoryId = oldContent.getCategoryId();

		//操作redis
		BoundHashOperations contents = redisTemplate.boundHashOps("contents");
		if(contents.size() > 0 && categoryId != null && !"".equals(categoryId)){
			contents.delete(categoryId);
		}
		contentDao.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Content findOne(Long id){
		return contentDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		BoundHashOperations contents = null;
		for(Long id:ids){
			contents = redisTemplate.boundHashOps("contents");
			Content content = contentDao.selectByPrimaryKey(id);
			if(contents.size() > 0 && content!=null){
				Long categoryId = content.getCategoryId();
				contents.delete(categoryId);
			}
			contentDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(Content content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		ContentQuery example=new ContentQuery();
		ContentQuery.Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<Content> page= (Page<Content>)contentDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 通过categoryId来查询相应的广告
	 * @param id
	 * @return
	 */
	@Override
	public List<Content> selectContentList(Long id) {
		List<Content> contents = (List<Content>) redisTemplate.boundHashOps("contents").get(id);
		System.out.println("从redis缓存读取数据....");
		if(contents == null || contents.size() == 0){
			ContentQuery query = new ContentQuery();
			ContentQuery.Criteria criteria = query.createCriteria();
			query.setOrderByClause("sort_order");
			criteria.andStatusEqualTo("1");
			criteria.andCategoryIdEqualTo(id);
			contents = contentDao.selectByExample(query);
			//同时设置缓存
			redisTemplate.boundHashOps("contents").put(id , contents);
			System.out.println("从数据库加载数据.....");
		}
		return contents;
	}

}
