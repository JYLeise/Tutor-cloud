package com.yc.configs;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// spring doc显示界面的主题信息
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
               .info(new io.swagger.v3.oas.models.info.Info()
                       .title("乐智家教")
                       .version("1.0")
                       .description("用户认证鉴权API文档")
                );
    }
}
