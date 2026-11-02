package io.github.roycaroje.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Locale;

public class SecurityUserDetailsService implements UserDetailsService {

    private final AccountUserRepository accountUserRepository;
    private final UserRoleRepository userRoleRepository;

    public SecurityUserDetailsService(AccountUserRepository accountUserRepository, UserRoleRepository userRoleRepository) {
        this.accountUserRepository = accountUserRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountUser user = accountUserRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(!user.isActive())
                .authorities(userRoleRepository.findByUserPublicId(user.getPublicId()).stream()
                        .map(UserRole::getRole)
                        .map(this::toAuthority)
                        .map(SimpleGrantedAuthority::new)
                        .toList())
                .build();
    }

    private String toAuthority(String role) {
        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
        return normalizedRole.startsWith("ROLE_") ? normalizedRole : "ROLE_" + normalizedRole;
    }
}
