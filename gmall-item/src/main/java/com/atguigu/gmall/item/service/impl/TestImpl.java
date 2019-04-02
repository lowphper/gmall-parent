package com.atguigu.gmall.item.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.item.service.TestI;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;

@Service
public class TestImpl implements TestI{
    @Reference
    ProductService productService;
    @Reference
    SkuStockService skuStock;


    public void test1(){
        System.out.println("走到这里了");
        Product byId = productService.getById(22);
        SkuStock byId1 = skuStock.getById(78);
        System.out.println(byId);
        System.out.println("============="+byId1);
    }
}
