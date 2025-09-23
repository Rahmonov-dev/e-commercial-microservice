package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.entity.Inventory;
import org.rakhmonov.inventoryservice.entity.Product;
import org.rakhmonov.inventoryservice.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.product.id = :productId")
    List<InventoryResponse> filterInventory(@Param("warehouseId") Long warehouseId,
                                            @Param("productId") Long productId);
    
    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
    
    List<Inventory> findByProduct(Product product);
    
    List<Inventory> findByWarehouse(Warehouse warehouse);
}
