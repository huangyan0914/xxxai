package com.xxx.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局 Knife4j/OpenAPI3 配置
 * 各业务模块只需引入 knife4j starter 与 common 依赖，即可自动加载
 */
@Configuration
public class Knife4jConfig implements WebMvcConfigurer {

    /**
     * 统一 Knife4j 静态资源映射（doc.html / webjars）
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}


