package org.rakhmonov.warehouseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.warehouseservice.dto.request.WarehouseRequest;
import org.rakhmonov.warehouseservice.dto.response.WarehouseResponse;
import org.rakhmonov.warehouseservice.entity.Warehouse;
import org.rakhmonov.warehouseservice.service.WarehouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouse", description = "Warehouse Management APIs")
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create new warehouse", description = "Create a new warehouse (Admin/Super Admin only)")
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse warehouse = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouse);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update warehouse", description = "Update an existing warehouse (Admin only)")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse warehouse = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(warehouse);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID", description = "Retrieve a warehouse by ID")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        WarehouseResponse warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(warehouse);
    }
    
    @GetMapping
    @Operation(summary = "Get all warehouses", description = "Retrieve all warehouses with pagination")
    public ResponseEntity<Page<WarehouseResponse>> getAllWarehouses(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<WarehouseResponse> warehouses = warehouseService.getAllWarehouses(pageable);
        return ResponseEntity.ok(warehouses);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get warehouses by status", description = "Retrieve warehouses by status")
    public ResponseEntity<Page<WarehouseResponse>> getWarehousesByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Warehouse.WarehouseStatus warehouseStatus = Warehouse.WarehouseStatus.valueOf(status.toUpperCase());
        Page<WarehouseResponse> warehouses = warehouseService.getWarehousesByStatus(warehouseStatus, pageable);
        return ResponseEntity.ok(warehouses);
    }
    
    @GetMapping("/city/{city}")
    @Operation(summary = "Get warehouses by city", description = "Retrieve warehouses by city")
    public ResponseEntity<Page<WarehouseResponse>> getWarehousesByCity(
            @PathVariable String city,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<WarehouseResponse> warehouses = warehouseService.getWarehousesByCity(city, pageable);
        return ResponseEntity.ok(warehouses);
    }
    
    @GetMapping("/country/{country}")
    @Operation(summary = "Get warehouses by country", description = "Retrieve warehouses by country")
    public ResponseEntity<Page<WarehouseResponse>> getWarehousesByCountry(
            @PathVariable String country,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<WarehouseResponse> warehouses = warehouseService.getWarehousesByCountry(country, pageable);
        return ResponseEntity.ok(warehouses);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search warehouses", description = "Search warehouses by name")
    public ResponseEntity<Page<WarehouseResponse>> searchWarehouses(
            @RequestParam String name,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<WarehouseResponse> warehouses = warehouseService.searchWarehousesByName(name, pageable);
        return ResponseEntity.ok(warehouses);
    }
    
    @GetMapping("/available-capacity")
    @Operation(summary = "Get warehouses with available capacity", description = "Find warehouses with minimum available capacity")
    public ResponseEntity<Page<WarehouseResponse>> getWarehousesWithAvailableCapacity(
            @RequestParam Integer minAvailable,
            @PageableDefault(size = 20, sort = "availableCapacity") Pageable pageable) {
        Page<WarehouseResponse> warehouses = warehouseService.getWarehousesWithAvailableCapacity(minAvailable, pageable);
        return ResponseEntity.ok(warehouses);
    }
    
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate warehouse", description = "Activate a warehouse (Admin only)")
    public ResponseEntity<WarehouseResponse> activateWarehouse(@PathVariable Long id) {
        WarehouseResponse warehouse = warehouseService.activateWarehouse(id);
        return ResponseEntity.ok(warehouse);
    }
    
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate warehouse", description = "Deactivate a warehouse (Admin only)")
    public ResponseEntity<WarehouseResponse> deactivateWarehouse(@PathVariable Long id) {
        WarehouseResponse warehouse = warehouseService.deactivateWarehouse(id);
        return ResponseEntity.ok(warehouse);
    }
    
    @PostMapping("/{id}/maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Set warehouse to maintenance", description = "Set warehouse to maintenance mode (Admin only)")
    public ResponseEntity<WarehouseResponse> setMaintenanceMode(@PathVariable Long id) {
        WarehouseResponse warehouse = warehouseService.setMaintenanceMode(id);
        return ResponseEntity.ok(warehouse);
    }
    
    @PutMapping("/{id}/occupancy")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update warehouse occupancy", description = "Update current occupancy of a warehouse (Admin only)")
    public ResponseEntity<WarehouseResponse> updateOccupancy(
            @PathVariable Long id,
            @RequestParam Integer occupancy) {
        WarehouseResponse warehouse = warehouseService.updateOccupancy(id, occupancy);
        return ResponseEntity.ok(warehouse);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete warehouse", description = "Soft delete a warehouse (Admin only)")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count warehouses by status", description = "Count warehouses by status (Admin only)")
    public ResponseEntity<Long> countWarehousesByStatus(@PathVariable String status) {
        Warehouse.WarehouseStatus warehouseStatus = Warehouse.WarehouseStatus.valueOf(status.toUpperCase());
        long count = warehouseService.countWarehousesByStatus(warehouseStatus);
        return ResponseEntity.ok(count);
    }
}


