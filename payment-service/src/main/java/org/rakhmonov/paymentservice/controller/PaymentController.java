package org.rakhmonov.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.paymentservice.entity.Payment;
import org.rakhmonov.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // TODO: Implement payment endpoints
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        // TODO: Implement create payment endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        // TODO: Implement get payment by ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<Payment> getPaymentByPaymentNumber(@PathVariable String paymentNumber) {
        // TODO: Implement get payment by payment number endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        // TODO: Implement get all payments endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId) {
        // TODO: Implement get payments by order ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable Long userId) {
        // TODO: Implement get payments by user ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable Payment.PaymentStatus status) {
        // TODO: Implement get payments by status endpoint
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Long id, @RequestParam Payment.PaymentStatus status) {
        // TODO: Implement update payment status endpoint
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<Payment> processPayment(@PathVariable Long id) {
        // TODO: Implement process payment endpoint
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Payment> cancelPayment(@PathVariable Long id) {
        // TODO: Implement cancel payment endpoint
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        // TODO: Implement delete payment endpoint
        return ResponseEntity.ok().build();
    }
}


