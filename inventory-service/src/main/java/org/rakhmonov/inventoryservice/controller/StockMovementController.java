package org.rakhmonov.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.inventoryservice.dto.request.StockMovementRequest;
import org.rakhmonov.inventoryservice.dto.response.StockMovementResponse;
import org.rakhmonov.inventoryservice.entity.StockMovement;
import org.rakhmonov.inventoryservice.service.StockMovementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
@Slf4j
public class StockMovementController {
    private final StockMovementService stockMovementService;

    @PostMapping
    public ResponseEntity<StockMovementResponse> createStockMovement(@Valid @RequestBody StockMovementRequest request) {
        StockMovementResponse response = stockMovementService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovementResponse> getStockMovement(@PathVariable Long id) {
        StockMovementResponse response = stockMovementService.getStockMovement(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<StockMovementResponse>> getAllStockMovements() {
        List<StockMovementResponse> responses = stockMovementService.getAllStockMovements();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementResponse>> getStockMovementsByProduct(@PathVariable Long productId) {
        List<StockMovementResponse> responses = stockMovementService.getStockMovementsByProduct(productId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockMovementResponse>> getStockMovementsByWarehouse(@PathVariable Long warehouseId) {
        List<StockMovementResponse> responses = stockMovementService.getStockMovementsByWarehouse(warehouseId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{movementType}")
    public ResponseEntity<List<StockMovementResponse>> getStockMovementsByType(@PathVariable StockMovement.MovementType movementType) {
        List<StockMovementResponse> responses = stockMovementService.getStockMovementsByType(movementType);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<StockMovementResponse>> getStockMovementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<StockMovementResponse> responses = stockMovementService.getStockMovementsByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<List<StockMovementResponse>> getStockMovementsByReference(@PathVariable String referenceNumber) {
        List<StockMovementResponse> responses = stockMovementService.getStockMovementsByReference(referenceNumber);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reference-type/{referenceType}")
    public ResponseEntity<List<StockMovementResponse>> getStockMovementsByReferenceType(@PathVariable String referenceType) {
        List<StockMovementResponse> responses = stockMovementService.getStockMovementsByReferenceType(referenceType);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/created-by/{createdBy}")
    public ResponseEntity<List<StockMovementResponse>> getStockMovementsByCreatedBy(@PathVariable String createdBy) {
        List<StockMovementResponse> responses = stockMovementService.getStockMovementsByCreatedBy(createdBy);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {
        stockMovementService.deleteStockMovement(id);
        return ResponseEntity.noContent().build();
    }

    // Special endpoints for common operations
    @PostMapping("/purchase")
    public ResponseEntity<StockMovementResponse> recordPurchase(@Valid @RequestBody StockMovementRequest request) {
        request.setMovementType(StockMovement.MovementType.IN);
        request.setReferenceType("PURCHASE_ORDER");
        StockMovementResponse response = stockMovementService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sale")
    public ResponseEntity<StockMovementResponse> recordSale(@Valid @RequestBody StockMovementRequest request) {
        request.setMovementType(StockMovement.MovementType.OUT);
        request.setReferenceType("SALE");
        StockMovementResponse response = stockMovementService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/adjustment")
    public ResponseEntity<StockMovementResponse> recordAdjustment(@Valid @RequestBody StockMovementRequest request) {
        request.setMovementType(StockMovement.MovementType.ADJUSTMENT);
        request.setReferenceType("MANUAL_ADJUSTMENT");
        StockMovementResponse response = stockMovementService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<StockMovementResponse> recordTransfer(@Valid @RequestBody StockMovementRequest request) {
        request.setMovementType(StockMovement.MovementType.TRANSFER_OUT);
        request.setReferenceType("TRANSFER");
        StockMovementResponse response = stockMovementService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
