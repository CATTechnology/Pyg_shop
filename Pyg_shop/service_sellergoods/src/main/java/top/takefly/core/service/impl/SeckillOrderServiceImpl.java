package top.takefly.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import pojo.utils.IdWorker;
import top.takefly.core.dao.seckill.SeckillGoodsDao;
import top.takefly.core.dao.seckill.SeckillOrderDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.seckill.SeckillGoods;
import top.takefly.core.pojo.seckill.SeckillOrder;
import top.takefly.core.pojo.seckill.SeckillOrderQuery;
import top.takefly.core.service.SeckillOrderService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    /**
     * 查询全部
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderDao.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<SeckillOrder> page = (Page<SeckillOrder>) seckillOrderDao.selectByExample(null);
        return new PageResult((int) page.getTotal(), page.getResult());
    }


    /**
     * 增加
     */
    @Override
    public void add(SeckillOrder seckillOrder) {
        seckillOrderDao.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderDao.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findOne(Long id) {
        return seckillOrderDao.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderDao.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(SeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        SeckillOrderQuery example = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }

        }

        Page<SeckillOrder> page = (Page<SeckillOrder>) seckillOrderDao.selectByExample(example);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    @Autowired
    private IdWorker idWorker;

    @Override
    public void submitOrder(Long seckillId, String userId) {
        //首先从redis获取当前商品
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(seckillId);
        //判读是否存在该商品
        if (seckillGoods == null) {
            throw new RuntimeException("当前商品不存在!!!");
        }
        //判断当前商品是否出售完
        if (seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("当前商品已经出售完!!!");
        }
        //如果当前商品还存在，并且未 销售完
        //商品的库存减少一个
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //判断是否库存为0
        redisTemplate.boundHashOps("seckillGoodsList").put(seckillId, seckillGoods);
        if (seckillGoods.getStockCount() == 0) {
            //更新数据库，将此时商品的数量变成0
            seckillGoods.setStockCount(0);
            seckillGoodsDao.updateByPrimaryKey(seckillGoods);
            //同步redis,商品库存为0的商品
            redisTemplate.boundHashOps("seckillGoodsList").delete(seckillId);
        }

        //保存订单
        Long orderid = idWorker.nextId();
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setId(orderid);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setSeckillId(seckillId);
        //未支付
        seckillOrder.setStatus("0");
        seckillOrder.setUserId(userId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        //根据用户Id来存储订单
        redisTemplate.boundHashOps("seckillOrderList").put(userId, seckillOrder);
    }

    @Override
    public SeckillOrder findOrderFromRedis(String userId) {
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        return seckillOrder;
    }

    @Override
    public void saveOrderFromRedis(String userId, Long orderId, String transactionId) {
        //首先从redis里面读取出来订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        //是否存在
        if (seckillOrder == null) {
            throw new RuntimeException("订单不存在，系统故障");
        }
        if (seckillOrder.getId().longValue() != orderId.longValue()) {
            throw new RuntimeException("订单不一致!!!");
        }
        //设置支付时间
        seckillOrder.setPayTime(new Date());
        seckillOrder.setStatus("1");
        //存入数据库
        seckillOrderDao.insert(seckillOrder);
        //删除redis的订单
        redisTemplate.boundHashOps("seckillOrderList").delete(userId);
    }

    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //首先获取缓存中的数据
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        //判断是否存在当前订单
        if (seckillOrder != null && seckillOrder.getId().longValue() == orderId.longValue()) {
            //删除当前用户的订单
            redisTemplate.boundHashOps("seckillOrderList").delete(userId);
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(seckillOrder.getSeckillId());
            if (seckillGoods != null) {
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                //存入到redis里面
                redisTemplate.boundHashOps("seckillGoodsList").put(seckillOrder.getSeckillId() , seckillGoods);
            }
        }
    }

}
