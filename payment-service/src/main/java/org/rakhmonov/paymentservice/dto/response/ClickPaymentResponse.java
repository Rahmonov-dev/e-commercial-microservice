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
    private String clickTransId;
    private String merchantTransId;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private ClickPayment.PaymentStatus status;
    private String clickPaydocId;
    private String error;
    private String errorNote;
    private String prepareId;
    private String action;
    private String merchantPrepareId;
    private String merchantConfirmId;
    private LocalDateTime processedAt;
    private String description;
    private Map<String, String> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClickPaymentResponse fromEntity(ClickPayment payment) {
        return ClickPaymentResponse.builder()
                .id(payment.getId())
                .clickTransId(payment.getClickTransId())
                .merchantTransId(payment.getMerchantTransId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .clickPaydocId(payment.getClickPaydocId())
                .error(payment.getError())
                .errorNote(payment.getErrorNote())
                .prepareId(payment.getPrepareId())
                .action(payment.getAction())
                .merchantPrepareId(payment.getMerchantPrepareId())
                .merchantConfirmId(payment.getMerchantConfirmId())
                .processedAt(payment.getProcessedAt())
                .description(payment.getDescription())
                .metadata(payment.getMetadata())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

