package io.github.roycaroje.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret used for HMAC signing. Recommend at least 32 bytes (256 bits).
     * Can be raw text or base64 encoded.
     */
    private String secret;

    /** Expiration in milliseconds */
    private long accessTokenExpiration = 900_000; // 15 minutes

    /** Expiration in milliseconds */
    private long refreshTokenExpiration = 2_592_000_000L; // 30 days

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}

