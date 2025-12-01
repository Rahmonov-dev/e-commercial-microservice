package org.rakhmonov.warehouseservice.repo;

import org.rakhmonov.warehouseservice.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    
    // Find by name
    Optional<Warehouse> findByName(String name);
    boolean existsByName(String name);
    
    // Find by status with pagination
    Page<Warehouse> findByStatusAndIsDeletedFalse(Warehouse.WarehouseStatus status, Pageable pageable);
    
    // Find active warehouses with pagination
    Page<Warehouse> findByIsDeletedFalse(Pageable pageable);
    Optional<Warehouse> findByIdAndIsDeletedFalse(Long id);
    
    // Find by city with pagination
    Page<Warehouse> findByCityAndIsDeletedFalse(String city, Pageable pageable);
    
    // Find by country with pagination
    Page<Warehouse> findByCountryAndIsDeletedFalse(String country, Pageable pageable);
    
    // Search by name with pagination
    @Query("SELECT w FROM Warehouse w WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :name, '%')) AND w.isDeleted = false")
    Page<Warehouse> searchByName(@Param("name") String name, Pageable pageable);
    
    // Count by status
    @Query("SELECT COUNT(w) FROM Warehouse w WHERE w.status = :status AND w.isDeleted = false")
    long countByStatus(@Param("status") Warehouse.WarehouseStatus status);
    
    // Find warehouses with available capacity
    @Query("SELECT w FROM Warehouse w WHERE (w.capacity - w.currentOccupancy) >= :minAvailable AND w.isDeleted = false")
    Page<Warehouse> findWarehousesWithAvailableCapacity(@Param("minAvailable") Integer minAvailable, Pageable pageable);
}


