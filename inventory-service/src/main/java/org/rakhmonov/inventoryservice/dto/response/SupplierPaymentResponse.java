package org.rakhmonov.inventoryservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SupplierPaymentResponse {
    private Long supplierId;          // Supplier kimligi
    private BigDecimal creditLimit;   // Kredit limiti
    private BigDecimal currentBalance;// Qarzdorlik yoki hozirgi balans
    private BigDecimal availableCredit; // Qolgan kredit (creditLimit - currentBalance)
    private String paymentTerms;      // Masalan: "Net 30"
    private String paymentMethod;     // Masalan: "Bank Transfer"
}
