package org.rakhmonov.inventoryservice.repo;

import org.rakhmonov.inventoryservice.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("SELECT s FROM Supplier s " +
            "WHERE LOWER(s.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR s.phone LIKE CONCAT('%', :keyword, '%') " +
            "   OR LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Supplier> searchSuppliers(@Param("keyword") String keyword);

    @Query("SELECT s FROM Supplier s " +
            "WHERE (:city IS NULL OR LOWER(s.city) = LOWER(:city)) " +
            "AND (:paymentMethod IS NULL OR LOWER(s.paymentMethod) LIKE %:paymentMethod%) " +
            "AND (:status IS NULL OR s.status = :status) " +
            "AND (:postalCode IS NULL OR s.postalCode = :postalCode) " +
            "AND (:country IS NULL OR LOWER(s.country) LIKE %:country%) " +
            "AND (:state IS NULL OR LOWER(s.state) LIKE %:state%) " +
            "AND (:minCredit IS NULL OR s.creditLimit >= :minCredit)")
    List<Supplier> filterSuppliers(
            @Param("city") String city,
            @Param("paymentMethod") String paymentMethod,
            @Param("status") Supplier.SupplierStatus status,
            @Param("postalCode") String postalCode,
            @Param("country") String country,
            @Param("state") String state,
            @Param("minCredit") BigDecimal minCredit
    );
}
