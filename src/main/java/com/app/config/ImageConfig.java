package com.app.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
//@Configuration
public class ImageConfig implements WebMvcConfigurer {

    @Value("${reggie.path}")
    private String fileLocalPath;

    /**
     * 映射外部资源
     * @param registry 注册器对象
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/app/images/**")
                .addResourceLocations("file:" + fileLocalPath);
    }

    /**
     * 映射 resource 资源
     * @param registry 注册器对象
     * addResourceHandler 指的是对外暴露的静态资源访问路径
     * addResourceLocations 指的是静态资源文件实际放置的目录
     *
     * addResourceHandler("/images/**")表示凡事以 /images/ 路径发起请求的，按照 addResourceLocations("file:"+fileSavePath)的路径进行映射
     */
    //@Override
    //public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //    registry.addResourceHandler("/images/**")
    //            .addResourceLocations("classpath:images");
    //}
}
