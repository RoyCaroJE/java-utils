package io.github.roycaroje.security.dto;

import java.time.Instant;
import java.util.Set;

public record AccountProfileResponse(
        Long id,
        String email,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
