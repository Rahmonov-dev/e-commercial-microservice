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
@Table(name = "stock_movements", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product", "warehouse"})
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_cost", precision = 10, scale = 2)
    private BigDecimal unitCost;
    
    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;
    
    @Column(name = "stock_before")
    private Integer stockBefore;
    
    @Column(name = "stock_after")
    private Integer stockAfter;
    
    @Column(name = "reference_number")
    private String referenceNumber; // PO number, order number, etc.
    
    @Column(name = "reference_type")
    private String referenceType; // "PURCHASE_ORDER", "SALE", "ADJUSTMENT", etc.
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        if (unitCost != null && quantity != null) {
            this.totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    public enum MovementType {
        IN,             // Stock coming in (purchase, return, adjustment)
        OUT,            // Stock going out (sale, damage, adjustment)
        TRANSFER_IN,    // Transfer from another warehouse
        TRANSFER_OUT,   // Transfer to another warehouse
        ADJUSTMENT,     // Manual adjustment
        RESERVED,       // Stock reserved for order
        RELEASED        // Reserved stock released
    }
    
    // Helper methods
    public boolean isInbound() {
        return movementType == MovementType.IN || 
               movementType == MovementType.TRANSFER_IN ||
               movementType == MovementType.RELEASED;
    }
    
    public boolean isOutbound() {
        return movementType == MovementType.OUT || 
               movementType == MovementType.TRANSFER_OUT ||
               movementType == MovementType.RESERVED;
    }
    
    public int getEffectiveQuantity() {
        return isInbound() ? quantity : -quantity;
    }
}




