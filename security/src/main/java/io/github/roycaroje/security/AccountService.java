package io.github.roycaroje.security;

import io.github.roycaroje.security.dto.AccountProfileResponse;
import io.github.roycaroje.security.dto.ChangePasswordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.Locale;

@Service
public class AccountService {

    private final AccountUserRepository accountUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountUserRepository accountUserRepository, PasswordEncoder passwordEncoder) {
        this.accountUserRepository = accountUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public AccountProfileResponse me() {
        AccountUser user = getCurrentUser();
        return new AccountProfileResponse(
                user.getId(),
                user.getEmail(),
                new LinkedHashSet<>(user.getRoles()),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        AccountUser user = getCurrentUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is invalid");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        accountUserRepository.save(user);
    }

    @Transactional
    public void deactivateOwnAccount() {
        AccountUser user = getCurrentUser();
        user.setActive(false);
        accountUserRepository.save(user);
    }

    private AccountUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String principalName && !"anonymousUser".equals(principalName)) {
            email = principalName;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return accountUserRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }
}
