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
    Optional<ClickPayment> findByClickTransId(String clickTransId);
    Optional<ClickPayment> findByMerchantTransId(String merchantTransId);
    List<ClickPayment> findByOrderId(Long orderId);
    List<ClickPayment> findByUserId(Long userId);
    List<ClickPayment> findByStatus(ClickPayment.PaymentStatus status);
    Optional<ClickPayment> findByPrepareId(String prepareId);
    List<ClickPayment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT cp FROM ClickPayment cp WHERE cp.userId = :userId AND cp.status = :status")
    List<ClickPayment> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ClickPayment.PaymentStatus status);

    @Query("SELECT cp FROM ClickPayment cp WHERE cp.orderId = :orderId AND cp.status = :status")
    List<ClickPayment> findByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") ClickPayment.PaymentStatus status);

    @Query("SELECT cp FROM ClickPayment cp WHERE cp.status = :status AND cp.createdAt BETWEEN :startDate AND :endDate")
    List<ClickPayment> findByStatusAndDateRange(@Param("status") ClickPayment.PaymentStatus status,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    List<ClickPayment> findByUserIdOrderByCreatedAtDesc(Long userId);
}

