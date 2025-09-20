package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;
import org.rakhmonov.inventoryservice.entity.ThirdPartySeller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThirdPartySellerResponse {
    private Long id;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;
    private BigDecimal commissionRate;
    private Integer deliveryTimeDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ThirdPartySellerResponse toResponse(ThirdPartySeller entity) {
        if (entity == null) return null;

        return ThirdPartySellerResponse.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .contactPerson(entity.getContactPerson())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .isActive(entity.getIsActive())
                .commissionRate(entity.getCommissionRate())
                .deliveryTimeDays(entity.getDeliveryTimeDays())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}
