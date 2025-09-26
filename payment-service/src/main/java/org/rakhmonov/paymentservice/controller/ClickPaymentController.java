package org.rakhmonov.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.paymentservice.dto.request.ClickPaymentRequest;
import org.rakhmonov.paymentservice.dto.response.ClickPaymentResponse;
import org.rakhmonov.paymentservice.service.ClickPaymentService;
import org.rakhmonov.paymentservice.service.ClickApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/click-payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Click Payment", description = "Click.uz payment management APIs")
public class ClickPaymentController {
    private final ClickPaymentService clickPaymentService;
    private final ClickApiService clickApiService;

    @PostMapping
    @Operation(summary = "Create a new Click payment", description = "Creates a new payment request for Click.uz processing")
    public ResponseEntity<ClickPaymentResponse> createPayment(@RequestBody ClickPaymentRequest request) {
        log.info("Creating Click payment for order: {}", request.getOrderId());
        ClickPaymentResponse response = clickPaymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a Click payment by its ID")
    public ResponseEntity<ClickPaymentResponse> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        return clickPaymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "Get payment by invoice ID", description = "Retrieves a Click payment by invoice ID")
    public ResponseEntity<ClickPaymentResponse> getPaymentByInvoiceId(
            @Parameter(description = "Invoice ID") @PathVariable Long invoiceId) {
        return clickPaymentService.getPaymentByInvoiceId(invoiceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/merchant-trans/{merchantTransId}")
    @Operation(summary = "Get payment by merchant transaction ID", description = "Retrieves a Click payment by merchant transaction ID")
    public ResponseEntity<ClickPaymentResponse> getPaymentByMerchantTransId(
            @Parameter(description = "Merchant transaction ID") @PathVariable String merchantTransId) {
        return clickPaymentService.getPaymentByMerchantTransId(merchantTransId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves all Click payments")
    public ResponseEntity<List<ClickPaymentResponse>> getAllPayments() {
        List<ClickPaymentResponse> payments = clickPaymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payments by order ID", description = "Retrieves all payments for a specific order")
    public ResponseEntity<List<ClickPaymentResponse>> getPaymentsByOrderId(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        List<ClickPaymentResponse> payments = clickPaymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payments by user ID", description = "Retrieves all payments for a specific user")
    public ResponseEntity<List<ClickPaymentResponse>> getPaymentsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<ClickPaymentResponse> payments = clickPaymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{paymentStatus}")
    @Operation(summary = "Get payments by payment status", description = "Retrieves all payments with a specific payment status")
    public ResponseEntity<List<ClickPaymentResponse>> getPaymentsByPaymentStatus(
            @Parameter(description = "Payment status") @PathVariable Integer paymentStatus) {
        List<ClickPaymentResponse> payments = clickPaymentService.getPaymentsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}/payment-status")
    @Operation(summary = "Update payment status", description = "Updates the payment status of a Click payment")
    public ResponseEntity<ClickPaymentResponse> updatePaymentStatus(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "New payment status") @RequestParam Integer paymentStatus) {
        ClickPaymentResponse response = clickPaymentService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/invoice")
    @Operation(summary = "Update invoice", description = "Updates the invoice information of a Click payment")
    public ResponseEntity<ClickPaymentResponse> updateInvoice(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "Invoice ID") @RequestParam Long invoiceId,
            @Parameter(description = "Invoice status note") @RequestParam(required = false) String invoiceStatusNote) {
        ClickPaymentResponse response = clickPaymentService.updateInvoiceStatus(id, invoiceId, invoiceStatusNote);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/error")
    @Operation(summary = "Update error", description = "Updates the error information of a Click payment")
    public ResponseEntity<ClickPaymentResponse> updateError(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "Error code") @RequestParam Integer errorCode,
            @Parameter(description = "Error note") @RequestParam String errorNote) {
        ClickPaymentResponse response = clickPaymentService.updateError(id, errorCode, errorNote);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Deletes a Click payment")
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        clickPaymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invoice")
    @Operation(summary = "Create invoice", description = "Creates a Click.uz invoice for a payment")
    public ResponseEntity<Map<String, Object>> createInvoice(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "Phone number") @RequestParam String phoneNumber) {
        try {
            ClickPaymentResponse payment = clickPaymentService.getPaymentById(id)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            
            ClickPaymentRequest request = ClickPaymentRequest.builder()
                    .orderId(payment.getOrderId())
                    .userId(payment.getUserId())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .description(payment.getDescription())
                    .build();
            
            Map<String, Object> result = clickApiService.createInvoice(request, phoneNumber);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error creating invoice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create invoice: " + e.getMessage()));
        }
    }

    @PostMapping("/card-token")
    @Operation(summary = "Create card token", description = "Creates a Click.uz card token")
    public ResponseEntity<Map<String, Object>> createCardToken(
            @Parameter(description = "Card number") @RequestParam String cardNumber,
            @Parameter(description = "Expire date (MMYY)") @RequestParam String expireDate,
            @Parameter(description = "Temporary token") @RequestParam(defaultValue = "true") Boolean temporary) {
        try {
            Map<String, Object> result = clickApiService.createCardToken(cardNumber, expireDate, temporary);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error creating card token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create card token: " + e.getMessage()));
        }
    }

    @PostMapping("/card-token/verify")
    @Operation(summary = "Verify card token", description = "Verifies a Click.uz card token")
    public ResponseEntity<Map<String, Object>> verifyCardToken(
            @Parameter(description = "Card token") @RequestParam String cardToken,
            @Parameter(description = "SMS code") @RequestParam String smsCode) {
        try {
            Map<String, Object> result = clickApiService.verifyCardToken(cardToken, smsCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error verifying card token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to verify card token: " + e.getMessage()));
        }
    }

    @PostMapping("/card-token/payment")
    @Operation(summary = "Payment with card token", description = "Processes payment using card token")
    public ResponseEntity<Map<String, Object>> paymentWithToken(
            @Parameter(description = "Card token") @RequestParam String cardToken,
            @Parameter(description = "Amount") @RequestParam java.math.BigDecimal amount,
            @Parameter(description = "Merchant transaction ID") @RequestParam String merchantTransId) {
        try {
            Map<String, Object> result = clickApiService.paymentWithToken(cardToken, amount, merchantTransId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing payment with token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process payment: " + e.getMessage()));
        }
    }

    @DeleteMapping("/card-token/{cardToken}")
    @Operation(summary = "Delete card token", description = "Deletes a Click.uz card token")
    public ResponseEntity<Map<String, Object>> deleteCardToken(
            @Parameter(description = "Card token") @PathVariable String cardToken) {
        try {
            Map<String, Object> result = clickApiService.deleteCardToken(cardToken);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error deleting card token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete card token: " + e.getMessage()));
        }
    }
}

