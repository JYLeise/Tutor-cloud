package com.yc.configs;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@RefreshScope
@Configuration
@Slf4j
public class MyDruidDataSource{

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Primary //表示默认的数据源，优先创建
    @Bean
    @ConditionalOnMissingBean
    @RefreshScope
    public DataSource mydataSource(){
        log.info("初始化druid数据源");
        DruidDataSource ds=new DruidDataSource();
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(this.driverClassName);
        ds.setUrl(url);
        return ds;
    }
}