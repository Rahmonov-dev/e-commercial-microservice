package org.rakhmonov.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.paymentservice.entity.Refund;
import org.rakhmonov.paymentservice.service.RefundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    // TODO: Implement refund endpoints
    @PostMapping
    public ResponseEntity<Refund> createRefund(@RequestBody Refund refund) {
        // TODO: Implement create refund endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Refund> getRefundById(@PathVariable Long id) {
        // TODO: Implement get refund by ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/number/{refundNumber}")
    public ResponseEntity<Refund> getRefundByRefundNumber(@PathVariable String refundNumber) {
        // TODO: Implement get refund by refund number endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Refund>> getAllRefunds() {
        // TODO: Implement get all refunds endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<Refund>> getRefundsByPaymentId(@PathVariable Long paymentId) {
        // TODO: Implement get refunds by payment ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Refund>> getRefundsByStatus(@PathVariable Refund.RefundStatus status) {
        // TODO: Implement get refunds by status endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requested-by/{requestedBy}")
    public ResponseEntity<List<Refund>> getRefundsByRequestedBy(@PathVariable Long requestedBy) {
        // TODO: Implement get refunds by requested by endpoint
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Refund> updateRefundStatus(@PathVariable Long id, @RequestParam Refund.RefundStatus status) {
        // TODO: Implement update refund status endpoint
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<Refund> processRefund(@PathVariable Long id) {
        // TODO: Implement process refund endpoint
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Refund> approveRefund(@PathVariable Long id, @RequestParam Long approvedBy) {
        // TODO: Implement approve refund endpoint
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Refund> rejectRefund(@PathVariable Long id, @RequestParam String reason) {
        // TODO: Implement reject refund endpoint
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRefund(@PathVariable Long id) {
        // TODO: Implement delete refund endpoint
        return ResponseEntity.ok().build();
    }
}


