package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name% AND c.isDeleted = false")
    Category findByNameContaining(String name);

    @Query("SELECT c.id FROM Category c WHERE c.parent.id = :categoryId AND c.isDeleted = false")
    List<Long> findCategoryChildrenIds(Long categoryId);
}
