package org.rakhmonov.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.paymentservice.service.ClickPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/click-webhook")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Click Webhook", description = "Click.uz webhook handling APIs")
public class ClickWebhookController {
    private final ClickPaymentService clickPaymentService;

    @PostMapping("/invoice")
    @Operation(summary = "Handle invoice webhook", description = "Handles Click.uz invoice webhook notifications")
    public ResponseEntity<Map<String, Object>> handleInvoiceWebhook(@RequestBody Map<String, Object> webhookData) {
        log.info("Received Click invoice webhook: {}", webhookData);
        
        try {
            String merchantTransId = (String) webhookData.get("merchant_trans_id");
            Integer errorCode = (Integer) webhookData.get("error_code");
            String errorNote = (String) webhookData.get("error_note");
            Long invoiceId = ((Number) webhookData.get("invoice_id")).longValue();
            
            if (errorCode != null && errorCode != 0) {
                // Handle error case
                log.error("Click invoice creation failed: {} - {}", errorCode, errorNote);
                return ResponseEntity.ok(Map.of("error_code", errorCode, "error_note", errorNote));
            }
            
            // Update payment with invoice ID
            clickPaymentService.updateInvoiceStatus(Long.valueOf(merchantTransId.split("-")[1]), 
                    invoiceId, null);
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "Invoice created successfully"));
            
        } catch (Exception e) {
            log.error("Error handling invoice webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/payment")
    @Operation(summary = "Handle payment webhook", description = "Handles Click.uz payment webhook notifications")
    public ResponseEntity<Map<String, Object>> handlePaymentWebhook(@RequestBody Map<String, Object> webhookData) {
        log.info("Received Click payment webhook: {}", webhookData);
        
        try {
            String merchantTransId = (String) webhookData.get("merchant_trans_id");
            Integer errorCode = (Integer) webhookData.get("error_code");
            String errorNote = (String) webhookData.get("error_note");
            Integer paymentStatus = (Integer) webhookData.get("payment_status");
            
            if (errorCode != null && errorCode != 0) {
                // Handle error case
                log.error("Click payment failed: {} - {}", errorCode, errorNote);
                return ResponseEntity.ok(Map.of("error_code", errorCode, "error_note", errorNote));
            }
            
            // Update payment status
            clickPaymentService.updatePaymentStatus(Long.valueOf(merchantTransId.split("-")[1]), paymentStatus);
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "Payment status updated successfully"));
            
        } catch (Exception e) {
            log.error("Error handling payment webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Webhook status", description = "Returns webhook service status")
    public ResponseEntity<Map<String, Object>> getWebhookStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "active",
            "service", "Click.uz Webhook Handler",
            "timestamp", System.currentTimeMillis()
        ));
    }
}

