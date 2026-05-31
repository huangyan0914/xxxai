package com.xxx.common.auth;

/**
 * 当前登录用户信息，由 AuthInterceptor 解析 JWT 后存入 UserContext（ThreadLocal）。
 */
public class LoginUser {

    private Long userId;
    private String username;
    private String realName;

    public LoginUser() {
    }

    public LoginUser(Long userId, String username, String realName) {
        this.userId = userId;
        this.username = username;
        this.realName = realName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}

