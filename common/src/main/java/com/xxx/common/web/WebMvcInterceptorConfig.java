package com.xxx.common.web;

import com.xxx.common.auth.AuthProperties;
import com.xxx.common.auth.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 统一注册拦截器：日志拦截 + JWT 鉴权拦截
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class WebMvcInterceptorConfig implements WebMvcConfigurer {

    /** 不需要鉴权的路径白名单 */
    private static final String[] AUTH_WHITELIST = {
            "/api/auth/captcha",
            "/api/auth/login",
            // Knife4j / Swagger 文档
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/favicon.ico"
    };

    private final LogInterceptor logInterceptor;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AuthProperties authProperties;

    @Autowired
    public WebMvcInterceptorConfig(LogInterceptor logInterceptor,
                                   JwtUtil jwtUtil,
                                   ObjectMapper objectMapper,
                                   AuthProperties authProperties) {
        this.logInterceptor = logInterceptor;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.authProperties = authProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 日志拦截器：记录所有请求
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**");

        // JWT 鉴权拦截器：排除白名单路径
        registry.addInterceptor(new AuthInterceptor(jwtUtil, objectMapper, authProperties))
                .addPathPatterns("/**")
                .excludePathPatterns(AUTH_WHITELIST);
    }
}


