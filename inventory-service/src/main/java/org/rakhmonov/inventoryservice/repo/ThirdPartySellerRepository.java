package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.dto.response.ThirdPartySellerResponse;
import org.rakhmonov.inventoryservice.entity.ThirdPartySeller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThirdPartySellerRepository extends JpaRepository<ThirdPartySeller, Long> {
    @Query("SELECT t FROM ThirdPartySeller t WHERE t.companyName LIKE %:name%")
    List<ThirdPartySellerResponse> findByCompanyNameContaining(String name);
}
