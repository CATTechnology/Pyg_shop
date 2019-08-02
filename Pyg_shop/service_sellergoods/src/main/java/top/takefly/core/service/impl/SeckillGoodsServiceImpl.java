package top.takefly.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import top.takefly.core.dao.seckill.SeckillGoodsDao;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.seckill.SeckillGoods;
import top.takefly.core.pojo.seckill.SeckillGoodsQuery;
import top.takefly.core.service.SeckillGoodsService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    /**
     * 查询全部
     */
    @Override
    public List<SeckillGoods> findAll() {
        return seckillGoodsDao.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<SeckillGoods> page = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(null);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(SeckillGoods seckillGoods) {
        seckillGoodsDao.insert(seckillGoods);
    }


    /**
     * 修改
     */
    @Override
    public void update(SeckillGoods seckillGoods) {
        seckillGoodsDao.updateByPrimaryKey(seckillGoods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public SeckillGoods findOne(Long id) {
        return seckillGoodsDao.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillGoodsDao.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(SeckillGoods seckillGoods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        SeckillGoodsQuery example = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = example.createCriteria();

        if (seckillGoods != null) {
            if (seckillGoods.getTitle() != null && seckillGoods.getTitle().length() > 0) {
                criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
            }
            if (seckillGoods.getSmallPic() != null && seckillGoods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + seckillGoods.getSmallPic() + "%");
            }
            if (seckillGoods.getSellerId() != null && seckillGoods.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillGoods.getSellerId() + "%");
            }
            if (seckillGoods.getStatus() != null && seckillGoods.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillGoods.getStatus() + "%");
            }
            if (seckillGoods.getIntroduction() != null && seckillGoods.getIntroduction().length() > 0) {
                criteria.andIntroductionLike("%" + seckillGoods.getIntroduction() + "%");
            }

        }

        Page<SeckillGoods> page = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(example);
        return new PageResult((int) page.getTotal(), page.getResult());
    }

    @Override
    public List<SeckillGoods> findList() {
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoodsList").values();
        if (seckillGoodsList == null || seckillGoodsList.size() == 0) {
            //记录当前时间
            Date date = new Date();
            SeckillGoodsQuery goodsQuery = new SeckillGoodsQuery();
            SeckillGoodsQuery.Criteria criteria = goodsQuery.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andStockCountGreaterThan(0);
            //结束时间要大于当前时间
            criteria.andEndTimeGreaterThanOrEqualTo(date);
            //开始时间要等于或者小于当前时间
            criteria.andStartTimeLessThanOrEqualTo(date);
            seckillGoodsList = seckillGoodsDao.selectByExample(goodsQuery);

            //如果有就存入
            if (seckillGoodsList.size() > 0) {
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    //存入redis
                    redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(), seckillGoods);
                }
            }
        }
        //返回当前的秒杀对象
        return seckillGoodsList;
    }

    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
        return seckillGoods;
    }

}
