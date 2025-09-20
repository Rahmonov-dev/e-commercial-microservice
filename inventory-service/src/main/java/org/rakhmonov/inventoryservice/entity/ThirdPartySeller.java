package org.rakhmonov.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "third_party_sellers", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"products"})
public class ThirdPartySeller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String companyName;
    
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal commissionRate = new BigDecimal("10.00");
    
    @Builder.Default
    private Integer deliveryTimeDays = 3;
    
    // Relationships
    @OneToMany(mappedBy = "thirdPartySeller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
    
    // Timestamps
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

