package top.takefly.buyer.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import pojo.utils.IdWorker;
import top.takefly.core.dao.log.PayLogDao;
import top.takefly.core.dao.order.OrderDao;
import top.takefly.core.dao.order.OrderItemDao;
import top.takefly.core.pojo.entity.Cart;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.log.PayLog;
import top.takefly.core.pojo.order.Order;
import top.takefly.core.pojo.order.OrderItem;
import top.takefly.core.pojo.order.OrderQuery;
import top.takefly.core.service.OrderService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private PayLogDao payLogDao;

    /**
     * 查询全部
     */
    @Override
    public List<Order> findAll() {
        return orderDao.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Order> page = (Page<Order>) orderDao.selectByExample(null);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Order order) {
        //获取redis中的购物车
        BoundHashOperations redisCartList = redisTemplate.boundHashOps("CartList");
        List<Cart> cartList = (List<Cart>) redisCartList.get(order.getUserId());
        ArrayList<String> idList = new ArrayList<>();
        double money = 0;
        for (Cart cart : cartList) {
            long order_id = idWorker.nextId();
            Order order1 = new Order();
            order1.setOrderId(order_id);
            order1.setUserId(order.getUserId());
            order1.setCreateTime(new Date());
            order1.setUpdateTime(new Date());
            order1.setStatus("1");
            order1.setPaymentType(order.getPaymentType());
            order1.setReceiverAreaName(order.getReceiverAreaName());
            order1.setReceiver(order.getReceiver());
            order1.setReceiverMobile(order.getReceiverMobile());
            order1.setSourceType(order.getSourceType());
            order1.setSellerId(cart.getSellerId());
            for (OrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(order_id);
                orderItem.setSellerId(cart.getSellerId());
                money += orderItem.getTotalFee().doubleValue();
                idList.add(orderItem.getItemId() + "");
                orderItemDao.insertSelective(orderItem);
            }
            orderDao.insertSelective(order1);
        }

        //如果是微信支付就将该记录存入redis
        if ("1".equals(order.getPaymentType())) {
            PayLog payLog = new PayLog();
            //支付订单号
            String outTradeNo = idWorker.nextId() + "";
            payLog.setOutTradeNo(outTradeNo);
            payLog.setCreateTime(new Date());
            payLog.setPayType("1");
            String idListStr = idList.toString().replace("[", "")
                    .replace("]", "")
                    .replace(" ", "");
            payLog.setOrderList(idListStr);
            payLog.setTradeState("0");
            payLog.setUserId(order.getUserId());
            payLog.setTotalFee((long) money * 100);
            payLogDao.insert(payLog);
            //将日志信息加入缓存
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
        }

        //删除提交了的购物车
        redisTemplate.boundHashOps("CartList").delete(order.getUserId());
    }


    /**
     * 修改
     */
    @Override
    public void update(Order order) {
        orderDao.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Order findOne(Long id) {
        return orderDao.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderDao.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(Order order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        OrderQuery example = new OrderQuery();
        OrderQuery.Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<Order> page = (Page<Order>) orderDao.selectByExample(example);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    @Override
    public PayLog searchPayLogFromRedis(String userId) {
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(userId);
        return payLog;
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //首先查询出相应的订单
        PayLog payLog = payLogDao.selectByPrimaryKey(out_trade_no);

        payLog.setPayTime(new Date());
        payLog.setTradeState("1");
        payLog.setTransactionId(transaction_id);
        //更新支付日志
        payLogDao.updateByPrimaryKey(payLog);
        //获取所有的已支付订单
        String orderList = payLog.getOrderList();
        String[] orderListArr = orderList.split(",");
        for (String id : orderListArr) {
            Order order = orderDao.selectByPrimaryKey(Long.parseLong(id));
            if (order != null) {
                //修改订单为已支付状态
                order.setStatus("2");
                orderDao.updateByPrimaryKey(order);
            }
        }

        //然后清除相应redis缓存即可
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }

}
