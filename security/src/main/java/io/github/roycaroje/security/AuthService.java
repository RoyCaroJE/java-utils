package io.github.roycaroje.security;

import io.github.roycaroje.security.dto.AuthTokensResponse;
import io.github.roycaroje.security.dto.LoginRequest;
import io.github.roycaroje.security.dto.RefreshTokenRequest;
import io.github.roycaroje.security.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;

@Service
public class AuthService {

    private final AccountUserRepository accountUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(AccountUserRepository accountUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService, JwtProperties jwtProperties) {
        this.accountUserRepository = accountUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthTokensResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (accountUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        AccountUser user = new AccountUser();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of("ROLE_USER"));
        user.setActive(true);

        AccountUser saved = accountUserRepository.save(user);
        return issueTokens(saved);
    }

    @Transactional(readOnly = true)
    public AuthTokensResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        AccountUser user = accountUserRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isActive()) {
            throw new DisabledException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public AuthTokensResponse refresh(RefreshTokenRequest request) {
        if (!jwtService.isRefreshToken(request.refreshToken())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String email = jwtService.extractSubject(request.refreshToken());
        AccountUser user = accountUserRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (!user.isActive()) {
            throw new DisabledException("Account is disabled");
        }

        return issueTokens(user);
    }

    private AuthTokensResponse issueTokens(AccountUser user) {
        var authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        String accessToken = jwtService.generateAccessToken(user.getEmail(), authorities);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthTokensResponse(accessToken, refreshToken, "Bearer", jwtProperties.getExpiration());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
