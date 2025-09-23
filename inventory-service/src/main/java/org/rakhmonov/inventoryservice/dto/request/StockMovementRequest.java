package org.rakhmonov.inventoryservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.rakhmonov.inventoryservice.entity.Product;
import org.rakhmonov.inventoryservice.entity.StockMovement;
import org.rakhmonov.inventoryservice.entity.Warehouse;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    private Long warehouseId;

    @NotNull(message = "Movement type is required")
    private StockMovement.MovementType movementType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be positive")
    private Integer quantity;

    @DecimalMin(value = "0.0", message = "Unit cost cannot be negative")
    private BigDecimal unitCost;

    private String referenceNumber; // PO number, order number, etc.

    private String referenceType; // "PURCHASE_ORDER", "SALE", "ADJUSTMENT", etc.

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private String createdBy;

    public static StockMovement toEntity(StockMovementRequest request, Product product, Warehouse warehouse) {
        return StockMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .movementType(request.getMovementType())
                .quantity(request.getQuantity())
                .unitCost(request.getUnitCost())
                .referenceNumber(request.getReferenceNumber())
                .referenceType(request.getReferenceType())
                .notes(request.getNotes())
                .createdBy(request.getCreatedBy())
                .build();
    }
}


