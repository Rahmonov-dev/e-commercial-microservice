package org.rakhmonov.orderservice.repo;

import org.rakhmonov.orderservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByUserId(Long userId);
    
    List<Cart> findByUserIdOrderByCreatedAtDesc(Long userId);
}


