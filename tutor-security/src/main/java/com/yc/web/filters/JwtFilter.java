package com.yc.web.filters;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yc.service.TutorUserBiz;
import com.yc.utils.JwtTokenUtil;
import com.yc.web.model.ResponseResult;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//此过滤器作用: 1. 是否有token  2. token是否有效 3.验证用户名和密码是否匹配
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    // 使用 Jackson ObjectMapper 转换对象为 JSON
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtTokenUtil jwtUtils;

    @Autowired
    private TutorUserBiz tutorUserBiz;


    //Spring Security 过滤器的实现，用于处理 JWT（JSON Web Token）的验证
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //每次请求头中都要携带token  验证token是否有效
        String jwtTaken = request.getHeader("token"); // 从请求头中获取token
        if(jwtTaken != null && !jwtTaken.isEmpty() && !jwtUtils.isTokenExpired(jwtTaken)){
            try{  // token 有效
                //获取载荷  验证用户名和密码是否匹配
                //就是一个键值对的map  载荷中包含了用户名和密码、过期时间、签名等信息
                Claims claims = jwtUtils.decodeJWTWithkey(jwtTaken);
                String userId = (String) claims.get("userId"); // 从token中获取用户名
                //从数据库中获取用户信息
                UserDetails user = ((UserDetailsService)tutorUserBiz).loadUserByUsername(userId);
                if ( user != null){
                    //验证密码与权限是否正确
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user , null , user.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }else {
            response.setContentType("application/json;charset=utf-8");
            // 将 ResponseResult 转换为 JSON 字符串
            String jsonResponse = objectMapper.writeValueAsString(ResponseResult.error("登录已过期，请重新登录！"));
            response.getWriter().write(jsonResponse);
            return;
        }
        filterChain.doFilter(request,response); //继续执行过滤器链
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 定义需要忽略的路径列表
        List<String> excludePaths = Arrays.asList(
                "/api/security/login",
                "/api/security/register",
                "/api/security/captcha",
                "/api/security/api-docs/**",
                "/api/security/swagger-ui/**"
        );

        // 使用 Ant 风格路径匹配（支持通配符）
        return excludePaths.stream()
                .anyMatch(path -> new AntPathMatcher().match(path, request.getServletPath()));
    }
}