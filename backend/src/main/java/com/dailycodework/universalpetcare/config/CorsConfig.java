package com.dailycodework.universalpetcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 匹配所有接口
                        .allowedOrigins("http://34.246.135.101:3000") // 允许你的前端地址
                        .allowedMethods("*") // 允许所有 HTTP 方法：GET/POST/PUT/DELETE...
                        .allowedHeaders("*") // 允许所有请求头
                        .allowCredentials(true); // 如果你用到了 Cookie 或 Token 验证，启用这个
            }
        };
    }
}
