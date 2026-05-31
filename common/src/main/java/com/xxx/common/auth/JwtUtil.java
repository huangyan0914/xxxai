package com.xxx.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT Token 工具类，提供 Token 的生成与解析功能。
 */
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_REAL_NAME = "realName";

    private final Key signingKey;
    private final long expireSeconds;

    public JwtUtil(String secret, long expireSeconds) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // 确保密钥至少 256 位（32 字节）
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret 长度不足 32 字节，请修改配置");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expireSeconds = expireSeconds;
    }

    /**
     * 生成 JWT Token。
     */
    public String generateToken(Long userId, String username, String realName) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expireSeconds * 1000L);
        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_REAL_NAME, realName)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token，成功则返回 {@link LoginUser}，失败（过期/签名错误）返回 null。
     */
    public LoginUser parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Long userId = claims.get(CLAIM_USER_ID, Long.class);
            String username = claims.getSubject();
            String realName = claims.get(CLAIM_REAL_NAME, String.class);
            return new LoginUser(userId, username, realName);
        } catch (JwtException e) {
            log.debug("JWT 解析失败: {}", e.getMessage());
            return null;
        }
    }
}

