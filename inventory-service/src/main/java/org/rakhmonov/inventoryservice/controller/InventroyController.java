package org.rakhmonov.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.InventoryRequest;
import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/inventory")
public class InventroyController {
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryRequest inventoryRequest) {
        return ResponseEntity.ok(InventoryResponse.toResponse(inventoryService.createInventory(inventoryRequest)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Long id, @RequestBody InventoryRequest inventoryRequest) {
        return ResponseEntity.ok(InventoryResponse.toResponse(inventoryService.updateInventory(id, inventoryRequest)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(InventoryResponse.toResponse(inventoryService.getInventory(id)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }
    @GetMapping("/filter")
    public ResponseEntity<List<InventoryResponse>> filterInventory(@RequestParam String status,
                                                                   @RequestParam  Long warehouseId,
                                                                   @RequestParam  Long productId) {
        return ResponseEntity.ok(inventoryService.filterInventory(status, warehouseId, productId));
    }
}
