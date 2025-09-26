package org.rakhmonov.paymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.paymentservice.entity.ClickPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickPaymentResponse {
    private Long id;
    private Long invoiceId;
    private Long paymentId;
    private String merchantTransId;
    private Long orderId;
    private Long userId;
    private String phoneNumber;
    private BigDecimal amount;
    private String currency;
    private Integer errorCode;
    private String errorNote;
    private Integer invoiceStatus;
    private String invoiceStatusNote;
    private Integer paymentStatus;
    private String cardToken;
    private String cardNumber;
    private Boolean temporary;
    private LocalDateTime processedAt;
    private String description;
    private Map<String, String> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClickPaymentResponse fromEntity(ClickPayment payment) {
        return ClickPaymentResponse.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoiceId())
                .paymentId(payment.getPaymentId())
                .merchantTransId(payment.getMerchantTransId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .phoneNumber(payment.getPhoneNumber())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .errorCode(payment.getErrorCode())
                .errorNote(payment.getErrorNote())
                .invoiceStatus(payment.getInvoiceStatus())
                .invoiceStatusNote(payment.getInvoiceStatusNote())
                .paymentStatus(payment.getPaymentStatus())
                .cardToken(payment.getCardToken())
                .cardNumber(payment.getCardNumber())
                .temporary(payment.getTemporary())
                .processedAt(payment.getProcessedAt())
                .description(payment.getDescription())
                .metadata(payment.getMetadata())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

