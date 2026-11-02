package io.github.roycaroje.security;

import io.github.roycaroje.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
        name = "user_role",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_role_user_public_id_role",
                columnNames = {"user_public_id", "role"}
        )
)
public class UserRole extends BaseEntity {

    @Column(name = "user_public_id", nullable = false, length = 50)
    private UUID userPublicId;

    @Column(name = "role", nullable = false, length = 100)
    private String role;

    protected UserRole() {
    }

    public UserRole(UUID userPublicId, String role) {
        this.userPublicId = userPublicId;
        this.role = role;
    }

    public UUID getUserPublicId() {
        return userPublicId;
    }

    public void setUserPublicId(UUID userPublicId) {
        this.userPublicId = userPublicId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
