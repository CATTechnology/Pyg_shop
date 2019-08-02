package top.takefly.buyer.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.order.OrderItemDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.order.OrderItem;
import top.takefly.core.pojo.order.OrderItemQuery;
import top.takefly.core.service.OrderItemService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderItemServiceImpl implements OrderItemService {

	@Autowired
	private OrderItemDao orderItemDao;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<OrderItem> findAll() {
		return orderItemDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<OrderItem> page=   (Page<OrderItem>) orderItemDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(OrderItem orderItem) {
		orderItemDao.insert(orderItem);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(OrderItem orderItem){
		orderItemDao.updateByPrimaryKey(orderItem);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public OrderItem findOne(Long id){
		return orderItemDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderItemDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(OrderItem orderItem, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		OrderItemQuery example=new OrderItemQuery();
		OrderItemQuery.Criteria criteria = example.createCriteria();
		
		if(orderItem!=null){			
						if(orderItem.getTitle()!=null && orderItem.getTitle().length()>0){
				criteria.andTitleLike("%"+orderItem.getTitle()+"%");
			}
			if(orderItem.getPicPath()!=null && orderItem.getPicPath().length()>0){
				criteria.andPicPathLike("%"+orderItem.getPicPath()+"%");
			}
			if(orderItem.getSellerId()!=null && orderItem.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+orderItem.getSellerId()+"%");
			}
	
		}
		
		Page<OrderItem> page= (Page<OrderItem>)orderItemDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}
	
}
