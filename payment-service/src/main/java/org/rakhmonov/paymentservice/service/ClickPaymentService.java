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
                .merchantTransId("MERCHANT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
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

    public Optional<ClickPaymentResponse> getPaymentByInvoiceId(Long invoiceId) {
        return clickPaymentRepository.findByInvoiceId(invoiceId)
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

    public List<ClickPaymentResponse> getPaymentsByPaymentStatus(Integer paymentStatus) {
        return clickPaymentRepository.findByPaymentStatusAndDateRange(paymentStatus, 
                LocalDateTime.now().minusDays(30), LocalDateTime.now()).stream()
                .map(ClickPaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClickPaymentResponse updatePaymentStatus(Long id, Integer newPaymentStatus) {
        ClickPayment payment = clickPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Click payment not found with id: " + id));
        
        payment.setPaymentStatus(newPaymentStatus);
        payment.setUpdatedAt(LocalDateTime.now());
        
        if (newPaymentStatus == 2) { // SUCCESSFUL
            payment.setProcessedAt(LocalDateTime.now());
        }
        
        ClickPayment savedPayment = clickPaymentRepository.save(payment);
        log.info("Click payment status updated to: {} for payment ID: {}", newPaymentStatus, id);
        
        return ClickPaymentResponse.fromEntity(savedPayment);
    }

    @Transactional
    public ClickPaymentResponse updateInvoiceStatus(Long id, Long invoiceId, String invoiceStatusNote) {
        ClickPayment payment = clickPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Click payment not found with id: " + id));
        
        payment.setInvoiceId(invoiceId);
        payment.setInvoiceStatusNote(invoiceStatusNote);
        payment.setUpdatedAt(LocalDateTime.now());
        
        ClickPayment savedPayment = clickPaymentRepository.save(payment);
        log.info("Click payment invoice ID updated to: {} for payment ID: {}", invoiceId, id);
        
        return ClickPaymentResponse.fromEntity(savedPayment);
    }

    @Transactional
    public ClickPaymentResponse updateError(Long id, Integer errorCode, String errorNote) {
        ClickPayment payment = clickPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Click payment not found with id: " + id));
        
        payment.setErrorCode(errorCode);
        payment.setErrorNote(errorNote);
        payment.setUpdatedAt(LocalDateTime.now());
        
        ClickPayment savedPayment = clickPaymentRepository.save(payment);
        log.info("Click payment error updated: {} for payment ID: {}", errorCode, id);
        
        return ClickPaymentResponse.fromEntity(savedPayment);
    }

    @Transactional
    public void deletePayment(Long id) {
        clickPaymentRepository.deleteById(id);
        log.info("Click payment deleted with ID: {}", id);
    }
}

