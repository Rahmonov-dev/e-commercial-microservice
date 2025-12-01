package org.rakhmonov.inventoryservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    private String sku;
    private String barcode;
    private MultipartFile imageUrl;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private Long thirdPartySellerId; // Reference to User Service
    private Long supplierId; // Reference to User Service
    
    private Integer currentStock;
    private Integer reorderPoint;
    private BigDecimal unitCost;
    private Long warehouseId; // Reference to Warehouse Service


}

