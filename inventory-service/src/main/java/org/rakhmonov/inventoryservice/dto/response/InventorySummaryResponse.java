package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryResponse {
    private Long id;
    private Integer currentStock;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer reorderPoint;
    private BigDecimal unitCost;
    private BigDecimal totalValue;
    
    // Calculated fields
    public String getStockStatus() {
        if (currentStock <= 0) return "OUT_OF_STOCK";
        if (currentStock <= reorderPoint) return "LOW_STOCK";
        if (currentStock >= maxStockLevel) return "OVERSTOCKED";
        return "IN_STOCK";
    }
    
    public boolean needsReorder() {
        return currentStock <= reorderPoint;
    }
}

