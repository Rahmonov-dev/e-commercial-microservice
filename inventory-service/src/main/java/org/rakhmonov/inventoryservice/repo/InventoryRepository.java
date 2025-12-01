package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.entity.Inventory;
import org.rakhmonov.inventoryservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.warehouseId = :warehouseId AND i.product.id = :productId AND i.isDeleted = false")
    List<InventoryResponse> filterInventory(@Param("warehouseId") Long warehouseId,
                                            @Param("productId") Long productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.warehouseId = :warehouseId AND i.isDeleted = false")
    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    
    @Query("SELECT i FROM Inventory i WHERE i.product = :product AND i.isDeleted = false")
    List<Inventory> findByProduct(@Param("product") Product product);
    
    @Query("SELECT i FROM Inventory i WHERE i.warehouseId = :warehouseId AND i.isDeleted = false")
    List<Inventory> findByWarehouseId(@Param("warehouseId") Long warehouseId);
}
