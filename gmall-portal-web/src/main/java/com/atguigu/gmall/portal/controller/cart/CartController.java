package com.atguigu.gmall.portal.controller.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.cart.bean.SkuResponse;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Api(tags = "搜索模块")
@CrossOrigin
@RestController("/cart")
public class CartController {
    @Reference
    CartService cartService;

    @PostMapping("/add")
    @ApiOperation(value = "添加到购物车")
    public CommonResult addCart(
        @RequestParam("token")String token,
        @RequestParam("cartKey")String cartKey,
        @RequestParam("skuId")String skuId,
        @RequestParam("num")Integer num
    ){
        SkuResponse b = cartService.addToCart(token,cartKey,skuId,num);

        return new CommonResult().success(null);
    }
}
