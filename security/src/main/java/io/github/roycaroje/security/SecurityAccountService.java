package io.github.roycaroje.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Service
public class SecurityAccountService {

    private final SecurityAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityAccountService(SecurityAccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SecurityAccount register(String username, String rawPassword) {
        String normalizedUsername = normalizeValue(username, "username");
        validatePassword(rawPassword);

        if (accountRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Username already exists");
        }

        SecurityAccount account = new SecurityAccount();
        account.setUsername(normalizedUsername);
        account.setPasswordHash(passwordEncoder.encode(rawPassword));
        account.setRoleSet(Set.of("ROLE_USER"));

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public SecurityAccount authenticate(String username, String rawPassword) {
        String normalizedUsername = normalizeValue(username, "username");
        validatePassword(rawPassword);

        SecurityAccount account = accountRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!account.isEnabled() || !passwordEncoder.matches(rawPassword, account.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return account;
    }

    @Transactional(readOnly = true)
    public SecurityAccount loadAccount(String username) {
        return accountRepository.findByUsername(normalizeValue(username, "username"))
                .orElseThrow(() -> new BadCredentialsException("User not found"));
    }

    @Transactional
    public SecurityAccount updateRoles(String username, Set<String> roles) {
        SecurityAccount account = loadAccount(username);
        account.setRoleSet(roles);
        return accountRepository.save(account);
    }

    @Transactional
    public SecurityAccount setEnabled(String username, boolean enabled) {
        SecurityAccount account = loadAccount(username);
        account.setEnabled(enabled);
        return accountRepository.save(account);
    }

    @Transactional
    public void updatePassword(String username, String newRawPassword) {
        validatePassword(newRawPassword);
        SecurityAccount account = loadAccount(username);
        account.setPasswordHash(passwordEncoder.encode(newRawPassword));
        accountRepository.save(account);
    }

    public Collection<? extends GrantedAuthority> toAuthorities(SecurityAccount account) {
        return account.getRoleSet().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private String normalizeValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim().toLowerCase();
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }
    }
}
