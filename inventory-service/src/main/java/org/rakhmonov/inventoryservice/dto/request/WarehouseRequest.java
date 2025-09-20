package org.rakhmonov.inventoryservice.dto.request;

import lombok.*;
import org.rakhmonov.inventoryservice.entity.Warehouse;

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
    private String status;   // Enum nomi sifatida yuboriladi (ACTIVE, INACTIVE, ...)
    private String type;     // Enum nomi sifatida yuboriladi (MAIN, DISTRIBUTION, ...)
    private Boolean isActive;
    private Integer capacitySquareMeters;
    private String operatingHours;
    private String timezone;

    public static Warehouse toEntity(WarehouseRequest dto) {
        if (dto == null) return null;

        Warehouse entity = new Warehouse();
        entity.setWarehouseCode(dto.getWarehouseCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setContactPerson(dto.getContactPerson());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());

        if (dto.getStatus() != null) {
            entity.setStatus(Warehouse.WarehouseStatus.valueOf(dto.getStatus()));
        }
        if (dto.getType() != null) {
            entity.setType(Warehouse.WarehouseType.valueOf(dto.getType()));
        }

        entity.setIsActive(dto.getIsActive());
        entity.setCapacitySquareMeters(dto.getCapacitySquareMeters());
        entity.setOperatingHours(dto.getOperatingHours());
        entity.setTimezone(dto.getTimezone());

        return entity;
    }
    public static void updateEntity(WarehouseRequest dto, Warehouse entity) {
        if (dto == null || entity == null) return;

        entity.setWarehouseCode(dto.getWarehouseCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setContactPerson(dto.getContactPerson());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());

        if (dto.getStatus() != null) {
            entity.setStatus(Warehouse.WarehouseStatus.valueOf(dto.getStatus()));
        }
        if (dto.getType() != null) {
            entity.setType(Warehouse.WarehouseType.valueOf(dto.getType()));
        }

        entity.setIsActive(dto.getIsActive());
        entity.setCapacitySquareMeters(dto.getCapacitySquareMeters());
        entity.setOperatingHours(dto.getOperatingHours());
        entity.setTimezone(dto.getTimezone());
    }

}
