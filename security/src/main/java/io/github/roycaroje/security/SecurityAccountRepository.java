package io.github.roycaroje.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecurityAccountRepository extends JpaRepository<SecurityAccount, Long> {

    boolean existsByUsername(String username);

    Optional<SecurityAccount> findByUsername(String username);
}
