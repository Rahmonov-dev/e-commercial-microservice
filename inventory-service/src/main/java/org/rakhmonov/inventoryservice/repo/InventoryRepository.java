package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.product.id = :productId")
    List<InventoryResponse> filterInventory(@Param("warehouseId") Long warehouseId,
                                            @Param("productId") Long productId);
}
