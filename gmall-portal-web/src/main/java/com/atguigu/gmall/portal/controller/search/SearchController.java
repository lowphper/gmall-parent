package com.atguigu.gmall.portal.controller.search;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.item.bean.ProductAllInfos;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.service.TestI;
import com.atguigu.gmall.search.GmallSearchService;
import com.atguigu.gmall.to.es.SearchParam;
import com.atguigu.gmall.to.es.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@CrossOrigin
@RestController
@Api(tags = "搜索模块")
public class SearchController {

    @Reference(version = "1.0")
    GmallSearchService searchService;
    @Reference
    ItemService itemService;
    @Reference
    TestI testI;


    @GetMapping("/search")
    @ApiOperation(value = "查询")
    public SearchResponse search(SearchParam param) throws IOException {
        SearchResponse searchResponse =  searchService.searchProduct(param);
        return searchResponse;
    }
    @GetMapping("/search/{skuId}")
    @ApiOperation(value = "查询")
    public ProductAllInfos searchskuId(@PathVariable Long skuId) throws IOException {
        return itemService.getInfo(skuId);
    }

    @GetMapping("/search/xxxx}")
    @ApiOperation(value = "查询")
    public ProductAllInfos searchskuId1() throws IOException {
        testI.test1();
        return null;
    }
}
