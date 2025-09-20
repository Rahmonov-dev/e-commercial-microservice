package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;
import org.rakhmonov.inventoryservice.entity.Warehouse;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {
    private Long id;
    private String warehouseCode;
    private String name;
    private String description;
    private String address;
    private String city;
    private String contactPerson;
    private String phone;
    private String email;
    private String status;
    private String type;
    private Boolean isActive;
    private Integer capacitySquareMeters;
    private String operatingHours;
    private String timezone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WarehouseResponse toResponse(Warehouse entity) {
        if (entity == null) return null;

        return WarehouseResponse.builder()
                .id(entity.getId())
                .warehouseCode(entity.getWarehouseCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .address(entity.getAddress())
                .city(entity.getCity())
                .contactPerson(entity.getContactPerson())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .status(entity.getStatus().name())
                .type(entity.getType().name())
                .isActive(entity.getIsActive())
                .capacitySquareMeters(entity.getCapacitySquareMeters())
                .operatingHours(entity.getOperatingHours())
                .timezone(entity.getTimezone())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
