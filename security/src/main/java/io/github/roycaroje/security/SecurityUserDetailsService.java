package io.github.roycaroje.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

public class SecurityUserDetailsService implements UserDetailsService {

    private final AccountUserRepository accountUserRepository;

    public SecurityUserDetailsService(AccountUserRepository accountUserRepository) {
        this.accountUserRepository = accountUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountUser user = accountUserRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(!user.isActive())
                .authorities(user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
                .build();
    }
}
