package org.rakhmonov.inventoryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.inventoryservice.entity.Supplier;

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

    public static SupplierResponseDto fromEntity(Supplier supplier) {
        if (supplier == null) return null;

        return SupplierResponseDto.builder()
                .id(supplier.getId())
                .companyName(supplier.getCompanyName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .state(supplier.getState())
                .country(supplier.getCountry())
                .postalCode(supplier.getPostalCode())
                .website(supplier.getWebsite())
                .supplierType(supplier.getSupplierType().name())
                .status(supplier.getStatus().name())
                .paymentTerms(supplier.getPaymentTerms())
                .creditLimit(supplier.getCreditLimit())
                .currentBalance(supplier.getCurrentBalance())
                .minimumOrderAmount(supplier.getMinimumOrderAmount())
                .leadTimeDays(supplier.getLeadTimeDays())
                .totalOrders(supplier.getTotalOrders())
                .successfulOrders(supplier.getSuccessfulOrders())
                .cancelledOrders(supplier.getCancelledOrders())
                .totalPurchases(supplier.getTotalPurchases())
                .bankAccount(supplier.getBankAccount())
                .paymentMethod(supplier.getPaymentMethod())
                .notes(supplier.getNotes())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .successRate(supplier.getSuccessRate())
                .cancellationRate(supplier.getCancellationRate())
                .build();
    }
}
