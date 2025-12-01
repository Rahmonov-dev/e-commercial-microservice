package org.rakhmonov.orderservice.repo;

import org.rakhmonov.orderservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.isDeleted = false")
    Optional<Cart> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Cart> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}


