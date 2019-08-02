package top.pinyougou.shop.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import top.takefly.core.pojo.entity.GoodsAndGoodsDescAndItem;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.entity.Result;
import top.takefly.core.pojo.good.Goods;
import top.takefly.core.service.GoodsService;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<Goods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goodsAndGoodsDescAndItem
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsAndGoodsDescAndItem goodsAndGoodsDescAndItem){
		try {
			goodsService.update(goodsAndGoodsDescAndItem);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public GoodsAndGoodsDescAndItem findOne(Long id){
		//首先要判断是否为当前用户
		//String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String currentUser = "admin";
		String sellerId = goodsService.findName(id);
		if(currentUser==null || !currentUser.equals(sellerId)){
			return null;
		}
		return goodsService.findOne(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.updateStatus(ids , "1");
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 批量审核
	 * @param ids
	 * @return
	 */
	@RequestMapping("/verfify")
	public Result verfify(Long [] ids){
		try {
			goodsService.updateAuditStatus(ids , "0");
			return new Result(true, "审核中,请等待客服的处理");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "提交审核失败");
		}
	}

	/**
	 * 批量审核
	 * @param ids
	 * @return
	 */
	@RequestMapping("/shield")
	public Result shield(Long [] ids){
		try {
			goodsService.updateAuditStatus(ids , "4");
			return new Result(true, "已屏蔽");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "屏蔽失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody Goods goods, int page, int rows  ){
		//限制查询的商户
		goods.setSellerId("admin");

		return goodsService.findPage(goods, page, rows);		
	}

	@RequestMapping("/saveEntity")
	public Result saveEntity(@RequestBody GoodsAndGoodsDescAndItem goodsAndGoodsDescAndItem){
		try{
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			goodsAndGoodsDescAndItem.getGoods().setSellerId(name);
			goodsService.saveEntity(goodsAndGoodsDescAndItem);
			return new Result(true , "添加成功");
		}catch(Exception e){
			e.printStackTrace();
			return new Result(false , "添加失败！！！");
		}
	}
	
}
