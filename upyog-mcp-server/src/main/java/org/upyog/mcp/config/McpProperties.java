package org.upyog.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime settings for gateway access, confirmation tokens, limits, and Redis.
 * Secrets come from the environment ({@code UPYOG_MCP_TOKEN_SECRET}, {@code REDIS_URI}).
 */
@ConfigurationProperties(prefix = "upyog.mcp")
public class McpProperties {

    private String gatewayBaseUrl = "http://localhost:8080";
    private String uiBaseUrl = "http://localhost:3000";
    private String tokenSecret = "";
    private Duration confirmationTtl = Duration.ofMinutes(5);
    private Duration readTimeout = Duration.ofSeconds(8);
    private Duration writeTimeout = Duration.ofSeconds(20);
    private Duration connectTimeout = Duration.ofSeconds(3);
    private int maxPageSize = 20;
    private int maxResponseChars = 20_000;
    private int maxFreeTextChars = 1_000;
    private int maxUploadBytes = 2_000_000;
    private int rateLimitPerMinute = 60;
    private Redis redis = new Redis();
    private List<String> allowedGatewayPrefixes = new ArrayList<>();

    public String getGatewayBaseUrl() {
        return gatewayBaseUrl;
    }

    public void setGatewayBaseUrl(String gatewayBaseUrl) {
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public String getUiBaseUrl() {
        return uiBaseUrl;
    }

    public void setUiBaseUrl(String uiBaseUrl) {
        this.uiBaseUrl = uiBaseUrl;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public Duration getConfirmationTtl() {
        return confirmationTtl;
    }

    public void setConfirmationTtl(Duration confirmationTtl) {
        this.confirmationTtl = confirmationTtl;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public int getMaxResponseChars() {
        return maxResponseChars;
    }

    public void setMaxResponseChars(int maxResponseChars) {
        this.maxResponseChars = maxResponseChars;
    }

    public int getMaxFreeTextChars() {
        return maxFreeTextChars;
    }

    public void setMaxFreeTextChars(int maxFreeTextChars) {
        this.maxFreeTextChars = maxFreeTextChars;
    }

    public int getMaxUploadBytes() {
        return maxUploadBytes;
    }

    public void setMaxUploadBytes(int maxUploadBytes) {
        this.maxUploadBytes = maxUploadBytes;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void setRateLimitPerMinute(int rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public List<String> getAllowedGatewayPrefixes() {
        return allowedGatewayPrefixes;
    }

    public void setAllowedGatewayPrefixes(List<String> allowedGatewayPrefixes) {
        this.allowedGatewayPrefixes = allowedGatewayPrefixes;
    }

    public static class Redis {
        private boolean enabled;
        private String uri = "redis://localhost:6379";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
