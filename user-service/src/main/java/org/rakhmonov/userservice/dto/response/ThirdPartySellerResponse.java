package org.rakhmonov.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.userservice.entity.ThirdPartySeller;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThirdPartySellerResponse {
    private Long id;
    private Long userId;
    private String phoneNumber;
    private String email;
    private String businessName;
    private String address;
    private ThirdPartySeller.SellerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ThirdPartySellerResponse fromEntity(ThirdPartySeller seller) {
        return ThirdPartySellerResponse.builder()
                .id(seller.getId())
                .userId(seller.getUserId())
                .phoneNumber(seller.getPhoneNumber())
                .email(seller.getEmail())
                .businessName(seller.getBusinessName())
                .address(seller.getAddress())
                .status(seller.getStatus())
                .createdAt(seller.getCreatedAt())
                .updatedAt(seller.getUpdatedAt())
                .build();
    }
}


