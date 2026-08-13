package io.github.roycaroje.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
import java.util.*;
import java.util.stream.Collectors;

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
        byte[] keyBytes = properties.getSecret() != null ? properties.getSecret().getBytes(StandardCharsets.UTF_8) : new byte[0];
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = authorities == null ? Collections.emptyList() : authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        claims.put("roles", roles);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getAccessTokenExpiration());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(claims)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getRefreshTokenExpiration());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token);
        Claims body = jws.getBody();
        String username = body.getSubject();

        // Try to load full UserDetails if available in the application context
        if (userDetailsService != null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            } catch (Exception ignored) {
            }
        }

        // Fallback: build authorities from token claims
        Object rolesObj = body.get("roles");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (rolesObj instanceof Collection) {
            Collection<?> roles = (Collection<?>) rolesObj;
            for (Object r : roles) {
                authorities.add(new SimpleGrantedAuthority(String.valueOf(r)));
            }
        }

        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}

