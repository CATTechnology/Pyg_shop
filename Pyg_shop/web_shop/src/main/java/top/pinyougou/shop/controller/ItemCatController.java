package top.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.entity.Result;
import top.takefly.core.pojo.item.ItemCat;
import top.takefly.core.service.ItemCatService;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	@Reference
	private ItemCatService itemCatService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<ItemCat> findAll(){
		return itemCatService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(Integer page, Integer rows){
		return itemCatService.findPage(page, rows);
	}

	/**
	 * 增加
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody ItemCat itemCat){
		try {
			itemCatService.add(itemCat);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody ItemCat itemCat){
		try {
			itemCatService.update(itemCat);
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
	public ItemCat findOne(Long id){
		return itemCatService.findOne(id);
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			itemCatService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

		/**
	 * 查询+分页
	 * @param itemCat
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody ItemCat itemCat, Integer page, Integer rows  ){
		return itemCatService.findPage(itemCat, page, rows);		
	}

	@RequestMapping("/findByParentId")
	public PageResult findByParentId(@RequestBody ItemCat itemCat , Long parentId , Integer page , Integer rows){
		return itemCatService.findByParentId(parentId , page , rows , itemCat);
	}

	@RequestMapping("/findByParentIdUpdate")
	public List<ItemCat> findByParentIdUpdate(Long parentId){
		List<ItemCat> result = itemCatService.findByParentIdUpdate(parentId);
		return result;
	}
	
}
