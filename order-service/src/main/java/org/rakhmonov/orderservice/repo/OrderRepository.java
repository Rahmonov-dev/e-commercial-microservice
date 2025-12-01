package org.rakhmonov.orderservice.repo;

import org.rakhmonov.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByIsDeletedFalse(Pageable pageable);
    
    Page<Order> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.orderNumber = :orderNumber AND o.isDeleted = false")
    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);
    
    Page<Order> findByStatusAndIsDeletedFalse(Order.OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status AND o.isDeleted = false")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.isDeleted = false")
    List<Order> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.isDeleted = false")
    List<Order> findByStatus(@Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :paymentStatus AND o.isDeleted = false")
    Page<Order> findByPaymentStatus(@Param("paymentStatus") Order.PaymentStatus paymentStatus, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status AND o.isDeleted = false")
    Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Order.OrderStatus status, Pageable pageable);
}


