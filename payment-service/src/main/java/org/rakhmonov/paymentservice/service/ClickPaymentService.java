package org.rakhmonov.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.paymentservice.dto.request.ClickPaymentRequest;
import org.rakhmonov.paymentservice.dto.response.ClickPaymentResponse;
import org.rakhmonov.paymentservice.entity.ClickPayment;
import org.rakhmonov.paymentservice.repo.ClickPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickPaymentService {
    private final ClickPaymentRepository clickPaymentRepository;

    @Transactional
    public ClickPaymentResponse createPayment(ClickPaymentRequest request) {
        log.info("Creating Click payment for order: {}", request.getOrderId());
        
        ClickPayment payment = ClickPayment.builder()
                .clickTransId("CLICK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .merchantTransId("MERCHANT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(ClickPayment.PaymentStatus.PENDING)
                .description(request.getDescription())
                .metadata(request.getMetadata())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ClickPayment savedPayment = clickPaymentRepository.save(payment);
        log.info("Click payment created with ID: {}", savedPayment.getId());
        
        return ClickPaymentResponse.fromEntity(savedPayment);
    }

    public Optional<ClickPaymentResponse> getPaymentById(Long id) {
        return clickPaymentRepository.findById(id)
                .map(ClickPaymentResponse::fromEntity);
    }

    public Optional<ClickPaymentResponse> getPaymentByClickTransId(String clickTransId) {
        return clickPaymentRepository.findByClickTransId(clickTransId)
                .map(ClickPaymentResponse::fromEntity);
    }

    public Optional<ClickPaymentResponse> getPaymentByMerchantTransId(String merchantTransId) {
        return clickPaymentRepository.findByMerchantTransId(merchantTransId)
                .map(ClickPaymentResponse::fromEntity);
    }

    public List<ClickPaymentResponse> getAllPayments() {
        return clickPaymentRepository.findAll().stream()
                .map(ClickPaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ClickPaymentResponse> getPaymentsByOrderId(Long orderId) {
        return clickPaymentRepository.findByOrderId(orderId).stream()
                .map(ClickPaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ClickPaymentResponse> getPaymentsByUserId(Long userId) {
        return clickPaymentRepository.findByUserId(userId).stream()
                .map(ClickPaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ClickPaymentResponse> getPaymentsByStatus(ClickPayment.PaymentStatus status) {
        return clickPaymentRepository.findByStatus(status).stream()
                .map(ClickPaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClickPaymentResponse updatePaymentStatus(Long id, ClickPayment.PaymentStatus newStatus) {
        ClickPayment payment = clickPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Click payment not found with id: " + id));
        
        payment.setStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());
        
        if (newStatus == ClickPayment.PaymentStatus.CONFIRMED) {
            payment.setProcessedAt(LocalDateTime.now());
        }
        
        ClickPayment savedPayment = clickPaymentRepository.save(payment);
        log.info("Click payment status updated to: {} for payment ID: {}", newStatus, id);
        
        return ClickPaymentResponse.fromEntity(savedPayment);
    }

    @Transactional
    public ClickPaymentResponse preparePayment(Long id, String prepareId) {
        ClickPayment payment = clickPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Click payment not found with id: " + id));
        
        payment.setStatus(ClickPayment.PaymentStatus.PREPARED);
        payment.setPrepareId(prepareId);
        payment.setAction("prepare");
        payment.setUpdatedAt(LocalDateTime.now());
        
        ClickPayment savedPayment = clickPaymentRepository.save(payment);
        log.info("Click payment prepared with prepare ID: {}", prepareId);
        
        return ClickPaymentResponse.fromEntity(savedPayment);
    }

    @Transactional
    public ClickPaymentResponse confirmPayment(Long id, String merchantConfirmId) {
        ClickPayment payment = clickPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Click payment not found with id: " + id));
        
        payment.setStatus(ClickPayment.PaymentStatus.CONFIRMED);
        payment.setMerchantConfirmId(merchantConfirmId);
        payment.setAction("confirm");
        payment.setProcessedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        ClickPayment savedPayment = clickPaymentRepository.save(payment);
        log.info("Click payment confirmed with merchant confirm ID: {}", merchantConfirmId);
        
        return ClickPaymentResponse.fromEntity(savedPayment);
    }

    @Transactional
    public ClickPaymentResponse cancelPayment(Long id, String error, String errorNote) {
        ClickPayment payment = clickPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Click payment not found with id: " + id));
        
        payment.setStatus(ClickPayment.PaymentStatus.CANCELLED);
        payment.setError(error);
        payment.setErrorNote(errorNote);
        payment.setAction("cancel");
        payment.setUpdatedAt(LocalDateTime.now());
        
        ClickPayment savedPayment = clickPaymentRepository.save(payment);
        log.info("Click payment cancelled with error: {}", error);
        
        return ClickPaymentResponse.fromEntity(savedPayment);
    }

    @Transactional
    public void deletePayment(Long id) {
        clickPaymentRepository.deleteById(id);
        log.info("Click payment deleted with ID: {}", id);
    }
}

