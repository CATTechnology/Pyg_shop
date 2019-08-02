package top.takefly.core.service;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.log.PayLog;

import java.util.List;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface PayLogService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<PayLog> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(PayLog payLog);
	
	
	/**
	 * 修改
	 */
	public void update(PayLog payLog);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public PayLog findOne(String id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(String[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(PayLog payLog, int pageNum, int pageSize);
	
}
