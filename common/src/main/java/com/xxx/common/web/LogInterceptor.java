package com.xxx.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 简单请求日志拦截器：记录 URL 与耗时
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    private static final String START_TIME = "_req_start_time_";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("请求开始 URI: {}, 方法: {}", request.getRequestURI(), request.getMethod());
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) {
        Object start = request.getAttribute(START_TIME);
        if (start instanceof Long) {
            long cost = System.currentTimeMillis() - (Long) start;
            log.info("请求结束 URI: {}, 方法: {}, 耗时: {} ms", request.getRequestURI(), request.getMethod(), cost);
        }
    }
}


