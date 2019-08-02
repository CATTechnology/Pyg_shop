package top.takefly.core.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.takefly.core.dao.log.PayLogDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.log.PayLog;
import top.takefly.core.pojo.log.PayLogQuery;
import top.takefly.core.service.PayLogService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class PayLogServiceImpl implements PayLogService {

	@Autowired
	private PayLogDao payLogDao;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<PayLog> findAll() {
		return payLogDao.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<PayLog> page=   (Page<PayLog>) payLogDao.selectByExample(null);
		return new PageResult((int)page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(PayLog payLog) {
		payLogDao.insert(payLog);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(PayLog payLog){
		payLogDao.updateByPrimaryKey(payLog);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public PayLog findOne(String id){
		return payLogDao.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(String[] ids) {
		for(String id:ids){
			payLogDao.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(PayLog payLog, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		PayLogQuery example=new PayLogQuery();
		PayLogQuery.Criteria criteria = example.createCriteria();
		
		if(payLog!=null){			
						if(payLog.getOutTradeNo()!=null && payLog.getOutTradeNo().length()>0){
				criteria.andOutTradeNoLike("%"+payLog.getOutTradeNo()+"%");
			}
			if(payLog.getUserId()!=null && payLog.getUserId().length()>0){
				criteria.andUserIdLike("%"+payLog.getUserId()+"%");
			}
			if(payLog.getTransactionId()!=null && payLog.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+payLog.getTransactionId()+"%");
			}
			if(payLog.getTradeState()!=null && payLog.getTradeState().length()>0){
				criteria.andTradeStateLike("%"+payLog.getTradeState()+"%");
			}
			if(payLog.getOrderList()!=null && payLog.getOrderList().length()>0){
				criteria.andOrderListLike("%"+payLog.getOrderList()+"%");
			}
			if(payLog.getPayType()!=null && payLog.getPayType().length()>0){
				criteria.andPayTypeLike("%"+payLog.getPayType()+"%");
			}
	
		}
		
		Page<PayLog> page= (Page<PayLog>)payLogDao.selectByExample(example);		
		return new PageResult((int)page.getTotal(), page.getResult());
	}
	
}
