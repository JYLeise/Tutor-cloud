package com.yc.web.filters;

import com.yc.common.ExcludePathConfig;
import com.yc.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TokenGlobalFilter implements GlobalFilter, Ordered {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ExcludePathConfig excludePathConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        AntPathMatcher matcher = new AntPathMatcher();
        // 排除路径检查
        boolean shouldSkip = excludePathConfig.getUrls().stream()
                .anyMatch(pattern -> matcher.match(pattern, path));
        if (shouldSkip) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (token == null || jwtTokenUtil.isTokenExpired(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100; // 确保优先执行
    }
}