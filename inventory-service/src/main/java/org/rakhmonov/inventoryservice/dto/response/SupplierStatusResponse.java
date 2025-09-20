package org.rakhmonov.inventoryservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SupplierStatusResponse {
    private Long supplierId;
    private int totalOrders;          // jami zakazlar soni
    private int successfulOrders;     // muvaffaqiyatli zakazlar soni
    private int cancelledOrders;      // bekor qilingan zakazlar soni
    private double successRate;       // muvaffaqiyatli zakazlar % (successfulOrders / totalOrders)
    private double cancelRate;        // bekor qilingan zakazlar % (cancelledOrders / totalOrders)
    private BigDecimal totalPurchases; // jami xarid summasi
}
