package org.rakhmonov.inventoryservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    // Note: Supplier entity is in a different microservice (user-service)
    // This method is kept for potential future use or REST communication
    // Uncomment and modify when Supplier entity is available
    /*
    public static Supplier toEntity(SupplierRequestDto dto) {
        if (dto == null) return null;
        // Implementation removed - Supplier entity not in this service
        return null;
    }
    */
}
