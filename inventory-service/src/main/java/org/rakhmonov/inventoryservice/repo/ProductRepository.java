package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.entity.Category;
import org.rakhmonov.inventoryservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isDeleted = false")
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.supplierId = :supplierId AND p.isDeleted = false")
    List<Product> findBySupplierId(Long supplierId);

    @Query("SELECT p FROM Product p WHERE p.thirdPartySellerId = :sellerId AND p.isDeleted = false")
    Page<Product> findByThirdPartySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.isDeleted = false")
    Page<Product> findByCategoryId(@Param("categoryIds")List<Long> categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND p.isDeleted = false")
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String searchTerm, Pageable pageable);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

}
