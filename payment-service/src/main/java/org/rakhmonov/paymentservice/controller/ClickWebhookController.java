package org.rakhmonov.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.paymentservice.service.ClickApiService;
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
    private final ClickApiService clickApiService;
    private final ClickPaymentService clickPaymentService;

    @PostMapping("/prepare")
    @Operation(summary = "Handle prepare webhook", description = "Handles Click.uz prepare webhook notifications")
    public ResponseEntity<Map<String, Object>> handlePrepareWebhook(@RequestBody Map<String, String> webhookData) {
        log.info("Received Click prepare webhook: {}", webhookData);
        
        try {
            // Verify webhook signature
            if (!clickApiService.verifyWebhookSignature(webhookData)) {
                log.error("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid signature"));
            }
            
            String clickTransId = webhookData.get("click_trans_id");
            String merchantTransId = webhookData.get("merchant_trans_id");
            String prepareId = webhookData.get("prepare_id");
            String error = webhookData.get("error");
            String errorNote = webhookData.get("error_note");
            
            if (error != null && !error.equals("0")) {
                // Handle error case
                log.error("Click payment preparation failed: {} - {}", error, errorNote);
                return ResponseEntity.ok(Map.of("error", error, "error_note", errorNote));
            }
            
            // Update payment status to prepared
            clickPaymentService.preparePayment(Long.valueOf(merchantTransId.split("-")[1]), prepareId);
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "Payment prepared successfully"));
            
        } catch (Exception e) {
            log.error("Error handling prepare webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/complete")
    @Operation(summary = "Handle complete webhook", description = "Handles Click.uz complete webhook notifications")
    public ResponseEntity<Map<String, Object>> handleCompleteWebhook(@RequestBody Map<String, String> webhookData) {
        log.info("Received Click complete webhook: {}", webhookData);
        
        try {
            // Verify webhook signature
            if (!clickApiService.verifyWebhookSignature(webhookData)) {
                log.error("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid signature"));
            }
            
            String clickTransId = webhookData.get("click_trans_id");
            String merchantTransId = webhookData.get("merchant_trans_id");
            String clickPaydocId = webhookData.get("click_paydoc_id");
            String error = webhookData.get("error");
            String errorNote = webhookData.get("error_note");
            
            if (error != null && !error.equals("0")) {
                // Handle error case
                log.error("Click payment completion failed: {} - {}", error, errorNote);
                return ResponseEntity.ok(Map.of("error", error, "error_note", errorNote));
            }
            
            // Update payment status to confirmed
            clickPaymentService.confirmPayment(Long.valueOf(merchantTransId.split("-")[1]), clickPaydocId);
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "Payment completed successfully"));
            
        } catch (Exception e) {
            log.error("Error handling complete webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/cancel")
    @Operation(summary = "Handle cancel webhook", description = "Handles Click.uz cancel webhook notifications")
    public ResponseEntity<Map<String, Object>> handleCancelWebhook(@RequestBody Map<String, String> webhookData) {
        log.info("Received Click cancel webhook: {}", webhookData);
        
        try {
            // Verify webhook signature
            if (!clickApiService.verifyWebhookSignature(webhookData)) {
                log.error("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid signature"));
            }
            
            String clickTransId = webhookData.get("click_trans_id");
            String merchantTransId = webhookData.get("merchant_trans_id");
            String error = webhookData.get("error");
            String errorNote = webhookData.get("error_note");
            
            // Update payment status to cancelled
            clickPaymentService.cancelPayment(Long.valueOf(merchantTransId.split("-")[1]), error, errorNote);
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "Payment cancelled successfully"));
            
        } catch (Exception e) {
            log.error("Error handling cancel webhook", e);
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

