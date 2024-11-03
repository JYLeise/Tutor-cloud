package com.yc;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@MapperScan("com.yc.dao")   // 扫描dao层下的Mapper接口 自动生成 实现类
@EnableDiscoveryClient  //以开启服务注册到nacos的功能
@EnableCaching // 开启缓存
public class AppConfig {
}
