package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * sku库存Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsSkuStockController", description = "sku商品库存管理")
@RequestMapping("/sku")
public class PmsSkuStockController {
    @Reference
    private SkuStockService skuStockService;

    @ApiOperation("根据商品编号及编号模糊搜索sku库存")
    @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
    @ResponseBody
    public Object getList(@PathVariable Long pid, @RequestParam(value = "keyword",required = false) String keyword) {

        //根据商品编号及编号模糊搜索sku库存
        List<SkuStock> list = skuStockService.selectSkuStockByPidOrkeyword(pid,keyword);
        return new CommonResult().success(list);
    }
    @ApiOperation("批量更新库存信息")
    @RequestMapping(value ="/update/{pid}",method = RequestMethod.POST)
    @ResponseBody
    public Object update(@PathVariable Long pid,@RequestBody List<SkuStock> skuStockList){
        //批量更新库存信息
        boolean b = skuStockService.updateSkuStockBatch(pid,skuStockList);
        return new CommonResult().success(null);
    }
}
