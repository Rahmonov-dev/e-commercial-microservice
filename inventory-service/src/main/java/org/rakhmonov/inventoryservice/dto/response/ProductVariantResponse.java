package org.rakhmonov.inventoryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.inventoryservice.entity.ProductVariant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {
    
    private Long id;
    private String variantName; // e.g., "Size", "Color", "Material"
    private String variantValue; // e.g., "Large", "Red", "Cotton"
    private String sku; // Unique SKU for this variant
    private String barcode;
    private BigDecimal price; // Override price for this variant
    private BigDecimal cost; // Cost for this variant
    private BigDecimal weight;
    private String dimensions; // "LxWxH" format
    private String imageUrl; // Specific image for this variant
    private Integer stockQuantity;
    private Boolean isDefault;
    private ProductVariant.VariantStatus status;
    private Integer sortOrder; // For display ordering
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    private Boolean isInStock;
    private Boolean hasPriceOverride;
    private BigDecimal effectivePrice;
    
    // Business logic methods
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0 && status == ProductVariant.VariantStatus.ACTIVE;
    }
    
    public boolean hasPriceOverride() {
        return price != null;
    }
    
    public BigDecimal getEffectivePrice() {
        return price != null ? price : BigDecimal.ZERO; // Will be set by service
    }
    
    public String getStatusText() {
        if (status == null) {
            return "Unknown";
        }
        return status.toString();
    }
    
    public String getDisplayName() {
        return variantName + ": " + variantValue;
    }
}




