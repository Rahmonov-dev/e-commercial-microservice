package org.rakhmonov.paymentservice.repo;

import org.rakhmonov.paymentservice.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    
    List<PaymentMethod> findByUserId(Long userId);
    
    List<PaymentMethod> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    Optional<PaymentMethod> findByUserIdAndIsDefault(Long userId, Boolean isDefault);
    
    List<PaymentMethod> findByType(PaymentMethod.PaymentType type);
    
    List<PaymentMethod> findByProvider(String provider);
    
    List<PaymentMethod> findByIsActive(Boolean isActive);
    
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.userId = :userId AND pm.isActive = true ORDER BY pm.isDefault DESC, pm.createdAt DESC")
    List<PaymentMethod> findActivePaymentMethodsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.userId = :userId AND pm.type = :type AND pm.isActive = true")
    List<PaymentMethod> findByUserIdAndTypeAndIsActive(@Param("userId") Long userId, @Param("type") PaymentMethod.PaymentType type);
    
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.userId = :userId AND pm.provider = :provider AND pm.isActive = true")
    List<PaymentMethod> findByUserIdAndProviderAndIsActive(@Param("userId") Long userId, @Param("provider") String provider);
    
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.userId = :userId AND pm.accountNumber = :accountNumber")
    Optional<PaymentMethod> findByUserIdAndAccountNumber(@Param("userId") Long userId, @Param("accountNumber") String accountNumber);
}


