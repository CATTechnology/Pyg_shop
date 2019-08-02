package top.takefly.core.service;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.item.ItemCat;

import java.util.List;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<ItemCat> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(ItemCat itemCat);
	
	
	/**
	 * 修改
	 */
	public void update(ItemCat itemCat);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public ItemCat findOne(Long id);
	
	
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
	public PageResult findPage(ItemCat itemCat, int pageNum, int pageSize);

	/**
	 * 通过上级id来查询
	 * @param parentId
	 * @param page
	 * @param rows
	 * @param itemCat
	 * @return
	 */
    public PageResult findByParentId(Long parentId, Integer page, Integer rows, ItemCat itemCat);

    public List<ItemCat> findByParentIdUpdate(Long parentId);
}
