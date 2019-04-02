package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.bean.SkuResponse;

public interface CartService{

    SkuResponse addToCart(String token, String cartKey, String skuId, Integer num);
}
