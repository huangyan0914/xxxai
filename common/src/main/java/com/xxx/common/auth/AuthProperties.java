package com.xxx.common.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 鉴权配置属性，在 application.yml 中通过 auth.* 前缀配置。
 * <pre>
 * auth:
 *   use-fixed-admin: false
 * </pre>
 */
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * 是否启用固定管理员用户模式。
     * 开启后，当请求不携带 Authorization 头时，自动以固定的 admin 用户身份通过鉴权，
     * 仅用于本地开发/调试，生产环境请保持 false。
     */
    private boolean useFixedAdmin = false;

    public boolean isUseFixedAdmin() {
        return useFixedAdmin;
    }

    public void setUseFixedAdmin(boolean useFixedAdmin) {
        this.useFixedAdmin = useFixedAdmin;
    }
}

