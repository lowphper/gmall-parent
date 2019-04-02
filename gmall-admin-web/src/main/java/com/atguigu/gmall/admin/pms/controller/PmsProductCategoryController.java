package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.pms.vo.PmsProductCategoryParam;
import com.atguigu.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品分类模块Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsProductCategoryController", description = "商品分类管理")
@RequestMapping("/productCategory")
public class PmsProductCategoryController {
    @Reference
    private ProductCategoryService productCategoryService;
//已做
    @ApiOperation("添加产品分类")
    @PostMapping(value = "/create")
    public Object create(@Validated @RequestBody PmsProductCategoryParam productCategoryParam,
                         BindingResult result) {
        CommonResult commonResult = new CommonResult();
        //添加产品分类
       boolean b = productCategoryService.addProductCategroy(productCategoryParam);
       if(!b){
           return commonResult.failed();
       }
        return commonResult.success(null);
    }
//已做
    @ApiOperation("修改商品分类")
    @PostMapping(value = "/update/{id}")
    public Object update(@PathVariable Long id,
                         @Validated
                         @RequestBody PmsProductCategoryParam productCategoryParam,
                         BindingResult result) {
        //修改商品分类
        boolean b = productCategoryService.updateProductCategroyById(id,productCategoryParam);
        System.out.println("------"+b);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("分页查询商品分类")
    @GetMapping(value = "/list/{parentId}")
    public Object getList(@PathVariable Long parentId,
                          @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        //分页查询商品分类
        CommonResult commonResult = new CommonResult();
        HashMap<String, Object> map = productCategoryService.selectProductCategoryList(parentId, pageSize, pageNum);
        return commonResult.success(map);
    }
//已做
    @ApiOperation("根据id获取商品分类")
    @GetMapping(value = "/{id}")
    public Object getItem(@PathVariable Long id) {
        //根据id获取商品分类
        ProductCategory productCategory = productCategoryService.getById(id);
        return new CommonResult().success(productCategory);
    }
//已做
    @ApiOperation("删除商品分类")
    @GetMapping(value = "/delete/{id}")
    public Object delete(@PathVariable Long id) {
        //删除商品分类
        boolean b = productCategoryService.deleteById(id);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("修改导航栏显示状态")
    @PostMapping(value = "/update/navStatus")
    public Object updateNavStatus(@RequestParam("ids") List<Long> ids, @RequestParam("navStatus") Integer navStatus) {
        //修改导航栏显示状态
        boolean b = productCategoryService.updateNavStatus(ids,navStatus);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("修改显示状态")
    @PostMapping(value = "/update/showStatus")
    public Object updateShowStatus(@RequestParam("ids") List<Long> ids, @RequestParam("showStatus") Integer showStatus) {
        //修改显示状态
        boolean b = productCategoryService.updateShowStatus(ids,showStatus);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("查询所有一级分类及子分类[有难度]")
    @GetMapping(value = "/list/withChildren")
    public Object listWithChildren() {
        //查询所有一级分类及子分类
        Map<String, Object>[] map = null;
        List<PmsProductCategoryWithChildrenItem> list = productCategoryService.selectOneLevelAndChildrens();
        return new CommonResult().success(list);
    }
}
