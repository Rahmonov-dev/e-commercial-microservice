package org.rakhmonov.warehouseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.warehouseservice.entity.Warehouse;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponse {
    
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private Integer capacity;
    private Integer currentOccupancy;
    private Integer availableCapacity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static WarehouseResponse fromEntity(Warehouse warehouse) {
        if (warehouse == null) return null;
        
        Integer availableCapacity = warehouse.getCapacity() != null && warehouse.getCurrentOccupancy() != null
                ? warehouse.getCapacity() - warehouse.getCurrentOccupancy()
                : null;
        
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .address(warehouse.getAddress())
                .city(warehouse.getCity())
                .state(warehouse.getState())
                .country(warehouse.getCountry())
                .postalCode(warehouse.getPostalCode())
                .phoneNumber(warehouse.getPhoneNumber())
                .email(warehouse.getEmail())
                .capacity(warehouse.getCapacity())
                .currentOccupancy(warehouse.getCurrentOccupancy())
                .availableCapacity(availableCapacity)
                .status(warehouse.getStatus() != null ? warehouse.getStatus().name() : null)
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }
}


