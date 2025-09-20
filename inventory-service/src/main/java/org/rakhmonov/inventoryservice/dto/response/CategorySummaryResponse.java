package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
}

