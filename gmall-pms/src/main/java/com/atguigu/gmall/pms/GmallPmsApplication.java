package com.atguigu.gmall.pms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 使用事务注意点：
 *  1、开启事务@EnableTransactionManagement
 *  2、合理使用Required，Require_New
 *  自己调自己
*     1）、引入spring-aop（高级aop场景，aspectj）
*     2）、@EnableTransactionManagement(proxyTargetClass = true)
*    3）、@EnableAspectJAutoProxy(exposeProxy = true) //暴露出这些类的代理对象
*       然后再用ProductServiceImpl psProxy = (ProductServiceImpl) AopContext.currentProxy();
*       再去调方法
 */
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.atguigu.gmall.pms")
@EnableDubbo
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
