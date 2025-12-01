package org.rakhmonov.userservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.userservice.dto.request.SupplierRequest;
import org.rakhmonov.userservice.dto.response.SupplierResponse;
import org.rakhmonov.userservice.entity.Supplier;
import org.rakhmonov.userservice.repo.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        // Check if email or phone already exists
        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Supplier with this email already exists");
        }
        if (supplierRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Supplier with this phone number already exists");
        }

        Supplier supplier = Supplier.builder()
                .companyName(request.getCompanyName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);
        return SupplierResponse.fromEntity(savedSupplier);
    }

    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return SupplierResponse.fromEntity(supplier);
    }

    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(SupplierResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        if (request.getCompanyName() != null) {
            supplier.setCompanyName(request.getCompanyName());
        }
        if (request.getEmail() != null) {
            // Check if email is already taken by another supplier
            supplierRepository.findByEmail(request.getEmail())
                    .ifPresent(existingSupplier -> {
                        if (!existingSupplier.getId().equals(id)) {
                            throw new RuntimeException("Email already taken by another supplier");
                        }
                    });
            supplier.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            // Check if phone is already taken by another supplier
            supplierRepository.findByPhoneNumber(request.getPhoneNumber())
                    .ifPresent(existingSupplier -> {
                        if (!existingSupplier.getId().equals(id)) {
                            throw new RuntimeException("Phone number already taken by another supplier");
                        }
                    });
            supplier.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            supplier.setAddress(request.getAddress());
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return SupplierResponse.fromEntity(updatedSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        supplierRepository.delete(supplier);
    }
}
