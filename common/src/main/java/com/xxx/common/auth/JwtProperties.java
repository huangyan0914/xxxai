package com.xxx.common.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置属性，在 application.yml 中通过 jwt.* 前缀配置。
 * <pre>
 * jwt:
 *   secret: your-secret-key-min-32-chars
 *   expire-seconds: 86400
 * </pre>
 */
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 签名密钥（Base64 编码或明文，至少 32 字节建议 64 字节） */
    private String secret = "x-wxwh-default-jwt-secret-key-please-change-it-in-production";

    /** Token 有效期（秒），默认 24 小时 */
    private long expireSeconds = 86400L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}

