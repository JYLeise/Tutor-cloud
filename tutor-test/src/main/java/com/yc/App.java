package com.yc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args )
    {

        SpringApplication.run(App.class, args);
        log.info("测试info");
        log.warn("测试warn");
    }
}
