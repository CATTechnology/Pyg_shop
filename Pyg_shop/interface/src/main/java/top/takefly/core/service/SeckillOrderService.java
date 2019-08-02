package top.takefly.core.service;

import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.seckill.SeckillOrder;

import java.util.List;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface SeckillOrderService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<SeckillOrder> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(SeckillOrder seckillOrder);


    /**
     * 修改
     */
    public void update(SeckillOrder seckillOrder);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public SeckillOrder findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(SeckillOrder seckillOrder, int pageNum, int pageSize);

    /**
     * 提交订单
     *
     * @param seckillId
     * @param userId
     */
    public void submitOrder(Long seckillId, String userId);

    /**
     * 从redis搜索订单
     *
     * @return
     */
    public SeckillOrder findOrderFromRedis(String userId);

    /**
     * 从redis保存到数据库
     * @param userId
     * @param transactionId
     */
    public void saveOrderFromRedis(String userId,Long orderId , String transactionId);

    /**
     * 从缓存中删除数据
     * @param userId
     * @param orderId
     */
    public void deleteOrderFromRedis(String userId,Long orderId);
}
