package org.rakhmonov.orderservice.repo;

import org.rakhmonov.orderservice.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
}
