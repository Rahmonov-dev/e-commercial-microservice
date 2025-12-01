package org.rakhmonov.inventoryservice.dto.response;

import lombok.*;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Note: Warehouse entity is in a different microservice (warehouse-service or user-service)
    // This method is kept for potential future use or REST communication
    // Uncomment and modify when Warehouse entity is available
    /*
    public static WarehouseResponse toResponse(Warehouse entity) {
        if (entity == null) return null;
        // Implementation removed - Warehouse entity not in this service
        return null;
    }
    */
}
