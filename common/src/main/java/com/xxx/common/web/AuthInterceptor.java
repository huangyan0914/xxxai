package com.xxx.common.web;

import com.xxx.common.auth.AuthProperties;
import com.xxx.common.auth.JwtUtil;
import com.xxx.common.auth.LoginUser;
import com.xxx.common.auth.UserContext;
import com.xxx.common.resp.Resp;
import com.xxx.common.resp.RespCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 鉴权拦截器：从 Authorization 请求头读取 Bearer Token，解析后写入 UserContext。
 * <p>
 * 白名单路径（/api/auth/captcha、/api/auth/login 及 Swagger/Knife4j 文档路径）
 * 在 {@link WebMvcInterceptorConfig} 中通过 excludePathPatterns 排除，无需在此处理。
 * </p>
 * <p>
 * 当 {@code auth.use-fixed-admin=true} 时，若请求不携带 Authorization 头，
 * 自动以固定管理员用户（admin / userId=5）身份通过鉴权，仅供开发调试使用。
 * </p>
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /** 固定管理员用户：userId=5，username=admin，realName=涞水大X */
    private static final LoginUser FIXED_ADMIN = new LoginUser(5L, "admin", "涞水大X");

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AuthProperties authProperties;

    public AuthInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper, AuthProperties authProperties) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.authProperties = authProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
            if (authProperties.isUseFixedAdmin()) {
                UserContext.set(FIXED_ADMIN);
                return true;
            }
            writeUnauthorized(response);
            return false;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        LoginUser loginUser = jwtUtil.parseToken(token);
        if (loginUser == null) {
            writeUnauthorized(response);
            return false;
        }
        UserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Resp.fail(RespCode.UNAUTHORIZED)));
    }
}

