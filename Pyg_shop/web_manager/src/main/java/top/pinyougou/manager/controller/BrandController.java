package top.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.takefly.core.pojo.entity.PageResult;
import top.takefly.core.pojo.entity.Result;
import top.takefly.core.pojo.good.Brand;
import top.takefly.core.service.BrandService;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 默认加了.do
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    @RequestMapping("/findPage.do")
    public PageResult findPage(Integer page, Integer size) {
        PageResult pageResult = brandService.findPage(page, size);
        return pageResult;
    }

    @RequestMapping("/save.do")
    public Result save(@RequestBody Brand brand) {
        try {
            brandService.save(brand);
            return new Result(true, "保存成功.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    /**
     * 数据回显
     */
    @RequestMapping("/findOne.do")
    public Brand findOne(Long id) {
        Brand brand = brandService.findOne(id);
        return brand;
    }

    /**
     * 修改品牌
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return new Result(true, "恭喜你， 修改成功...");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "对不起，修改失败！");
        }
    }

    /**
     * 批量删除产品
     */
    @RequestMapping("/delDetach.do")
    public Result delDetach(Long[] ids) {
        String info = "品牌单个";
        if (ids.length > 1) {
            info = "品牌批量";
        }
        try {
            brandService.delDetach(ids);
            return new Result(true, info + "删除成功...");
        } catch (Exception e) {
            return new Result(false, info + "删除失败!!!");
        }
    }

    /**
     * 搜索
     * @param brand 条件
     * @param page 当前页
     * @param size 每页数
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult searchBrand(@RequestBody Brand brand, Integer page, Integer size) {
        PageResult pageResult = brandService.searchBrand(brand, page , size);
        return pageResult;
    }

    @RequestMapping("/selectBrandToTypeTemplate")
    public List<Map> selectBrandToTypeTemplate() {
        return brandService.selectBrandToTypeTemplate();
    }
}
