package org.rakhmonov.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.orderservice.entity.Order;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChangedEvent {
    
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private Order.OrderStatus oldStatus;
    private Order.OrderStatus newStatus;
    private LocalDateTime changedAt;
    
    public OrderStatusChangedEvent(Long orderId, String orderNumber, Long userId, 
                                 Order.OrderStatus oldStatus, Order.OrderStatus newStatus) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = LocalDateTime.now();
    }
}

