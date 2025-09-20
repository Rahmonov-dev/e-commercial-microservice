package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartySellerSummaryResponse {
    private Long id;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private Boolean isActive;
    private BigDecimal commissionRate;
    private Integer deliveryTimeDays;
}

