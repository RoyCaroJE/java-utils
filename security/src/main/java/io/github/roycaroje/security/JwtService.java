package io.github.roycaroje.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String ROLES_CLAIM = "roles";

    private final JwtProperties properties;
    private final SecretKey signingKey;
    private final UserDetailsService userDetailsService;

    public JwtService(JwtProperties properties, UserDetailsService userDetailsService) {
        this.properties = properties;
        this.userDetailsService = userDetailsService;
        this.signingKey = resolveSigningKey(properties.getSecret());
    }

    private SecretKey resolveSigningKey(String rawSecret) {
        if (rawSecret == null || rawSecret.isBlank()) {
            throw new IllegalStateException("jwt.secret is required");
        }

        byte[] keyBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .claims(claims)
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getRefreshExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH)
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {
        return parseClaims(token) != null;
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseClaims(token);
        return claims != null && TOKEN_TYPE_REFRESH.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    public String extractSubject(String token) {
        Claims claims = parseClaims(token);
        if (claims == null) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        Claims body = parseRequiredClaims(token);
        String tokenType = body.get(TOKEN_TYPE_CLAIM, String.class);
        if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
            throw new IllegalArgumentException("JWT token is not an access token");
        }

        String username = body.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!userDetails.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private Claims parseClaims(String token) {
        try {
            return parseRequiredClaims(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    private Claims parseRequiredClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
