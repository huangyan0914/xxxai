package com.xxx.common.auth;

/**
 * 基于 ThreadLocal 的当前登录用户上下文，供业务层调用。
 * <p>
 * 由 {@link com.xxx.common.web.AuthInterceptor} 在请求前置阶段填充，
 * 并在请求结束后清理，避免内存泄漏。
 * </p>
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.getUserId();
    }

    public static String getUsername() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.getUsername();
    }

    public static void clear() {
        HOLDER.remove();
    }
}

