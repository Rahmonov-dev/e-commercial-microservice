package org.rakhmonov.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.InventoryRequest;
import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/inventory")
public class InventroyController {
    private final InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryRequest inventoryRequest) {
        return ResponseEntity.ok(InventoryResponse.toResponse(inventoryService.createInventory(inventoryRequest)));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long id,
            @RequestBody InventoryRequest inventoryRequest) {
        return ResponseEntity.ok(InventoryResponse.toResponse(inventoryService.updateInventory(id, inventoryRequest)));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(InventoryResponse.toResponse(inventoryService.getInventory(id)));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }
    
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<List<InventoryResponse>> filterInventory(
            @RequestParam String status,
            @RequestParam Long warehouseId,
            @RequestParam Long productId) {
        return ResponseEntity.ok(inventoryService.filterInventory(status, warehouseId, productId));
    }
}
