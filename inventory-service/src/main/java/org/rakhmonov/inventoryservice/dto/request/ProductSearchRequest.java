package org.rakhmonov.inventoryservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {
    // Search criteria
    private String searchQuery;
    private Long categoryId;
    private Long thirdPartySellerId;
    
    // Price range
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    // Stock criteria
    private Boolean inStock;
    private Boolean needsReorder;
    
    // Sorting
    private String sortBy; // "name", "price", "createdAt", "updatedAt"
    private String sortDirection; // "ASC", "DESC"
    
    // Pagination
    @Min(value = 0, message = "Page number cannot be negative")
    @Builder.Default
    private int page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    @Builder.Default
    private int size = 20;
    
    // Additional filters
    private String sku;
    private String barcode;
}
