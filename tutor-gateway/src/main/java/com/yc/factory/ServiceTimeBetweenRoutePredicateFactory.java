package com.yc.factory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/***
  自定义谓词工厂  用于判断服务时间是否在指定时间段内
  1.创建一个类继承AbstractRoutePredicateFactory<CustomTimeBetweenConfig>
  2.创建一个配置类CustomTimeBetweenConfig 用于接收yml中的配置
  3.创建一个方法apply(CustomTimeBetweenConfig config) 用于判断服务时间是否在指定时间段内
 */
@Component
@Slf4j
public class ServiceTimeBetweenRoutePredicateFactory extends AbstractRoutePredicateFactory<ServiceTimeBetweenRoutePredicateFactory.CustomTimeBetweenConfig> {

    public ServiceTimeBetweenRoutePredicateFactory() {
        super(CustomTimeBetweenConfig.class);
    }

    //业务逻辑判断
    @Override
    public Predicate<ServerWebExchange> apply(CustomTimeBetweenConfig config) {
        //获取参数值
        LocalTime startTime = config.getStartTime();
        LocalTime endTime = config.getEndTime();
        //创建谓词工厂
        return new Predicate<ServerWebExchange>() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {

                //ServerWebExchange  就是HttpServletRequest请求对象
                //serverWebExchange.getRequest().getCookies()
                //serverWebExchange.getResponse().getCookies()
                log.info("自定义断言工厂服务时间在{}到{}之间", startTime, endTime);
                //获取当前时间
                LocalTime now = LocalTime.now();
                //判断当前时间是否在指定时间段内
                return now.isAfter(startTime) && now.isBefore(endTime);
            }
        };
    }
    //用于接收yml中的配置CustomTimeBetweenConfig  上午6:00 下午12:00
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("startTime", "endTime");
    }

    //配置类
    @Data
    public static class CustomTimeBetweenConfig {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
