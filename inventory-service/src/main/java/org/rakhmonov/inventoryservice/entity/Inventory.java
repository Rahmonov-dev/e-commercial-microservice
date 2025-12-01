package org.rakhmonov.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", schema = "public")
@Data
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer currentStock = 0;


    @Builder.Default
    private Integer reorderPoint = 10;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal unitCost;
    
    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId; // Reference to Warehouse Service
    
    // Timestamps
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    // Explicit setter for isDeleted (Lombok sometimes has issues with Boolean is* fields)

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    // Business logic methods
    public String getStockStatus() {
        if (currentStock <= 0) return "OUT_OF_STOCK";
        if (currentStock <= reorderPoint) return "LOW_STOCK";
        return "IN_STOCK";
    }
    
    public boolean needsReorder() {
        return currentStock <= reorderPoint;
    }
}
