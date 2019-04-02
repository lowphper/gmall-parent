package com.atguigu.gmall.admin.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.sms.service.CouponService;
import com.atguigu.gmall.sms.vo.SmsCouponCategoryWithList;
import com.atguigu.gmall.sms.vo.SmsCouponQueryParam;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商品管理Controller
 */
@CrossOrigin
@RestController
@Api(tags = "CmsCouponHistoryController", description = "优惠券管理")
@RequestMapping("/couponHistory/")
public class SmsCouponHistoryController {
    @Reference
    CouponService couponService;

    //获取优惠券领取记录
    @ApiOperation("获取优惠券领取记录")
    @GetMapping(value = "/list")
    public Object create(SmsCouponQueryParam smsCouponQueryParam,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                         @RequestParam(value = "couponId") Integer couponId) {
        //获取优惠券领取记录
        Map<String,Object > map = couponService.listCoupon(smsCouponQueryParam,pageSize,pageNum);
        return new CommonResult().success(map);
    }

}
