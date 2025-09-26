package org.rakhmonov.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "click_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "merchant_trans_id", unique = true, nullable = false)
    private String merchantTransId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "UZS";

    @Column(name = "error_code")
    private Integer errorCode;

    @Column(name = "error_note", columnDefinition = "TEXT")
    private String errorNote;

    @Column(name = "invoice_status")
    private Integer invoiceStatus;

    @Column(name = "invoice_status_note")
    private String invoiceStatusNote;

    @Column(name = "payment_status")
    private Integer paymentStatus;

    @Column(name = "card_token")
    private String cardToken;

    @Column(name = "card_number", length = 50)
    private String cardNumber;

    @Column(name = "temporary")
    private Boolean temporary;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "click_payment_metadata", joinColumns = @JoinColumn(name = "payment_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PaymentStatus {
        PENDING(0),
        PROCESSING(1),
        SUCCESSFUL(2),
        CANCELLED(-1),
        REJECTED(-2),
        ERROR(-3);

        private final int code;

        PaymentStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static PaymentStatus fromCode(int code) {
            for (PaymentStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return ERROR;
        }
    }
}

