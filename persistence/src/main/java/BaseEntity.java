import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @Column(name = "public_id", nullable = false, updatable = false, unique = true)
    private UUID publicId;

    @Column(name = "version", nullable = false)
    @Version
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        this.publicId = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseEntity that = (BaseEntity) o;

        if (publicId == null || that.publicId == null) {
            return false;
        }

        return publicId.equals(that.publicId);
    }

    @Override
    public int hashCode() {
        return publicId != null ? publicId.hashCode() : System.identityHashCode(this);
    }

}