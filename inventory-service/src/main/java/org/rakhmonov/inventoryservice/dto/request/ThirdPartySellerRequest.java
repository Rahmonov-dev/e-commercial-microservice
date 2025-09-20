package org.rakhmonov.inventoryservice.dto.request;

import lombok.*;
import org.rakhmonov.inventoryservice.entity.ThirdPartySeller;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThirdPartySellerRequest {
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private Integer deliveryTimeDays; // optional, default 3

    public static ThirdPartySeller toEntity(ThirdPartySellerRequest request) {
        if (request == null) return null;

        return ThirdPartySeller.builder()
                .companyName(request.getCompanyName())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .deliveryTimeDays(request.getDeliveryTimeDays() != null ? request.getDeliveryTimeDays() : 3)
                .build();
    }
}
