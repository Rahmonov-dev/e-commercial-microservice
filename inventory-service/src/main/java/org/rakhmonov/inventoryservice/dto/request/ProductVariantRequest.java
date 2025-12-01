package org.rakhmonov.inventoryservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantRequest {
    
    @NotBlank(message = "Variant name is required")
    @Size(min = 2, max = 100, message = "Variant name must be between 2 and 100 characters")
    private String variantName; // e.g., "Size", "Color", "Material"
    
    @NotBlank(message = "Variant value is required")
    @Size(min = 1, max = 100, message = "Variant value must be between 1 and 100 characters")
    private String variantValue; // e.g., "Large", "Red", "Cotton"
    
    @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9-_]+$", message = "SKU can only contain uppercase letters, numbers, hyphens, and underscores")
    private String sku; // Unique SKU for this variant
    
    @Size(max = 50, message = "Barcode cannot exceed 50 characters")
    private String barcode;
    
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price; // Override price for this variant
    
    @DecimalMin(value = "0.0", message = "Cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Cost must have at most 8 integer digits and 2 decimal places")
    private BigDecimal cost; // Cost for this variant
    
    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    private BigDecimal weight;
    
    @Size(max = 50, message = "Dimensions cannot exceed 50 characters")
    private String dimensions; // "LxWxH" format
    
    private MultipartFile imageUrl; // Specific image for this variant
    
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    
    @Builder.Default
    private Boolean isDefault = false;
    
    @Min(value = 0, message = "Sort order cannot be negative")
    private Integer sortOrder; // For display ordering
    
    // Validation methods
    public boolean hasPriceOverride() {
        return price != null;
    }
    
    public boolean hasStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
}




