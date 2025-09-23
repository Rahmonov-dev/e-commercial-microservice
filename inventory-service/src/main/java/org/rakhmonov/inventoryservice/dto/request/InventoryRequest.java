package org.rakhmonov.inventoryservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.rakhmonov.inventoryservice.entity.Inventory;
import org.rakhmonov.inventoryservice.entity.Product;
import org.rakhmonov.inventoryservice.entity.Warehouse;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @Min(value = 0, message = "Current stock cannot be negative")
    private Integer currentStock;

    @Min(value = 0, message = "Reorder point cannot be negative")
    private Integer reorderPoint;

    @DecimalMin(value = "0.0", inclusive = true, message = "Unit cost cannot be negative")
    private BigDecimal unitCost;

    public static Inventory toEntity(InventoryRequest request, Product product, Warehouse warehouse) {
        return Inventory.builder()
                .currentStock(request.getCurrentStock())
                .reorderPoint(request.getReorderPoint())
                .unitCost(request.getUnitCost())
                .product(product)
                .warehouse(warehouse)
                .build();
    }
}
