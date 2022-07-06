package com.app.config;

import com.app.filter.LoginCheckFilter;
import com.app.filter.WebCorsFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Slf4j
@Configuration
public class FilterConfig {
    @Bean
    public Filter getCorsFilter() {
        return new WebCorsFilter();
    }

    @Bean
    public Filter getLoginCheckFilter() {
        return new LoginCheckFilter();
    }



    @Bean
    public FilterRegistrationBean<WebCorsFilter> filterRegistrationBean1() {
        FilterRegistrationBean<WebCorsFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter((WebCorsFilter) this.getCorsFilter());
        filterRegistrationBean.setName("WebCorsFilter");
        filterRegistrationBean.addUrlPatterns("*");
        filterRegistrationBean.setOrder(-99);

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<LoginCheckFilter> filterRegistrationBean2() {
        FilterRegistrationBean<LoginCheckFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter((LoginCheckFilter) this.getLoginCheckFilter());
        filterRegistrationBean.setName("LoginCheckFilter");
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);

        return filterRegistrationBean;
    }


}
