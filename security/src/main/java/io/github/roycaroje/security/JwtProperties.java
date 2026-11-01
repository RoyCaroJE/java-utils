package io.github.roycaroje.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret used for HMAC signing. Recommend at least 32 bytes (256 bits).
     * Can be raw text or base64 encoded.
     */
    private String secret;

    /** Access token expiration in milliseconds */
    private long expiration = 86_400_000; // 24 hours by default

    /** Refresh token expiration in milliseconds */
    private long refreshExpiration = 604_800_000; // 7 days by default

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}
