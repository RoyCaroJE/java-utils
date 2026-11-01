package io.github.roycaroje.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityAccountUserDetailsService implements UserDetailsService {

    private final SecurityAccountRepository accountRepository;

    public SecurityAccountUserDetailsService(SecurityAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityAccount account = accountRepository.findByUsername(username == null ? null : username.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .disabled(!account.isEnabled())
                .authorities(account.getRoleSet().toArray(String[]::new))
                .build();
    }
}
