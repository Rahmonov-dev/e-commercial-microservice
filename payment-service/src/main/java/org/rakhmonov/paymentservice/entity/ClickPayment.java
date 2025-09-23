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

    @Column(name = "click_trans_id", unique = true, nullable = false)
    private String clickTransId;

    @Column(name = "merchant_trans_id", unique = true, nullable = false)
    private String merchantTransId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "UZS";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "click_paydoc_id")
    private String clickPaydocId;

    @Column(name = "sign_string")
    private String signString;

    @Column(name = "sign_time")
    private String signTime;

    @Column(name = "error", length = 4)
    private String error;

    @Column(name = "error_note", columnDefinition = "TEXT")
    private String errorNote;

    @Column(name = "prepare_id")
    private String prepareId;

    @Column(name = "action", length = 10)
    private String action;

    @Column(name = "merchant_prepare_id")
    private String merchantPrepareId;

    @Column(name = "merchant_confirm_id")
    private String merchantConfirmId;

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
        PENDING, PREPARED, CONFIRMED, CANCELLED, REJECTED, ERROR
    }
}

