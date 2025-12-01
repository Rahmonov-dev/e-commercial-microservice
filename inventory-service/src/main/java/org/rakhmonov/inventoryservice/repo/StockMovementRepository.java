package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findByProductId(Long productId);
    
    List<StockMovement> findByWarehouseId(Long warehouseId);
    
    List<StockMovement> findByMovementType(StockMovement.MovementType movementType);
    
    List<StockMovement> findByReferenceNumber(String referenceNumber);
    
    List<StockMovement> findByReferenceType(String referenceType);
    
    List<StockMovement> findByCreatedBy(String createdBy);
    
    List<StockMovement> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId AND sm.warehouseId = :warehouseId")
    List<StockMovement> findByProductIdAndWarehouseId(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementType = :movementType AND sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> findByMovementTypeAndDateRange(@Param("movementType") StockMovement.MovementType movementType, 
                                                      @Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId ORDER BY sm.createdAt DESC")
    List<StockMovement> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);
}
