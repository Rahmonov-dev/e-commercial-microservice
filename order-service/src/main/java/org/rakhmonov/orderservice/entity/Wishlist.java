package org.rakhmonov.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wishlists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WishlistItem> items = new ArrayList<>();
    
    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addItem(WishlistItem item) {
        items.add(item);
        item.setWishlist(this);
    }
    
    public void removeItem(WishlistItem item) {
        items.remove(item);
        item.setWishlist(null);
    }
    
    public int getTotalItems() {
        return items.size();
    }
}
