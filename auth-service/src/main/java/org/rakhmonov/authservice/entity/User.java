package org.rakhmonov.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"role"}) // Exclude role from toString to prevent circular reference
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnoreProperties({"permissions"}) // Prevent circular reference
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        
        // Add role authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
        
        // Add permission authorities
        if (role != null && role.getPermissions() != null) {
            authorities.addAll(
                    role.getPermissions().stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                            .toList()
            );
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return status == UserStatus.ACTIVE;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDeleted == null) {
            isDeleted = false;
        }
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE
    }
}
