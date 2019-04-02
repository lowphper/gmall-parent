package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品管理Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsProductController", description = "商品管理")
@RequestMapping("/product")
public class PmsProductController {
    @Reference
    private ProductService productService;
    private ProductAttributeCategoryService productAttributeCategoryService;

    //未做
    @ApiOperation("创建商品")
    @PostMapping(value = "/create")
    public Object create(@RequestBody PmsProductParam productParam,
                         BindingResult bindingResult) {
        //查询所有一级分类及子分类
        boolean b = productService.saveProduct(productParam);
        return new CommonResult().success(null);
    }

    //已做
    @ApiOperation("根据商品id获取商品编辑信息")
    @GetMapping(value = "/updateInfo/{id}")
    public Object getUpdateInfo(@PathVariable Long id) {
        //根据商品id获取商品编辑信息
        PmsProductParam pmsProductParam = productService.getProductInfoById(id);
        return new CommonResult().success(pmsProductParam);
    }

    //已做
    @ApiOperation("更新商品")
    @PostMapping(value = "/update/{id}")
    public Object update(@PathVariable Long id, @RequestBody PmsProductParam productParam, BindingResult bindingResult) {
        //更新商品
        boolean b = productService.updateProductAndOtherInfoById(id, productParam);
        return new CommonResult().success(null);
    }

    //已做
    @ApiOperation("查询商品")
    @GetMapping(value = "/list")
    public Object getList(PmsProductQueryParam productQueryParam,
                          @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        //查询商品
        Map<String, Object> map = productService.selectPrducts(productQueryParam, pageSize, pageNum);
        return new CommonResult().success(map);
    }

    //已做
    @ApiOperation("根据商品名称或货号模糊查询")
    @GetMapping(value = "/simpleList")
    public Object getList(String keyword) {
        //根据商品名称或货号模糊查询
        Map<String, Object> map = productService.selectProductByNameOrProductSn(keyword);
        return new CommonResult().success(map);
    }
//已做
    @ApiOperation("批量修改审核状态")
    @PostMapping(value = "/update/verifyStatus")
    public Object updateVerifyStatus(@RequestParam("ids") List<Long> ids,
                                     @RequestParam("verifyStatus") Integer verifyStatus,
                                     @RequestParam("detail") String detail) {
        //批量修改审核状态
        boolean b = productService.updateVerifyStatusBatch(ids, verifyStatus, detail);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("批量上下架")
    @PostMapping(value = "/update/publishStatus")
    public Object updatePublishStatus(@RequestParam("ids") List<Long> ids,
                                      @RequestParam("publishStatus") Integer publishStatus) {
        //批量上下架
        boolean b = productService.updatePublishStatusBatch(ids, publishStatus);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("批量推荐商品")
    @PostMapping(value = "/update/recommendStatus")
    public Object updateRecommendStatus(@RequestParam("ids") List<Long> ids,
                                        @RequestParam("recommendStatus") Integer recommendStatus) {
        //批量推荐商品
        boolean b = productService.updateRecommendStatusBatch(ids, recommendStatus);
        return new CommonResult().success(null);
    }
//已做
    @ApiOperation("批量设为新品")
    @PostMapping(value = "/update/newStatus")
    public Object updateNewStatus(@RequestParam("ids") List<Long> ids,
                                  @RequestParam("newStatus") Integer newStatus) {
        //批量设为新品
        boolean b = productService.updateNewStatusBatch(ids, newStatus);
        return new CommonResult().success(null);
    }

    //已做
    @ApiOperation("批量修改删除状态")
    @PostMapping(value = "/update/deleteStatus")
    public Object updateDeleteStatus(@RequestParam("ids") List<Long> ids,
                                     @RequestParam("deleteStatus") Integer deleteStatus) {
        //根据商品id获取商品编辑信息
        boolean b = productService.deleteProductBatch(ids, deleteStatus);
        if (!b) {
            return new CommonResult().failed();
        }
        return new CommonResult().success(null);
    }
}
