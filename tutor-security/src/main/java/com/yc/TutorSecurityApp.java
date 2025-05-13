package com.yc;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TutorSecurityApp {

    public static void main(String[] args) {
        System.out.println("ressecurity服务启动");
        SpringApplication.run(TutorSecurityApp.class, args);
    }
}
