package com.atguigu.gmall.admin.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.sms.service.CouponService;
import com.atguigu.gmall.sms.vo.SmsCouponCategoryWithList;
import com.atguigu.gmall.sms.vo.SmsCouponQueryParam;
import com.atguigu.gmall.to.CommonResult;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品管理Controller
 */
@CrossOrigin
@RestController
@Api(tags = "CmsCouponController", description = "优惠券管理")
@RequestMapping("/coupon")
public class SmsCouponController {
    @Reference
    CouponService couponService;

    //查询优惠卷列表
    @ApiOperation("获取优惠券列表")
    @GetMapping(value = "/list")
    public Object create(SmsCouponQueryParam smsCouponQueryParam,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        //查询所有优惠券
        Map<String,Object > map = couponService.listCoupon(smsCouponQueryParam,pageSize,pageNum);
        return new CommonResult().success(map);
    }
    //根据Id删除优惠券
    @ApiOperation("根据Id删除优惠券")
    @PostMapping(value = "/delete/{id}")
    public Object deleteById(@PathVariable Integer id) {
        //删除根据id
        boolean b = couponService.removeById(id);
        return new CommonResult().success(null);
    }
    //根据Id查看优惠卷详情
    @ApiOperation("根据Id查看优惠卷详情")
    @GetMapping(value = "/{id}")
    public Object getDetileById(Long id) {
        //根据Id查看优惠卷详情
        SmsCouponCategoryWithList list = couponService.getCouponDetileById(id);
        return new CommonResult().success(list);
    }
    //修改优惠券
    @ApiOperation("根据Id修改优惠券")
    @PostMapping(value = "/update/{id}")
    public Object updateCouponById(@PathVariable Long id,
            @RequestBody SmsCouponCategoryWithList smsCouponCategoryWithList) {
        //根据Id查看优惠卷详情
        boolean b = couponService.updateCouponById(id,smsCouponCategoryWithList);
        return new CommonResult().success(null);
    }
    //修改优惠券
    @ApiOperation("根据Id修改优惠券")
    @PostMapping(value = "/create")
    public Object createCoupon(@RequestBody SmsCouponCategoryWithList smsCouponCategoryWithList) {
        //根据Id查看优惠卷详情
        boolean b = couponService.createCoubon(smsCouponCategoryWithList);
        return new CommonResult().success(null);
    }




}
