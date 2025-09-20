package org.rakhmonov.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import java.time.LocalDateTime;

@Entity
@Table(name = "permissions", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString // No circular reference since we removed the roles field
public class Permission implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "resource", length = 100)
    private String resource; // e.g., "USER", "PRODUCT", "ORDER"

    @Column(name = "action", length = 50)
    private String action; // e.g., "CREATE", "READ", "UPDATE", "DELETE"

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    // Removed reverse relationship to prevent circular reference
    // @ManyToMany(mappedBy = "permissions", fetch = FetchType.EAGER)
    // private List<Role> roles;

    // Spring Security GrantedAuthority implementation
    @Override
    public String getAuthority() {
        return this.name;
    }

    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
