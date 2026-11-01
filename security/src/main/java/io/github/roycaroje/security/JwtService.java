package io.github.roycaroje.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties properties;
    @Nullable
    private final UserDetailsService userDetailsService;

    public JwtService(JwtProperties properties, @Nullable UserDetailsService userDetailsService) {
        this.properties = properties;
        this.userDetailsService = userDetailsService;
    }

    private Key signingKey() {
        String secret = properties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret must be configured");
        }

        byte[] keyBytes = decodeSecret(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] decodeSecret(String secret) {
        try {
            return Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = authorities == null
                ? Collections.emptyList()
                : authorities.stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", roles);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getExpiration());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(claims)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token);
        Claims body = jws.getBody();
        String username = body.getSubject();

        if (userDetailsService != null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
            } catch (Exception ignored) {
            }
        }

        Object rolesObj = body.get("roles");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (rolesObj instanceof Collection<?> roles) {
            for (Object role : roles) {
                authorities.add(new SimpleGrantedAuthority(String.valueOf(role)));
            }
        }

        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
