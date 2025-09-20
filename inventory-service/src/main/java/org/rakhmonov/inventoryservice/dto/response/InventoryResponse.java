package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;
import org.rakhmonov.inventoryservice.entity.Inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long id;
    private Integer currentStock;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer reorderPoint;
    private BigDecimal unitCost;
    private BigDecimal totalValue;
    private String stockStatus;

    private Long productId;
    private Long warehouseId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public static InventoryResponse toResponse(Inventory entity) {
        return InventoryResponse.builder()
                .id(entity.getId())
                .currentStock(entity.getCurrentStock())
                .minStockLevel(entity.getMinStockLevel())
                .maxStockLevel(entity.getMaxStockLevel())
                .reorderPoint(entity.getReorderPoint())
                .unitCost(entity.getUnitCost())
                .stockStatus(entity.getStockStatus())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
