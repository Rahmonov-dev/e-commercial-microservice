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
    private ThirdPartySellerSummaryResponse thirdPartySeller;
    private InventorySummaryResponse inventory;
    
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
        return null;
    }
}

