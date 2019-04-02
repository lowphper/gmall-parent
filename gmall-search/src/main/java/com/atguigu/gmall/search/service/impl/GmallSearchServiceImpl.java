package com.atguigu.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.constant.EsConstant;
import com.atguigu.gmall.search.GmallSearchService;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.SearchParam;
import com.atguigu.gmall.to.es.SearchResponse;
import com.atguigu.gmall.to.es.SearchResponseAttrVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.management.Query;
import java.io.IOException;
import java.util.List;

@Service(version = "1.0")
@Component
public class GmallSearchServiceImpl implements GmallSearchService {

    @Autowired
    JestClient jestClient;

    @Override
    public void publishStatus(List<Long> ids, Integer publishStatus) {

    }

    /**
     * 封装商品信息到ES
     *
     * @param esProduct
     * @return
     */
    @Override
    public boolean saveProductInfoToES(EsProduct esProduct) {
        System.out.println("进到Es里了");
        Index build = new Index.Builder(esProduct)
                .index(EsConstant.ES_PRODUCT_INDEX)
                .type(EsConstant.ES_PRODUCT_TYPE)
                .build();
        DocumentResult execute = null;
        try {
            execute = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return execute.isSucceeded();
    }

    /**
     * 搜索商品
     *
     * @param param
     * @return
     * @throws IOException
     */
    @Override
    public SearchResponse searchProduct(SearchParam param)throws IOException {
        //1、根据页面传递的参数构建检索的DSL语句
        String queryDSL = buildSearchDsl(param);
        Search search = new Search.Builder(queryDSL).build();
        //2、执行查询
        SearchResult result = jestClient.execute(search);

        //3、封装属性
        return buildSearchResult(result);
    }

    /**
     * 创建DSL语句
     * @param param
     * @return
     */
    private String buildSearchDsl(SearchParam param) {
        //1创建查询类
        SearchSourceBuilder searchSource = new SearchSourceBuilder();
        //2先来个组合
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //2.1如果关键字不为空
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("name",param.getKeyword()));
            //还有加分项
            boolQuery.should(QueryBuilders.matchQuery("subTitle",param.getKeyword()));
            boolQuery.should(QueryBuilders.matchQuery("keywords",param.getKeyword()));
        }
        //2.2分类过滤
        if(param.getCatelog3Id()!=null){
            boolQuery.filter(QueryBuilders.termsQuery("productCategoryId",param.getCatelog3Id()));
        }
        //2.3品牌过滤
        if(param.getBrandId()!=null){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //2.4属性过滤,可能一个属性是多选，例如 网络:4G-5G
        String[] props = param.getProps();
        if(props!=null&&props.length>0){
            for (String prop : props) {
                String[] productAttributeId = prop.split(":");//分割：网络 4G-5G
                String[] attrValueList = productAttributeId[1].split("-"); //4G 5G
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                BoolQueryBuilder must = boolQueryBuilder.must(QueryBuilders.termQuery("attrValueList.productAttributeId",productAttributeId[0]))
                        .must(QueryBuilders.termsQuery("attrValueList.value", attrValueList));
                boolQuery.filter(QueryBuilders.nestedQuery("attrValueList",must, ScoreMode.None));
            }
        }
        //2.5价格区间过滤
        if(param.getPriceFrom()!=null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(param.getPriceFrom()));
        }
        if(param.getPriceTo()!=null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").lte(param.getPriceTo()));
        }
        searchSource.query(boolQuery);

        //3聚合查询， searchSourceBuilder.aggregation()
        // AggregationBuilders聚合查询工具类
        //3.1查询品牌id和品牌名
        TermsAggregationBuilder brandIdAggs = AggregationBuilders.terms("brandIdAggs")
                .field("brandId").size(100)
                .subAggregation(
                        AggregationBuilders.terms("brandIdAGGS_brandNameAggs")
                                .field("brandName")
                                .size(100)
                );

        searchSource.aggregation(brandIdAggs);
        //3.2查询类别id与名称
        TermsAggregationBuilder productCategoryAggs = AggregationBuilders.terms("productCategoryIdAggs")
                .field("productCategoryId")
                .size(100)
                .subAggregation(AggregationBuilders.terms("productCategoryId_NameAggs")
                        .field("productCategoryName").size(100));
        searchSource.aggregation(productCategoryAggs);
        //3.3过滤属性，将type=1的查询出来
        FilterAggregationBuilder filter = AggregationBuilders.filter("attrIdAggs"
                , QueryBuilders.termQuery("attrValueList.type", "1"));

       filter.subAggregation(
                AggregationBuilders.terms("attrIdAggs")
                        .field("attrValueList.productAttributeId")
                        .size(100)
                        .subAggregation(AggregationBuilders.terms("attrIdAggs_name")
                                .field("attrValueList.name")
                                .size(100)
                                .subAggregation(AggregationBuilders.terms("attrIdAggs_name_value")
                                        .field("attrValueList.value")
                                        .size(100)
                                )
                        )
        );
       //3.4属性聚合
        NestedAggregationBuilder attrValueAggs = AggregationBuilders.nested("attrValueAggs", "attrValueList")
                .subAggregation(filter);
        searchSource.aggregation(attrValueAggs);

        searchSource.from((param.getPageNum()-1)*param.getPageSize());
        searchSource.size(param.getPageSize());

        System.out.println(searchSource.toString());
        return searchSource.toString();
    }

    /**
     * 封装属性
     * @param result
     * @return
     */

    private SearchResponse buildSearchResult(SearchResult result) {
        SearchResponse searchResponse = new SearchResponse();
        //1获取到查询的内容
        List<SearchResult.Hit<EsProduct, Void>> hits = result.getHits(EsProduct.class);
        //2遍历查询到的内容，添加到返回内容里
        for (SearchResult.Hit<EsProduct, Void> hit : hits) {
            EsProduct source = hit.source;
            searchResponse.getProducts().add(source);
        }
        //3封装属性
        // {name:"品牌",values:["小米","苹果"]}
        MetricAggregation aggregations = result.getAggregations();
        SearchResponseAttrVo brand = new SearchResponseAttrVo();
        brand.setName("品牌");
        //3.1获取到品牌的id的桶的name值，添加带value
        List<TermsAggregation.Entry> brandIdAggs = aggregations.getTermsAggregation("brandIdAggs").getBuckets();
        brandIdAggs.forEach((brandId)->{
            brandId.getTermsAggregation("brandIdAGGS_brandNameAggs").getBuckets().forEach((brandName)->{
                String key = brandName.getKey();
                brand.getValue().add(key);
            });
        });
        searchResponse.setBrand(brand);

        //3获取到分类
        SearchResponseAttrVo category = new SearchResponseAttrVo();
        category.setName("分类");
        List<TermsAggregation.Entry> productCategoryIdAggs = aggregations.getTermsAggregation("productCategoryIdAggs").getBuckets();
        for (TermsAggregation.Entry productCategoryIdAgg : productCategoryIdAggs) {
            for (TermsAggregation.Entry productCategoryId_nameAggs : productCategoryIdAgg.getTermsAggregation("productCategoryId_NameAggs").getBuckets()) {
                String key = productCategoryId_nameAggs.getKey();
                category.getValue().add(key);
            }
        }
        searchResponse.setCatelog(category);
        //4获取属性
        TermsAggregation termsAggregation = aggregations.getChildrenAggregation("attrValueAggs")
                .getChildrenAggregation("attrIdAggs")
                .getTermsAggregation("attrIdAggs");
        List<TermsAggregation.Entry> buckets = termsAggregation.getBuckets();
        for (TermsAggregation.Entry bucket : buckets) {
            SearchResponseAttrVo attrVo = new SearchResponseAttrVo();
            //4.1先封装Id
           attrVo.setProductAttributeId(Long.parseLong( bucket.getKey()));
           //4.2封装属性名称
            List<TermsAggregation.Entry> nameBuckets = bucket.getTermsAggregation("attrIdAggs_name").getBuckets();
            for (TermsAggregation.Entry nameBucket : nameBuckets) {
               attrVo.setName( nameBucket.getKey());//封装属性
                //4.3封装属性值
                List<TermsAggregation.Entry> valueBuckets = nameBucket.getTermsAggregation("attrIdAggs_name_value").getBuckets();
                for (TermsAggregation.Entry valueBucket : valueBuckets) {
                   attrVo.getValue().add( valueBucket.getKey());
                }
            }
            searchResponse.getAttrs().add(attrVo);
        }
        return searchResponse;
    }

}
