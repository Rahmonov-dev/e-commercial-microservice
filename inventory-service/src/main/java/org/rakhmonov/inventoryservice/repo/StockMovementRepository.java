package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
