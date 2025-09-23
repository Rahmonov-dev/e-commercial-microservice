package org.rakhmonov.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.paymentservice.entity.PaymentMethod;
import org.rakhmonov.paymentservice.service.PaymentMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    // TODO: Implement payment method endpoints
    @PostMapping
    public ResponseEntity<PaymentMethod> createPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        // TODO: Implement create payment method endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethodById(@PathVariable Long id) {
        // TODO: Implement get payment method by ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethod>> getAllPaymentMethods() {
        // TODO: Implement get all payment methods endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethodsByUserId(@PathVariable Long userId) {
        // TODO: Implement get payment methods by user ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<PaymentMethod>> getActivePaymentMethodsByUserId(@PathVariable Long userId) {
        // TODO: Implement get active payment methods by user ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/default")
    public ResponseEntity<PaymentMethod> getDefaultPaymentMethodByUserId(@PathVariable Long userId) {
        // TODO: Implement get default payment method by user ID endpoint
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@PathVariable Long id, @RequestBody PaymentMethod paymentMethod) {
        // TODO: Implement update payment method endpoint
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<PaymentMethod> setDefaultPaymentMethod(@PathVariable Long id) {
        // TODO: Implement set default payment method endpoint
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<PaymentMethod> activatePaymentMethod(@PathVariable Long id) {
        // TODO: Implement activate payment method endpoint
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<PaymentMethod> deactivatePaymentMethod(@PathVariable Long id) {
        // TODO: Implement deactivate payment method endpoint
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        // TODO: Implement delete payment method endpoint
        return ResponseEntity.ok().build();
    }
}


