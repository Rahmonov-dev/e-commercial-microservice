package org.rakhmonov.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "suppliers", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"products"})
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "website")
    private String website;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SupplierType supplierType = SupplierType.MANUFACTURER;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SupplierStatus status = SupplierStatus.ACTIVE;

    @Column(name = "payment_terms")
    private String paymentTerms; // "Net 30", "COD", etc.

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "current_balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "lead_time_days")
    @Builder.Default
    private Integer leadTimeDays = 7;

    @Column(name = "minimum_order_amount", precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "successful_orders")
    @Builder.Default
    private Integer successfulOrders = 0;

    @Column(name = "cancelled_orders")
    @Builder.Default
    private Integer cancelledOrders = 0;

    @Column(name = "total_purchases", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalPurchases = BigDecimal.ZERO;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum SupplierType {
        MANUFACTURER,   // Makes the products
        WHOLESALER,     // Buys in bulk and resells
        DISTRIBUTOR,    // Specialized distributor
        IMPORTER,       // Imports products
        AGENT           // Sales agent
    }

    public enum SupplierStatus {
        ACTIVE,         // Active supplier
        INACTIVE,       // Temporarily inactive
        SUSPENDED,      // Suspended due to issues
        BLACKLISTED     // Permanently blacklisted
    }

    // Business logic methods
    public double getSuccessRate() {
        if (totalOrders == 0) {
            return 0.0;
        }
        return (double) successfulOrders / totalOrders * 100;
    }

    public double getCancellationRate() {
        if (totalOrders == 0) {
            return 0.0;
        }
        return (double) cancelledOrders / totalOrders * 100;
    }

    public boolean canPlaceOrder(BigDecimal orderAmount) {
        if (status != SupplierStatus.ACTIVE) {
            return false;
        }

        if (minimumOrderAmount != null && orderAmount.compareTo(minimumOrderAmount) < 0) {
            return false;
        }

        if (creditLimit != null && currentBalance.add(orderAmount).compareTo(creditLimit) > 0) {
            return false;
        }

        return true;
    }

    public void updateOrderStats(boolean successful, BigDecimal orderAmount) {
        this.totalOrders++;
        if (successful) {
            this.successfulOrders++;
            this.totalPurchases = this.totalPurchases.add(orderAmount);
            this.currentBalance = this.currentBalance.add(orderAmount);
        } else {
            this.cancelledOrders++;
        }
    }
}
