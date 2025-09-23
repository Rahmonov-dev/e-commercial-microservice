package org.rakhmonov.paymentservice.repo;

import org.rakhmonov.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    
    List<Payment> findByOrderId(Long orderId);
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    List<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByGatewayReference(String gatewayReference);
    
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.status = :status")
    List<Payment> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId AND p.status = :status")
    List<Payment> findByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentMethodAndDateRange(@Param("paymentMethod") Payment.PaymentMethod paymentMethod, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByStatusAndDateRange(@Param("status") Payment.PaymentStatus status, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
}


