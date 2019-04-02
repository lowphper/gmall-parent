package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


/**
 * 商品属性分类Controller
 * Created by atguigu 4/26.
 */
@CrossOrigin
@RestController
@Api(tags = "PmsProductAttributeCategoryController", description = "商品属性分类管理")
@RequestMapping("/productAttribute/category")
public class PmsProductAttributeCategoryController {
    @Reference
    private ProductAttributeCategoryService productAttributeCategoryService;
//已做
    @ApiOperation("添加商品属性分类")
    @PostMapping(value = "/create")
    public Object create(@RequestParam String name) {

        //添加商品属性分类
        productAttributeCategoryService.saveAttributeCategory(name);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("修改商品属性分类")
    @PostMapping(value = "/update/{id}")
    public Object update(@PathVariable Long id, @RequestParam String name) {
        //修改商品属性分类
        boolean b = productAttributeCategoryService.updateAttributeCategoryById(id,name);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("删除单个商品属性分类")
    @GetMapping(value = "/delete/{id}")
    public Object delete(@PathVariable Long id) {
        //删除单个商品属性分类
        productAttributeCategoryService.deleteById(id);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("获取单个商品属性分类信息")
    @GetMapping(value = "/{id}")
    public Object getItem(@PathVariable Long id) {
        //获取单个商品属性分类信息
        ProductAttributeCategory productAttributeCategory = productAttributeCategoryService.getAttributeCategoryById(id);
        return new CommonResult().success(productAttributeCategory);
    }
//已做
    @ApiOperation("分页获取所有商品属性分类")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Object getList(@RequestParam(defaultValue = "5") Integer pageSize, @RequestParam(defaultValue = "1") Integer pageNum) {
        //分页获取所有商品属性分类
        HashMap<String, Object> map = productAttributeCategoryService.selectproductAttributeCategoryPage(pageSize,pageNum);
        return new CommonResult().success(map);
    }
//已做
    @ApiOperation("获取所有商品属性分类及其下属性【难度较高】")
    @RequestMapping(value = "/list/withAttr", method = RequestMethod.GET)
    @ResponseBody
    public Object getListWithAttr() {
        //PmsProductAttributeCategoryItem
        //获取所有商品属性分类及其下属性
        List<PmsProductAttributeCategoryItem> list = productAttributeCategoryService.getAttributeCategoryAndChildren();
        return new CommonResult().success(list);
    }
}
