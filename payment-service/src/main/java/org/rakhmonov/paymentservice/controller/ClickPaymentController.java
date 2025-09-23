package org.rakhmonov.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.paymentservice.dto.request.ClickPaymentRequest;
import org.rakhmonov.paymentservice.dto.response.ClickPaymentResponse;
import org.rakhmonov.paymentservice.entity.ClickPayment;
import org.rakhmonov.paymentservice.service.ClickPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/click-payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Click Payment", description = "Click.uz payment management APIs")
public class ClickPaymentController {
    private final ClickPaymentService clickPaymentService;

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

    @GetMapping("/click-trans/{clickTransId}")
    @Operation(summary = "Get payment by Click transaction ID", description = "Retrieves a Click payment by Click transaction ID")
    public ResponseEntity<ClickPaymentResponse> getPaymentByClickTransId(
            @Parameter(description = "Click transaction ID") @PathVariable String clickTransId) {
        return clickPaymentService.getPaymentByClickTransId(clickTransId)
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

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Retrieves all payments with a specific status")
    public ResponseEntity<List<ClickPaymentResponse>> getPaymentsByStatus(
            @Parameter(description = "Payment status") @PathVariable ClickPayment.PaymentStatus status) {
        List<ClickPaymentResponse> payments = clickPaymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update payment status", description = "Updates the status of a Click payment")
    public ResponseEntity<ClickPaymentResponse> updatePaymentStatus(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam ClickPayment.PaymentStatus status) {
        ClickPaymentResponse response = clickPaymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/prepare")
    @Operation(summary = "Prepare payment", description = "Prepares a Click payment for processing")
    public ResponseEntity<ClickPaymentResponse> preparePayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "Prepare ID") @RequestParam String prepareId) {
        ClickPaymentResponse response = clickPaymentService.preparePayment(id, prepareId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm payment", description = "Confirms a Click payment")
    public ResponseEntity<ClickPaymentResponse> confirmPayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "Merchant confirm ID") @RequestParam String merchantConfirmId) {
        ClickPaymentResponse response = clickPaymentService.confirmPayment(id, merchantConfirmId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel payment", description = "Cancels a Click payment")
    public ResponseEntity<ClickPaymentResponse> cancelPayment(
            @Parameter(description = "Payment ID") @PathVariable Long id,
            @Parameter(description = "Error code") @RequestParam String error,
            @Parameter(description = "Error note") @RequestParam String errorNote) {
        ClickPaymentResponse response = clickPaymentService.cancelPayment(id, error, errorNote);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Deletes a Click payment")
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        clickPaymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}

