package org.rakhmonov.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product"})
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "variant_name", nullable = false)
    private String variantName; // e.g., "Size", "Color", "Material"
    
    @Column(name = "variant_value", nullable = false)
    private String variantValue; // e.g., "Large", "Red", "Cotton"
    
    @Column(name = "sku", unique = true)
    private String sku; // Unique SKU for this variant
    
    @Column(name = "barcode")
    private String barcode;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price; // Override price for this variant
    
    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost; // Cost for this variant
    
    @Column(name = "weight")
    private BigDecimal weight;
    
    @Column(name = "dimensions")
    private String dimensions; // "LxWxH" format
    
    @Column(name = "image_url")
    private String imageUrl; // Specific image for this variant
    
    @Column(name = "stock_quantity")
    @Builder.Default
    private Integer stockQuantity = 0;
    
    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VariantStatus status = VariantStatus.ACTIVE;
    
    @Column(name = "sort_order")
    private Integer sortOrder; // For display ordering
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum VariantStatus {
        ACTIVE,         // Available for sale
        INACTIVE,       // Not available
        OUT_OF_STOCK,   // Temporarily out of stock
        DISCONTINUED    // Permanently discontinued
    }
    
    // Business logic methods
    public BigDecimal getEffectivePrice() {
        return price != null ? price : product.getPrice();
    }
    
    public boolean isInStock() {
        return stockQuantity > 0 && status == VariantStatus.ACTIVE;
    }
    
    public boolean canReserve(int quantity) {
        return stockQuantity >= quantity && status == VariantStatus.ACTIVE;
    }
    
    public void reserveStock(int quantity) {
        if (canReserve(quantity)) {
            this.stockQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Insufficient stock to reserve");
        }
    }
    
    public void releaseReservedStock(int quantity) {
        this.stockQuantity += quantity;
    }
    
    public void sellStock(int quantity) {
        if (canReserve(quantity)) {
            this.stockQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Insufficient stock to sell");
        }
    }
    
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
}




