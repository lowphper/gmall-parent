package com.atguigu.gmall.item.service.impl;



import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.item.bean.ProductAllInfos;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.to.es.EsProductAttributeValue;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Reference
    SkuStockService skuStockService;
    @Reference
    ProductService productService;
    /**
     * 商品详情页查询
     * 1查询sku信息
     * 2查询spu信息
     * 3查询sku所有信息
     * 4查询商品的销售属性
     * 5查询商品的筛选属性
     * 待做，做空值判断
     * @param skuId
     * @return
     */
    public ProductAllInfos getInfo(Long skuId){
        System.out.println(skuId);
        ProductAllInfos infos = new ProductAllInfos();
        //1查询sku信息
        System.out.println("-------"+skuStockService);
        System.out.println("-------"+productService);
        SkuStock sku = skuStockService.selectSkuStockById(skuId);
        if(sku==null){
            return null;
        }
        Long productId = sku.getProductId();
        //2查询spu信息
        Product product = null;
        product = productService.selectProductFromCache(productId);
        //3查询sku系列信息
        List<SkuStock> list = skuStockService.selectSkuStockByPidOrkeyword(productId, null);
        //4查询商品的销售属性
        List<EsProductAttributeValue> saleAttr = productService.getProductSaleAttr(productId);
        //5查询商品的筛选属性
        List<EsProductAttributeValue> baseAttr = productService.getProductBaseAttr(productId);
        infos.setProduct(product);
        infos.setSkuStock(sku);
        infos.setProductBaseAttr(baseAttr);
        infos.setProductSaleAttr(saleAttr);
        infos.setSkuStockList(list);
        return infos;
    }
}
