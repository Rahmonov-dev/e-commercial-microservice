package org.rakhmonov.inventoryservice.dto.request;

import lombok.Data;

@Data
public class DecreaseStockRequest {
    private Long productId;
    private Integer quantity;
    private Long warehouseId; // Optional
}







