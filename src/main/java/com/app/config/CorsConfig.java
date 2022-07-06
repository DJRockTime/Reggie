package com.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class CorsConfig {

    private static final long MAX_AGE = 24 * 60 * 60;

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //corsConfiguration.setAllowCredentials(true); // 客户端携带验证信息 true : 允许  false: 不允许
        corsConfiguration.addAllowedOrigin("*");    // 设置访问源地址
        corsConfiguration.addAllowedHeader("*");   // 设置访问请求头
        corsConfiguration.addAllowedMethod("*");   // 设置请求方法
        corsConfiguration.setMaxAge(MAX_AGE);

        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}





