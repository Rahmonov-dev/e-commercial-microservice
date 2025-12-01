package org.rakhmonov.inventoryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierResponseDto {

    private Long id;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String website;

    private String supplierType;
    private String status;

    private String paymentTerms;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;
    private BigDecimal minimumOrderAmount;
    private Integer leadTimeDays;

    private Integer totalOrders;
    private Integer successfulOrders;
    private Integer cancelledOrders;
    private BigDecimal totalPurchases;

    private String bankAccount;
    private String paymentMethod;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Qo‘shimcha statistik ma’lumotlar
    private Double successRate;
    private Double cancellationRate;

    // Note: Supplier entity is in a different microservice (user-service)
    // This method is kept for potential future use or REST communication
    // Uncomment and modify when Supplier entity is available
    /*
    public static SupplierResponseDto fromEntity(Supplier supplier) {
        if (supplier == null) return null;
        // Implementation removed - Supplier entity not in this service
        return null;
    }
    */
}
