package top.takefly.core.service;
import java.util.List;

import top.takefly.core.pojo.address.Cities;
import top.takefly.core.pojo.entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface CitiesService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<Cities> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Cities cities);
	
	
	/**
	 * 修改
	 */
	public void update(Cities cities);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Cities findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(Cities cities, int pageNum, int pageSize);
	
}
