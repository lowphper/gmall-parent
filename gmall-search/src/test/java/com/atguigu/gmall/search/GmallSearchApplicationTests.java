package com.atguigu.gmall.search;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.*;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.translog.Translog;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchControls;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchApplicationTests {

	@Autowired
	JestClient jestClient;

	/**
	 * 基本增删改查
	 */
//	@Test
//	public void contextLoads()throws IOException {
//		//查询所有
//		Search search = new Search.Builder("").addIndex("bank").addType("acount").build();
//		SearchResult execute = jestClient.execute(search);
//		List<Account> list = execute.getSourceAsObjectList(Account.class);//查出数据，默认查出十条数据
//		for (Account account : list) {
//			System.out.println(account+"\n");
//		}
//		System.out.println();
//	}
//
//	@Test
//	public void contextLoads1()throws IOException {
//		//删除
//		Delete build = new Delete.Builder("12").index("test").type("acount").build();
//		DocumentResult execute = jestClient.execute(build);
//		System.out.println(execute.getResponseCode());
//	}
//	@Test
//	public void contextLoads2()throws IOException {
//		//新增
//		//保存一个account
//		Account account = new Account(99000L, 21000L, "lei", "feng", 32, "F", "mill road", "tong teacher", "lfy@atguigu.com", "BJ", "CP");
//		Index build = new Index.Builder(account).index("test").type("acount").build();
//		DocumentResult execute = jestClient.execute(build);
//		System.out.println(execute.getResponseCode());
//	}
//	@Test
//	public void contextLoads3()throws IOException {
//		//操作DSL语句
//		//1、所有的条件都在SearchSourceBuilder中
//		//2、QueryBuilders构造各个条件
//		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
//		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(queryBuilder);
//		//接下来与普通查询一致
//		Search build = new Search.Builder(searchSourceBuilder.toString()).addIndex("bank").addType("acount")
//				.build();
//
//		SearchResult execute = jestClient.execute(build);
//		List<Account> list = execute.getSourceAsObjectList(Account.class);
//		for (Account account : list) {
//			System.out.println(account+"\n");
//		}
//		System.out.println();
//	}



}
