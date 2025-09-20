package org.rakhmonov.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.SupplierRequestDto;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.dto.response.SupplierPaymentResponse;
import org.rakhmonov.inventoryservice.dto.response.SupplierResponseDto;
import org.rakhmonov.inventoryservice.dto.response.SupplierStatusResponse;
import org.rakhmonov.inventoryservice.entity.Supplier;
import org.rakhmonov.inventoryservice.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponseDto> createSupplier(@RequestBody SupplierRequestDto supplierRequestDto) {
        Supplier supplier = supplierService.createSupplier(SupplierRequestDto.toEntity(supplierRequestDto));
        return ResponseEntity.ok(SupplierResponseDto.fromEntity(supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> updateSupplier(@PathVariable Long id, @RequestBody SupplierRequestDto supplierRequestDto) {
        Supplier supplier = supplierService.updateSupplier(id, SupplierRequestDto.toEntity(supplierRequestDto));
        return ResponseEntity.ok(SupplierResponseDto.fromEntity(supplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> getSupplier(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplier(id);
        return ResponseEntity.ok(SupplierResponseDto.fromEntity(supplier));
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers.stream()
                .map(SupplierResponseDto::fromEntity)
                .collect(Collectors.toList()));
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<SupplierResponseDto> updateStatus(@PathVariable Long id, @PathVariable String status){
        return ResponseEntity.ok(supplierService.updateStatus(id, status));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<SupplierStatusResponse> getSupplierStatus(@PathVariable Long id){
        return ResponseEntity.ok(supplierService.getSupplierStatus(id));
    }
    @GetMapping("/{id}/payment")
    public ResponseEntity<SupplierPaymentResponse> getSupplierPayment(@PathVariable Long id){
        return ResponseEntity.ok(supplierService.getSupplierPayment(id));
    }
    @GetMapping("/search")
    public ResponseEntity<List<SupplierResponseDto>> searchSuppliers(@RequestParam String name){
        return ResponseEntity.ok(supplierService.searchSuppliers(name));
    }
    @GetMapping("/filter")
    public ResponseEntity<List<SupplierResponseDto>> filterSuppliers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) Supplier.SupplierStatus status,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) BigDecimal minCredit
    ) {
        List<SupplierResponseDto> suppliers = supplierService.filterSuppliers(
                city, paymentMethod, status, postalCode, country, state, minCredit
        );
        return ResponseEntity.ok(suppliers);
    }
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductResponse>> getSupplierProducts(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierProducts(id));
    }

}
