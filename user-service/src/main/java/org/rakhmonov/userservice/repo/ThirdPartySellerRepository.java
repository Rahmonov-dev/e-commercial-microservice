package org.rakhmonov.userservice.repo;

import org.rakhmonov.userservice.entity.ThirdPartySeller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThirdPartySellerRepository extends JpaRepository<ThirdPartySeller, Long> {
    Optional<ThirdPartySeller> findByUserId(Long userId);
    Optional<ThirdPartySeller> findByPhoneNumber(String phoneNumber);
    Optional<ThirdPartySeller> findByEmail(String email);
    boolean existsByUserId(Long userId);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
