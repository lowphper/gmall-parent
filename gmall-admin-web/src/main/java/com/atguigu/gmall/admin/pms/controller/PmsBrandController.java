package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.service.BrandService;
import com.atguigu.gmall.pms.vo.PmsBrandParam;
import com.atguigu.gmall.to.CommonResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 品牌功能Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsBrandController", description = "商品品牌管理")
@RequestMapping("/brand")
public class PmsBrandController {
    @Reference
    private BrandService brandService;

    //已做
    @ApiOperation(value = "获取全部品牌列表")
    @GetMapping(value = "/listAll")
    public Object getList() {

        //获取全部品牌列表
        List<Brand> list = brandService.getAllBrand();
        return new CommonResult().success(list);
    }

    //已做
    @ApiOperation(value = "添加品牌")
    @PostMapping(value = "/create")
    public Object create(@Validated @RequestBody PmsBrandParam pmsBrand, BindingResult result) {
        CommonResult commonResult = new CommonResult();
        //添加品牌
        Brand brand = new Brand();
        BeanUtils.copyProperties(pmsBrand, brand);
        boolean b = brandService.addBrandOne(brand);
        if (!b) {
            return commonResult.failed();
        }
        commonResult.setMessage("操作成功");
        return commonResult.success(null);
    }

    //已做
    @ApiOperation(value = "更新品牌")
    @PostMapping(value = "/update/{id}")
    public Object update(@PathVariable("id") Long id,
                         @Validated @RequestBody PmsBrandParam pmsBrandParam,
                         BindingResult result) {
        CommonResult commonResult = new CommonResult();
        Brand brand = new Brand();
        BeanUtils.copyProperties(pmsBrandParam, brand);
        brand.setId(id);
        //更新品牌
        boolean b = brandService.updateBrandById(brand);
        commonResult.setMessage("操作成功");
        return commonResult.success(null);
    }

    //已做
    @ApiOperation(value = "删除品牌")
    @GetMapping(value = "/delete/{id}")
    public Object delete(@PathVariable("id") Long id) {
        CommonResult commonResult = new CommonResult();
        //删除品牌
        boolean b = brandService.deleteBrandById(id);
        if (!b) {
            return commonResult.failed();
        }
        commonResult.setMessage("操作成功");
        return commonResult.success(null);
    }

    //已做
    @ApiOperation(value = "根据品牌名称分页获取品牌列表")
    @GetMapping(value = "/list")
    public Object getList(@RequestParam(value = "keyword", required = false) String keyword,
                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        CommonResult commonResult = new CommonResult();
        //根据品牌名称分页获取品牌列表
        Map<String, Object> map = brandService.getBrandList(keyword, pageNum, pageSize);
        commonResult.setMessage("操作成功");
        return commonResult.success(map);
    }

    //已做
    @ApiOperation(value = "根据编号查询品牌信息")
    @GetMapping(value = "/{id}")
    public Object getItem(@PathVariable("id") Long id) {
        CommonResult commonResult = new CommonResult();
        //根据编号查询品牌信息
        Brand brand = brandService.getById(id);
        return commonResult.success(brand);
    }
//已做
    @ApiOperation(value = "批量删除品牌")
    @PostMapping(value = "/delete/batch")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        CommonResult commonResult = new CommonResult();
        //批量删除品牌
        boolean b = brandService.deleteBrandBatchById(ids);
        commonResult.setMessage("操作成功");
        return commonResult.success(null);
    }

    //已做
    @ApiOperation(value = "批量更新显示状态")
    @PostMapping(value = "/update/showStatus")
    public Object updateShowStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("showStatus") Integer showStatus) {
        CommonResult commonResult = new CommonResult();
        //批量更新显示状态
        boolean b = brandService.updateShowStatusBatch(ids, showStatus);
        if (!b) {
            return commonResult.failed();
        }
        commonResult.setMessage("操作成功");
        return commonResult.success(null);
    }
//已做
    @ApiOperation(value = "批量更新厂家制造商状态")
    @PostMapping(value = "/update/factoryStatus")
    public Object updateFactoryStatus(@RequestParam("ids") List<Long> ids,
                                      @RequestParam("factoryStatus") Integer factoryStatus) {
        CommonResult commonResult = new CommonResult();
        //批量更新厂家制造商状态
       boolean b = brandService.updateFactoryStatusBatch(ids,factoryStatus);
        if (!b) {
            return commonResult.failed();
        }
        commonResult.setMessage("操作成功");
        return commonResult.success(null);
    }
}
