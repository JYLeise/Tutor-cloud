package com.yc.web.controllers;

import com.yc.bean.TutorUser;
import com.yc.service.TutorUserBiz;
import com.yc.utils.JwtTokenUtil;
import com.yc.web.model.ResponseResult;
import com.yc.web.model.TutorUserVO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/tutorsecurity")
public class UserController {

    @Autowired
    private TutorUserBiz tutorUserBiz;

    //注册
    @PostMapping("/register")
    public ResponseResult register(@RequestBody @Valid TutorUserVO tutorUserVO){
        try{
            int uid = this.tutorUserBiz.regUser( tutorUserVO );
            tutorUserVO.setUid( uid ); // 注册成功后，将用户id设置到resuser对象中
            tutorUserVO.setPassWord( "" ); // 注册成功后，将密码设置为""
            return ResponseResult.ok( "注册成功" ).setData( tutorUserVO );
        }catch (Exception e){
            log.error( "注册失败", e );
            return ResponseResult.error( "注册失败" );
        }
    }

    // 注入 认证管理器
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtUtil;

    //登录
    @PostMapping("/login")
    public ResponseResult login(@RequestBody TutorUserVO tutorUserVO , HttpSession session) {
        //获取验证码
        // String captcha = (String) session.getAttribute( "captcha" );
        // 验证码校验  验证码  忽略大小写
//        if(!captcha.equals( resuserVO.getCaptcha() ) ){
//            return ResponseResult.error( "验证码错误" );
//        }
        try {
            //用户认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(tutorUserVO.getUserId(),
                            tutorUserVO.getPassWord()));
            //将认证信息设置到 SecurityContext 中，表示用户已通过认证。
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //认证成功后，获取当前认证的用户信息。
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //生成jwt负载 JSON Web Token
            Map<String, String> payload = new HashMap<>();
            payload.put("uid", String.valueOf(((TutorUser) userDetails).getUid()));
            payload.put("userImg", ((TutorUser) userDetails).getUserImg());
            payload.put("userName", userDetails.getUsername());
            payload.put("userId", String.valueOf(String.valueOf(((TutorUser) userDetails).getUserId())));
            payload.put("phoneNumber", ((TutorUser) userDetails).getPhoneNumber());
            payload.put("userRealName", ((TutorUser) userDetails).getUserRealName());
            payload.put("userSex", ((TutorUser) userDetails).getUserSex());
            payload.put("userBornYear", String.valueOf(((TutorUser) userDetails).getUserBornYear()));
            payload.put("userRegional", ((TutorUser) userDetails).getUserRegional());
            payload.put("userDegree", ((TutorUser) userDetails).getUserDegree());
            payload.put("userIdentity", ((TutorUser) userDetails).getUserIdentity());
            payload.put("role", "user");
            payload.put("userEmail", ((TutorUser) userDetails).getUserEmail());
            //生成token
            String jwtToken = jwtUtil.encodeJWT(payload);

            return ResponseResult.ok("登录成功").setData(jwtToken);
        }catch (Exception e){
            log.error("登录失败，错误为+"+e.getMessage());
            return ResponseResult.error();
        }
    }

    //退出
    @PostMapping("/logout")
    public ResponseResult logout(@RequestHeader("Authorization") String token){
        //这里可以实现JWT 黑名单机制  或者让客户端删除存储的JWT
        //例如 将token 添加到redis黑名单中
        return ResponseResult.ok( "退出成功" );
    }

    //权限认证  只有登录成功后才能访问该接口
    @PostMapping("/check")
    public ResponseResult check(){
        log.info( "权限认证成功" );
        // 从 SecurityContext 中获取当前认证的用户信息。
        TutorUser tutorUser = (TutorUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tutorUser.setPassword( "" );
        return ResponseResult.ok( "成功" ).setData( tutorUser );
    }


}