package org.rakhmonov.inventoryservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Data;
import org.rakhmonov.inventoryservice.entity.Supplier;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequestDto {

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

    private String supplierType;   // MANUFACTURER, WHOLESALER...
    private String status;         // ACTIVE, INACTIVE...

    private String paymentTerms;   // "Net 30", "COD", etc.
    private BigDecimal creditLimit;
    private BigDecimal minimumOrderAmount;
    private Integer leadTimeDays;

    private String bankAccount;
    private String paymentMethod;
    private String notes;
    public static Supplier toEntity(SupplierRequestDto dto) {
        if (dto == null) return null;

        return Supplier.builder()
                .companyName(dto.getCompanyName())
                .contactPerson(dto.getContactPerson())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .postalCode(dto.getPostalCode())
                .website(dto.getWebsite())
                .supplierType(dto.getSupplierType() != null
                        ? Supplier.SupplierType.valueOf(dto.getSupplierType())
                        : Supplier.SupplierType.MANUFACTURER)
                .status(dto.getStatus() != null
                        ? Supplier.SupplierStatus.valueOf(dto.getStatus())
                        : Supplier.SupplierStatus.ACTIVE)
                .paymentTerms(dto.getPaymentTerms())
                .creditLimit(dto.getCreditLimit())
                .minimumOrderAmount(dto.getMinimumOrderAmount())
                .leadTimeDays(dto.getLeadTimeDays() != null ? dto.getLeadTimeDays() : 7)
                .bankAccount(dto.getBankAccount())
                .paymentMethod(dto.getPaymentMethod())
                .notes(dto.getNotes())
                .build();
    }
}
