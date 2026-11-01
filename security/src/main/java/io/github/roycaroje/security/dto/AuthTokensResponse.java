package io.github.roycaroje.security.dto;

public record AuthTokensResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMs
) {
}
