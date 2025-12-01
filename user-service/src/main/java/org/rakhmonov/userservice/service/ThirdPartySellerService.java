package org.rakhmonov.userservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.userservice.dto.request.ThirdPartySellerRequest;
import org.rakhmonov.userservice.dto.response.ThirdPartySellerResponse;
import org.rakhmonov.userservice.entity.ThirdPartySeller;
import org.rakhmonov.userservice.repo.ThirdPartySellerRepository;
import org.rakhmonov.userservice.util.JwtUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThirdPartySellerService {

    private final ThirdPartySellerRepository sellerRepository;
    private final RestTemplate restTemplate;
    private static final String AUTH_SERVICE_URL = "http://localhost:8081";

    @Transactional
    public ThirdPartySellerResponse createSeller(ThirdPartySellerRequest request, String token) {
        Long currentUserId = JwtUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        if (sellerRepository.existsByUserId(currentUserId)) {
            throw new RuntimeException("User is already registered as a seller");
        }
        if (sellerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        if (sellerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        ThirdPartySeller seller = ThirdPartySeller.builder()
                .userId(currentUserId)
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .businessName(request.getBusinessName())
                .address(request.getAddress())
                .contactPerson(request.getBusinessName() != null ? request.getBusinessName() : "")
                .status(ThirdPartySeller.SellerStatus.APPROVED)
                .build();

        ThirdPartySeller savedSeller = sellerRepository.save(seller);
        updateUserRoleToSeller(currentUserId, token);

        return ThirdPartySellerResponse.fromEntity(savedSeller);
    }

    private void updateUserRoleToSeller(Long userId, String token) {
        if (token == null || token.isEmpty()) {
            System.err.println("Token is null or empty, skipping role update");
            return;
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    AUTH_SERVICE_URL + "/users/me/become-seller",
                    HttpMethod.PUT,
                    requestEntity,
                    Object.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("User role updated to SELLER successfully");
            }
        } catch (Exception e) {
            System.err.println("Failed to update user role to SELLER: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ThirdPartySellerResponse getSellerById(Long id) {
        ThirdPartySeller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
        return ThirdPartySellerResponse.fromEntity(seller);
    }

    public ThirdPartySellerResponse getSellerByUserId(Long userId) {
        ThirdPartySeller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller not found for user id: " + userId));
        return ThirdPartySellerResponse.fromEntity(seller);
    }

    public List<ThirdPartySellerResponse> getAllSellers() {
        return sellerRepository.findAll().stream()
                .map(ThirdPartySellerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ThirdPartySellerResponse updateSeller(Long id, ThirdPartySellerRequest request) {
        ThirdPartySeller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));

        if (request.getPhoneNumber() != null) {
            seller.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            seller.setEmail(request.getEmail());
        }
        if (request.getBusinessName() != null) {
            seller.setBusinessName(request.getBusinessName());
        }
        if (request.getAddress() != null) {
            seller.setAddress(request.getAddress());
        }

        ThirdPartySeller updatedSeller = sellerRepository.save(seller);
        return ThirdPartySellerResponse.fromEntity(updatedSeller);
    }

    @Transactional
    public void deleteSeller(Long id) {
        ThirdPartySeller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
        sellerRepository.delete(seller);
    }
}
