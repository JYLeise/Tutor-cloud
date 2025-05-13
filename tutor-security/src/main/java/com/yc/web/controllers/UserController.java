package com.yc.web.controllers;

import com.yc.bean.TutorUser;
import com.yc.service.FileBiz;
import com.yc.service.TutorUserBiz;
import com.yc.utils.JwtTokenUtil;
import com.yc.web.model.ResponseResult;
import com.yc.web.model.TutorUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.concurrent.CompletableFuture;

@Tag(name="用户信息操作接口")
@RestController
@Slf4j
@RequestMapping("/api/security")
public class UserController {

    @Autowired
    private TutorUserBiz tutorUserBiz;
    @Autowired
    private FileBiz fileBiz;
    // 注入 认证管理器
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtUtil;
    //注册
    @Operation(summary = "用户注册接口")
    @PostMapping("/register")
    public ResponseResult register(@Parameter(description = "用户信息") @RequestBody @Valid TutorUserVO tutorUserVO){
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


    //登录
    @Operation(summary = "用户登录接口")
    @PostMapping("/login")
    public ResponseResult login(@Parameter(description = "用户账号密码") @RequestBody TutorUserVO tutorUserVO , HttpSession session) {
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
            payload.put("userIdCard", ((TutorUser) userDetails).getUserIdCard());
            payload.put("userRegional", ((TutorUser) userDetails).getUserRegional());
            payload.put("userDegree", ((TutorUser) userDetails).getUserDegree());
            payload.put("userIdentity", ((TutorUser) userDetails).getUserIdentity());
            payload.put("userTeachImg", ((TutorUser) userDetails).getUserEmail());
            payload.put("userTeachWage", ((TutorUser) userDetails).getUserEmail());
            payload.put("userTeachGrade", ((TutorUser) userDetails).getUserEmail());
            payload.put("userTeachType", ((TutorUser) userDetails).getUserEmail());
            payload.put("userTeachWay", ((TutorUser) userDetails).getUserEmail());
            payload.put("userTeachMotto", ((TutorUser) userDetails).getUserEmail());
            payload.put("userEmail", ((TutorUser) userDetails).getUserEmail());
            payload.put("userRole",  ((TutorUser) userDetails).getUserRole());

            //生成token
            String jwtToken = jwtUtil.encodeJWT(payload);

            return ResponseResult.ok("登录成功").setData(jwtToken);
        }catch (Exception e){
            log.error("登录失败，错误为+"+e.getMessage());
            return ResponseResult.error();
        }
    }

    //退出
    @Operation(summary = "用户登出接口")
    @PostMapping("/logout")
    public ResponseResult logout(@Parameter(description = "token") @RequestHeader("Authorization") String token){
        //这里可以实现JWT 黑名单机制  或者让客户端删除存储的JWT
        //例如 将token 添加到redis黑名单中
        return ResponseResult.ok( "退出成功" );
    }

    //申请为教师
    @Operation(summary = "普通用户申请为教师接口")
    @PostMapping("/subtutor")
    public ResponseResult subtutor(@Parameter(description = "用户的教师信息") @ModelAttribute TutorUserVO tutorUserVO){
        try{
            String imgFilePath = "";  // 上传图片的位置
            //异步上传图片  异步操作
            CompletableFuture<String> fileUrlFuture = fileBiz.upload(tutorUserVO.getImgfile());
            //阻塞当前线程直到异步操作完成
            imgFilePath = fileUrlFuture.get();

            TutorUser tutorUser = new TutorUser(); //PO对象，数据库表的字段

            //VO -> PO  字段的复制
            //忽略  fphotofile  字段  不复制
            BeanUtils.copyProperties(tutorUserVO, tutorUser);

            tutorUser.setUserTeachImg(imgFilePath); // OSS中图片的地址存入PO中
            tutorUserBiz.updateUserToTutor(tutorUser);// 插入数据库  返回主键值
            tutorUserVO.setUserTeachImg(imgFilePath); // OSS中图片的地址存入VO中
            //增加用户的教师数据
            return ResponseResult.ok();
        }catch (Exception e){
            e.printStackTrace();
            // 记录详细的错误信息
            log.error("更新教师信息失败", e);
            log.error("申请教师失败，错误为："+e.getMessage());
            return ResponseResult.error();
        }
    }


    //权限认证  只有登录成功后才能访问该接口
    @Operation(summary = "权限验证接口")
    @PostMapping("/check")
    public ResponseResult check() {
        // 从 SecurityContext 中获取当前认证的用户信息。
        TutorUser tutorUser = (TutorUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tutorUser.setPassword("");
        if (tutorUser != null) {
            return ResponseResult.ok("成功").setData(tutorUser);
        }else{
            return ResponseResult.error();
        }
    }
}