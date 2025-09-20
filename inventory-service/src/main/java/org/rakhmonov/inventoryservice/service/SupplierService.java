package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.dto.response.SupplierPaymentResponse;
import org.rakhmonov.inventoryservice.dto.response.SupplierResponseDto;
import org.rakhmonov.inventoryservice.dto.response.SupplierStatusResponse;
import org.rakhmonov.inventoryservice.entity.Supplier;
import org.rakhmonov.inventoryservice.repo.ProductRepository;
import org.rakhmonov.inventoryservice.repo.SupplierRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public Supplier createSupplier(Supplier entity) {
        return supplierRepository.save(entity);
    }

    public Supplier updateSupplier(Long id, Supplier entity) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Optional.ofNullable(entity.getCompanyName()).ifPresent(supplier::setCompanyName);
        Optional.ofNullable(entity.getContactPerson()).ifPresent(supplier::setContactPerson);
        Optional.ofNullable(entity.getEmail()).ifPresent(supplier::setEmail);
        Optional.ofNullable(entity.getPhone()).ifPresent(supplier::setPhone);
        Optional.ofNullable(entity.getAddress()).ifPresent(supplier::setAddress);
        Optional.ofNullable(entity.getCity()).ifPresent(supplier::setCity);
        Optional.ofNullable(entity.getState()).ifPresent(supplier::setState);
        Optional.ofNullable(entity.getCountry()).ifPresent(supplier::setCountry);
        Optional.ofNullable(entity.getPostalCode()).ifPresent(supplier::setPostalCode);
        Optional.ofNullable(entity.getWebsite()).ifPresent(supplier::setWebsite);
        Optional.ofNullable(entity.getSupplierType()).ifPresent(supplier::setSupplierType);
        Optional.ofNullable(entity.getStatus()).ifPresent(supplier::setStatus);
        Optional.ofNullable(entity.getPaymentTerms()).ifPresent(supplier::setPaymentTerms);
        Optional.ofNullable(entity.getCreditLimit()).ifPresent(supplier::setCreditLimit);
        Optional.ofNullable(entity.getMinimumOrderAmount()).ifPresent(supplier::setMinimumOrderAmount);
        Optional.ofNullable(entity.getLeadTimeDays()).ifPresent(supplier::setLeadTimeDays);
        Optional.ofNullable(entity.getBankAccount()).ifPresent(supplier::setBankAccount);
        Optional.ofNullable(entity.getPaymentMethod()).ifPresent(supplier::setPaymentMethod);
        Optional.ofNullable(entity.getNotes()).ifPresent(supplier::setNotes);

        return supplierRepository.save(supplier);
    }


    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    public Supplier getSupplier(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .filter(active -> active.getStatus().equals("ACTIVE"))
                .collect(Collectors.toList());
    }

    public SupplierStatusResponse getSupplierStatus(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return SupplierStatusResponse.builder()
                .supplierId(supplier.getId())
                .totalOrders(supplier.getTotalOrders())
                .successfulOrders(supplier.getSuccessfulOrders())
                .cancelledOrders(supplier.getCancelledOrders())
                .successRate(supplier.getSuccessRate())
                .cancelRate(supplier.getCancellationRate())
                .totalPurchases(supplier.getTotalPurchases())
                .build();
    }

    public SupplierPaymentResponse getSupplierPayment(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return SupplierPaymentResponse.builder()
                .supplierId(supplier.getId())
                .creditLimit(supplier.getCreditLimit())
                .currentBalance(supplier.getCurrentBalance())
                .availableCredit(supplier.getCreditLimit().subtract(supplier.getCurrentBalance()))
                .paymentTerms(supplier.getPaymentTerms())
                .paymentMethod(supplier.getPaymentMethod())
                .build();
    }

    public List<SupplierResponseDto> searchSuppliers(String keyword) {
        return supplierRepository.searchSuppliers(keyword)
                .stream()
                .map(SupplierResponseDto::fromEntity)
                .toList();
    }


    public List<SupplierResponseDto> filterSuppliers(String city,
                                                     String paymentMethod,
                                                     Supplier.SupplierStatus status,
                                                     String postalCode,
                                                     String country,
                                                     String state,
                                                     BigDecimal minCredit) {

        return supplierRepository.filterSuppliers(city, paymentMethod, status, postalCode, country, state, minCredit)
                .stream()
                .map(SupplierResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


    public List<ProductResponse> getSupplierProducts(Long supplierId) {
        return productRepository.findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public SupplierResponseDto updateStatus(Long id, String status) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.setStatus(Supplier.SupplierStatus.valueOf(status));
        return SupplierResponseDto.fromEntity(supplierRepository.save(supplier));
    }
}
