package org.rakhmonov.inventoryservice.controller;

import lombok.RequiredArgsConstructor;

import org.rakhmonov.inventoryservice.dto.request.WarehouseRequest;
import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.dto.response.WarehouseResponse;
import org.rakhmonov.inventoryservice.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    // Create
    @PostMapping
    public ResponseEntity<WarehouseResponse> create(@RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.create(request));
    }

    // Get by id
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getById(id));
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAll() {
        return ResponseEntity.ok(warehouseService.getAll());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> update(@PathVariable Long id,
                                                    @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.update(id, request));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/status/{status}")
    private ResponseEntity<List<WarehouseResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(warehouseService.getByStatus(status));
    }
    @GetMapping("/type/{type}")
    private ResponseEntity<List<WarehouseResponse>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(warehouseService.getByType(type));
    }

    @PatchMapping("/{id}/status/{status}")
    private ResponseEntity<WarehouseResponse> updateStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok(warehouseService.updateStatus(id, status));
    }
    @GetMapping("/search")
    private ResponseEntity<List<WarehouseResponse>> search(@RequestParam String name,
                                                           @RequestParam String city) {
        return ResponseEntity.ok(warehouseService.search(name,city));
    }
    @GetMapping("/{id}/inventory")
    public ResponseEntity<List<InventoryResponse>> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getInventory(id));
    }

}
