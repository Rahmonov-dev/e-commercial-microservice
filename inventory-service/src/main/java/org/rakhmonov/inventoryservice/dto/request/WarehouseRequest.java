package org.rakhmonov.inventoryservice.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRequest {
    private String warehouseCode;
    private String name;
    private String description;
    private String address;
    private String city;
    private String contactPerson;
    private String phone;
    private String email;
    private Integer capacitySquareMeters;
    private String operatingHours;

    // Note: Warehouse entity is in a different microservice (warehouse-service or user-service)
    // These methods are kept for potential future use or REST communication
    // Uncomment and modify when Warehouse entity is available
    /*
    public static Warehouse toEntity(WarehouseRequest dto) {
        if (dto == null) return null;
        // Implementation removed - Warehouse entity not in this service
        return null;
    }
    public static void updateEntity(WarehouseRequest dto, Warehouse entity) {
        if (dto == null || entity == null) return;
        // Implementation removed - Warehouse entity not in this service
    }
    */

}
