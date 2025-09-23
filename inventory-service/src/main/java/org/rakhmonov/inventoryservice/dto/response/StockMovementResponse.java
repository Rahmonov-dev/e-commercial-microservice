package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;
import org.rakhmonov.inventoryservice.entity.StockMovement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private StockMovement.MovementType movementType;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private Integer stockBefore;
    private Integer stockAfter;
    private String referenceNumber;
    private String referenceType;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;

    // Computed fields
    private Boolean isInbound;
    private Boolean isOutbound;
    private Integer effectiveQuantity;

    public static StockMovementResponse toResponse(StockMovement entity) {
        return StockMovementResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .warehouseName(entity.getWarehouse() != null ? entity.getWarehouse().getName() : null)
                .movementType(entity.getMovementType())
                .quantity(entity.getQuantity())
                .unitCost(entity.getUnitCost())
                .totalCost(entity.getTotalCost())
                .stockBefore(entity.getStockBefore())
                .stockAfter(entity.getStockAfter())
                .referenceNumber(entity.getReferenceNumber())
                .referenceType(entity.getReferenceType())
                .notes(entity.getNotes())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .isInbound(entity.isInbound())
                .isOutbound(entity.isOutbound())
                .effectiveQuantity(entity.getEffectiveQuantity())
                .build();
    }
}


