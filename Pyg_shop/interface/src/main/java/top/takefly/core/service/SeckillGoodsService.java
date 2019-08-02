package top.takefly.core.service;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.seckill.SeckillGoods;

import java.util.List;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<SeckillGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(SeckillGoods seckillGoods);
	
	
	/**
	 * 修改
	 */
	public void update(SeckillGoods seckillGoods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public SeckillGoods findOne(Long id);
	
	
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
	public PageResult findPage(SeckillGoods seckillGoods, int pageNum, int pageSize);

	/**
	 * 分页
	 * @return
	 */
	public List<SeckillGoods> findList();


	/**
	 * 到redis里面搜索
	 * @param id
	 * @return
	 */
    public SeckillGoods findOneFromRedis(Long id);
}
