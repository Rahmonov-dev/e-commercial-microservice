package org.rakhmonov.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.userservice.dto.request.SupplierRequest;
import org.rakhmonov.userservice.dto.response.SupplierResponse;
import org.rakhmonov.userservice.service.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Supplier management endpoints (Admin only)")
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create supplier", description = "Create a new supplier (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin privileges required")
    })
    public ResponseEntity<SupplierResponse> createSupplier(
            @Parameter(description = "Supplier details", required = true)
            @Valid @RequestBody SupplierRequest request
    ) {
        SupplierResponse response = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get supplier by ID", description = "Get supplier details by ID (Admin only)")
    public ResponseEntity<SupplierResponse> getSupplierById(
            @Parameter(description = "Supplier ID", required = true)
            @PathVariable Long id
    ) {
        SupplierResponse response = supplierService.getSupplierById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get all suppliers", description = "Get list of all suppliers (Admin only)")
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update supplier", description = "Update supplier information (Admin only)")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @Parameter(description = "Supplier ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated supplier information", required = true)
            @Valid @RequestBody SupplierRequest request
    ) {
        SupplierResponse response = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete supplier", description = "Delete supplier by ID (Admin only)")
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "Supplier ID", required = true)
            @PathVariable Long id
    ) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
