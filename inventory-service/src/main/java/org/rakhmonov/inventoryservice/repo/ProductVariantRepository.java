package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

}
