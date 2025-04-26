package com.yc;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TutorSecurityApp {

    @Value("${testcontent}")
    private static String testcontent;
    public static void main(String[] args) {
        System.out.println("ressecurity服务启动");
        SpringApplication.run(TutorSecurityApp.class, args);
    }
}
