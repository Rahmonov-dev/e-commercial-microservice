// order-service/src/main/java/org/rakhmonov/orderservice/entity/WishlistItem.java
package org.rakhmonov.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_items", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"wishlist_id", "product_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    @ToString.Exclude
    private Wishlist wishlist;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "product_price")
    private Double productPrice;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_image_url")
    private String productImageUrl;
    
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
    
    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;
    
    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}