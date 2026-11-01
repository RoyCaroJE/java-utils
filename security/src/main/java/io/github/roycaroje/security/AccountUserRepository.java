package io.github.roycaroje.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {

    Optional<AccountUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
