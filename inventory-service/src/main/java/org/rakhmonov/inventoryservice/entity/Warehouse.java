package org.rakhmonov.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"inventories"})
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warehouse_code", unique = true, nullable = false)
    private String warehouseCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WarehouseStatus status = WarehouseStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WarehouseType type = WarehouseType.MAIN;

    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "capacity_square_meters")
    private Integer capacitySquareMeters;

    @Column(name = "operating_hours")
    private String operatingHours;

    @Column(name = "timezone")
    private String timezone;

    // Relationships
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Inventory> inventories = new ArrayList<>();

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Business logic methods
    public String getFullAddress() {
        return String.format("%s, %s", address, city);
    }

    public boolean isOperational() {
        return status == WarehouseStatus.ACTIVE && isActive;
    }

    public boolean canHandleThirdPartyOrders() {
        return type == WarehouseType.MAIN || type == WarehouseType.DISTRIBUTION;
    }

    // Enums
    public enum WarehouseStatus {
        ACTIVE,       // Warehouse is operational
        INACTIVE,     // Warehouse is not operational
        MAINTENANCE,  // Warehouse under maintenance
        CLOSED        // Warehouse permanently closed
    }

    public enum WarehouseType {
        MAIN,          // Main warehouse for storage and fulfillment
        DISTRIBUTION,  // Distribution center for third-party orders
        REGIONAL,      // Regional warehouse
        PICKUP         // Pickup point for customers
    }
}
