package org.rakhmonov.paymentservice.repo;

import org.rakhmonov.paymentservice.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    
    Optional<PaymentTransaction> findByTransactionId(String transactionId);
    
    List<PaymentTransaction> findByPaymentId(Long paymentId);
    
    List<PaymentTransaction> findByTransactionType(PaymentTransaction.TransactionType transactionType);
    
    List<PaymentTransaction> findByStatus(PaymentTransaction.TransactionStatus status);
    
    List<PaymentTransaction> findByGatewayTransactionId(String gatewayTransactionId);
    
    List<PaymentTransaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId AND pt.transactionType = :transactionType")
    List<PaymentTransaction> findByPaymentIdAndTransactionType(@Param("paymentId") Long paymentId, 
                                                              @Param("transactionType") PaymentTransaction.TransactionType transactionType);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId AND pt.status = :status")
    List<PaymentTransaction> findByPaymentIdAndStatus(@Param("paymentId") Long paymentId, 
                                                     @Param("status") PaymentTransaction.TransactionStatus status);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.transactionType = :transactionType AND pt.status = :status")
    List<PaymentTransaction> findByTransactionTypeAndStatus(@Param("transactionType") PaymentTransaction.TransactionType transactionType, 
                                                           @Param("status") PaymentTransaction.TransactionStatus status);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.status = :status AND pt.createdAt BETWEEN :startDate AND :endDate")
    List<PaymentTransaction> findByStatusAndDateRange(@Param("status") PaymentTransaction.TransactionStatus status, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.userId = :userId ORDER BY pt.createdAt DESC")
    List<PaymentTransaction> findByPaymentUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.orderId = :orderId")
    List<PaymentTransaction> findByPaymentOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.retryCount > 0 AND pt.status = 'FAILED'")
    List<PaymentTransaction> findFailedTransactionsWithRetries();
}


