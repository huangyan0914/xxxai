package com.xxx.aimodel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lazycraft")
public class LazyCraftProperties {

    private String baseUrl = "http://localhost:30382";

    private String consoleApiPrefix = "/console/api";

    private String username = "admin";

    private String password = "LazyCraft@2025";

    private long tokenTtlSeconds = 3300L;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = trimTrailingSlash(baseUrl);
    }

    public String getConsoleApiPrefix() {
        return consoleApiPrefix;
    }

    public void setConsoleApiPrefix(String consoleApiPrefix) {
        this.consoleApiPrefix = consoleApiPrefix;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTokenTtlSeconds() {
        return tokenTtlSeconds;
    }

    public void setTokenTtlSeconds(long tokenTtlSeconds) {
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    public String consoleApiBaseUrl() {
        return trimTrailingSlash(baseUrl) + ensureLeadingSlash(consoleApiPrefix);
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String ensureLeadingSlash(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.startsWith("/") ? value : "/" + value;
    }
}

