package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;
import org.rakhmonov.inventoryservice.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private String barcode;
    private String imageUrl;
    private Boolean isActive;
    
    // Related entities (simplified)
    private CategorySummaryResponse category;
    private InventorySummaryResponse inventory;
    
    // Foreign keys to other services
    private Long thirdPartySellerId; // Reference to User Service
    private Long supplierId; // Reference to User Service
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Calculated fields
    public boolean isInStock() {
        return inventory != null && inventory.getCurrentStock() > 0;
    }
    
    public boolean needsReorder() {
        return inventory != null && inventory.needsReorder();
    }
    
    public String getStockStatus() {
        return inventory != null ? inventory.getStockStatus() : "UNKNOWN";
    }

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .category(product.getCategory() != null ? 
                    CategorySummaryResponse.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .build() : null)
                .thirdPartySellerId(product.getThirdPartySellerId())
                .supplierId(product.getSupplierId())
                .inventory(product.getInventory() != null ?
                    InventorySummaryResponse.builder()
                        .id(product.getInventory().getId())
                        .currentStock(product.getInventory().getCurrentStock())
                        .reorderPoint(product.getInventory().getReorderPoint())
                        .unitCost(product.getInventory().getUnitCost())
                        .stockStatus(product.getInventory().getStockStatus())
                        .build() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

