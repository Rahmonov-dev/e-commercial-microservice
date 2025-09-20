package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.ThirdPartySellerRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.dto.response.ThirdPartySellerResponse;
import org.rakhmonov.inventoryservice.entity.ThirdPartySeller;
import org.rakhmonov.inventoryservice.repo.ThirdPartySellerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThirdPartySellerService {
    private final ThirdPartySellerRepository thirdPartySellerRepository;

    public ThirdPartySellerResponse createThirdPartySeller(ThirdPartySellerRequest request) {
        ThirdPartySeller thirdPartySeller = ThirdPartySellerRequest.toEntity(request);
        thirdPartySeller = thirdPartySellerRepository.save(thirdPartySeller);
        return ThirdPartySellerResponse.toResponse(thirdPartySeller);
    }

    public ThirdPartySellerResponse updateThirdPartySeller(Long id, ThirdPartySellerRequest request) {
        ThirdPartySeller thirdPartySeller = thirdPartySellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ThirdPartySeller not found"));
        Optional.ofNullable(request.getCompanyName()).ifPresent(thirdPartySeller::setCompanyName);
        Optional.ofNullable(request.getCompanyName()).ifPresent(thirdPartySeller::setCompanyName);
        Optional.ofNullable(request.getEmail()).ifPresent(thirdPartySeller::setEmail);
        Optional.ofNullable(request.getPhone()).ifPresent(thirdPartySeller::setPhone);
        Optional.ofNullable(request.getAddress()).ifPresent(thirdPartySeller::setAddress);
        Optional.ofNullable(request.getDeliveryTimeDays()).ifPresent(thirdPartySeller::setDeliveryTimeDays);
        return ThirdPartySellerResponse.toResponse(thirdPartySeller);
    }

    public void deleteThirdPartySeller(Long id) {
        thirdPartySellerRepository.deleteById(id);
    }

    public ThirdPartySellerResponse getThirdPartySellerById(Long id) {
        ThirdPartySeller thirdPartySeller = thirdPartySellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ThirdPartySeller not found"));
        return ThirdPartySellerResponse.toResponse(thirdPartySeller);
    }
    public List<ThirdPartySellerResponse> getAllThirdPartySellers() {
        return thirdPartySellerRepository.findAll().stream()
                .filter(ThirdPartySeller::getIsActive)
                .map(ThirdPartySellerResponse::toResponse)
                .collect(Collectors.toList());
    }

    public ThirdPartySellerResponse activateThirdPartySeller(Long id) {
        ThirdPartySeller thirdPartySeller = thirdPartySellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ThirdPartySeller not found"));
        thirdPartySeller.setIsActive(true);
        return ThirdPartySellerResponse.toResponse(thirdPartySeller);
    }

    public ThirdPartySellerResponse deactivateThirdPartySeller(Long id) {
        ThirdPartySeller thirdPartySeller = thirdPartySellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ThirdPartySeller not found"));
        thirdPartySeller.setIsActive(false);
        return ThirdPartySellerResponse.toResponse(thirdPartySeller);
    }

    public List<ThirdPartySellerResponse> searchThirdPartySellers(String name) {
        return thirdPartySellerRepository.findByCompanyNameContaining(name);
    }

    public Page<ThirdPartySellerResponse> getThirdPartySellersByPage(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return thirdPartySellerRepository.findAll(pageable).map(ThirdPartySellerResponse::toResponse);
    }

    public List<ProductResponse> getProductsByThirdPartySellerId(Long id) {
        return thirdPartySellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ThirdPartySeller not found"))
                .getProducts()
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
