package org.rakhmonov.inventoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {
    
    @NotBlank(message = "Variant name is required")
    private String variantName; // e.g., "Size", "Color", "Material"
    
    @NotBlank(message = "Variant value is required")
    private String variantValue; // e.g., "Large", "Red", "Cotton"
    
    private String sku; // Unique SKU for this variant
    
    private String barcode;
    
    @PositiveOrZero(message = "Price must be positive or zero")
    private BigDecimal price; // Override price for this variant
    
    @PositiveOrZero(message = "Cost must be positive or zero")
    private BigDecimal cost; // Cost for this variant
    
    @PositiveOrZero(message = "Weight must be positive or zero")
    private BigDecimal weight;
    
    private String dimensions; // "LxWxH" format
    
    private String imageUrl; // Specific image for this variant
    
    @PositiveOrZero(message = "Stock quantity must be positive or zero")
    @Builder.Default
    private Integer stockQuantity = 0;
    
    @Builder.Default
    private Boolean isDefault = false;
    
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED
    
    private Integer sortOrder; // For display ordering
}
