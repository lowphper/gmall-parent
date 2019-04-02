package com.atguigu.gmall.item.bean;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductAllInfos implements Serializable{
    private SkuStock skuStock;
    private Product product;
    private List<SkuStock> skuStockList;
    private List<EsProductAttributeValue> productSaleAttr;
    private List<EsProductAttributeValue> productBaseAttr;

}
