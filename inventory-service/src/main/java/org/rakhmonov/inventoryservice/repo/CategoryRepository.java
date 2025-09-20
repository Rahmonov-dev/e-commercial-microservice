package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    Category findByNameContaining(String name);
}
