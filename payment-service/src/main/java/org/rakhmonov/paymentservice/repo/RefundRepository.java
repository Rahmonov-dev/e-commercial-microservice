package org.rakhmonov.paymentservice.repo;

import org.rakhmonov.paymentservice.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    Optional<Refund> findByRefundNumber(String refundNumber);
    
    List<Refund> findByPaymentId(Long paymentId);
    
    List<Refund> findByStatus(Refund.RefundStatus status);
    
    List<Refund> findByRequestedBy(Long requestedBy);
    
    List<Refund> findByApprovedBy(Long approvedBy);
    
    List<Refund> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT r FROM Refund r WHERE r.payment.id = :paymentId AND r.status = :status")
    List<Refund> findByPaymentIdAndStatus(@Param("paymentId") Long paymentId, @Param("status") Refund.RefundStatus status);
    
    @Query("SELECT r FROM Refund r WHERE r.requestedBy = :requestedBy AND r.status = :status")
    List<Refund> findByRequestedByAndStatus(@Param("requestedBy") Long requestedBy, @Param("status") Refund.RefundStatus status);
    
    @Query("SELECT r FROM Refund r WHERE r.status = :status AND r.createdAt BETWEEN :startDate AND :endDate")
    List<Refund> findByStatusAndDateRange(@Param("status") Refund.RefundStatus status, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM Refund r WHERE r.payment.userId = :userId ORDER BY r.createdAt DESC")
    List<Refund> findByPaymentUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Refund r WHERE r.payment.orderId = :orderId")
    List<Refund> findByPaymentOrderId(@Param("orderId") Long orderId);
}


