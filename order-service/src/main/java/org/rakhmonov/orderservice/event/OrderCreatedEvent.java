package org.rakhmonov.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    
    public OrderCreatedEvent(Long orderId, String orderNumber, Long userId, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
    }
}
