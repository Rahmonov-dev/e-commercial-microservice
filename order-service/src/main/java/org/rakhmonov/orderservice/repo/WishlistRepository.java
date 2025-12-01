package org.rakhmonov.orderservice.repo;

import org.rakhmonov.orderservice.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    @Query("SELECT w FROM Wishlist w WHERE w.userId = :userId")
    Optional<Wishlist> findByUserId(@Param("userId") Long userId);
}
