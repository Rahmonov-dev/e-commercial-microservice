package org.rakhmonov.paymentservice.repo;

import org.rakhmonov.paymentservice.entity.ClickPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClickPaymentRepository extends JpaRepository<ClickPayment, Long> {
    Optional<ClickPayment> findByInvoiceId(Long invoiceId);
    Optional<ClickPayment> findByPaymentId(Long paymentId);
    Optional<ClickPayment> findByMerchantTransId(String merchantTransId);
    List<ClickPayment> findByOrderId(Long orderId);
    List<ClickPayment> findByUserId(Long userId);
    List<ClickPayment> findByPhoneNumber(String phoneNumber);
    List<ClickPayment> findByCardToken(String cardToken);
    List<ClickPayment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT cp FROM ClickPayment cp WHERE cp.userId = :userId AND cp.paymentStatus = :paymentStatus")
    List<ClickPayment> findByUserIdAndPaymentStatus(@Param("userId") Long userId, @Param("paymentStatus") Integer paymentStatus);

    @Query("SELECT cp FROM ClickPayment cp WHERE cp.orderId = :orderId AND cp.paymentStatus = :paymentStatus")
    List<ClickPayment> findByOrderIdAndPaymentStatus(@Param("orderId") Long orderId, @Param("paymentStatus") Integer paymentStatus);

    @Query("SELECT cp FROM ClickPayment cp WHERE cp.paymentStatus = :paymentStatus AND cp.createdAt BETWEEN :startDate AND :endDate")
    List<ClickPayment> findByPaymentStatusAndDateRange(@Param("paymentStatus") Integer paymentStatus,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    List<ClickPayment> findByUserIdOrderByCreatedAtDesc(Long userId);
}

