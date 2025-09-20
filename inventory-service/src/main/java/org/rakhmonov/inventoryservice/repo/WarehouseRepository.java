package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.entity.Inventory;
import org.rakhmonov.inventoryservice.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("SELECT w FROM Warehouse w WHERE w.status = :status")
    List<Warehouse> findByStatus(@Param("status") Warehouse.WarehouseStatus status);

    @Query("SELECT w FROM Warehouse w WHERE w.type = :type")
    List<Warehouse> findByType(@Param("type") Warehouse.WarehouseType warehouseType);

    @Query("SELECT w FROM Warehouse w WHERE w.name LIKE %:name% OR w.city LIKE %:city%")
    List<Warehouse> findByNameContaining(@Param("name") String name,
                                         @Param("city") String city);

    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :id")
    List<Inventory> getInventory(Long id);
}
