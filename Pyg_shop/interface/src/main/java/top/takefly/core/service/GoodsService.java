package top.takefly.core.service;
import java.util.List;

import top.takefly.core.pojo.entity.GoodsAndGoodsDescAndItem;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.good.Goods;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<Goods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 * @param goods
	 */
	public void update(GoodsAndGoodsDescAndItem goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public GoodsAndGoodsDescAndItem findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids , String status);

	/**
	 *
	 * @param ids
	 * @param status
	 */
	public void updateAuditStatus(Long[] ids,String status) throws Exception;

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(Goods goods, int pageNum, int pageSize);

    public void saveEntity(GoodsAndGoodsDescAndItem goodsAndGoodsDescAndItem);

	public String findName(Long id);

    public void updateStatus(Long[] ids, String status);
}
