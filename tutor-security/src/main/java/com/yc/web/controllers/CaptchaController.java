package com.yc.web.controllers;

import com.github.cage.Cage;
import com.github.cage.GCage;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;
@Tag(name="验证码接口")
@RestController
@RequestMapping("/api/security")
@Slf4j
public class CaptchaController {

    private final Cage cage=new GCage();

    //因为验证码生成后要存到session中，所以这个 方法的参数 由springmvc自动注入HttpSession
    //因为我们用的是springboot 3 ，这里的HttpSession必须用 import jakarta.servlet.http.HttpSession;
    @GetMapping("/captcha")
    public String getCaptcha(HttpSession session) throws IOException {
        String token=cage.getTokenGenerator().next();
        log.info("生成的验证码是：",token); //先存token到session中
        //再生成验证码图片
        byte[] image=cage.draw(token);
        return Base64.getEncoder().encodeToString(image);
    }

}
