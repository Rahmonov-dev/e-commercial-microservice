package org.rakhmonov.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.orderservice.entity.Order;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChangedEvent {
    
    private String orderNumber;
    private Order.OrderStatus oldStatus;
    private Order.OrderStatus newStatus;
    private LocalDateTime changedAt;
    private List<OrderItemEvent> orderItems;
    
    public OrderStatusChangedEvent(String orderNumber,
                                 Order.OrderStatus oldStatus, Order.OrderStatus newStatus,
                                 List<OrderItemEvent> orderItems) {
        this.orderNumber = orderNumber;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = LocalDateTime.now();
        this.orderItems = orderItems;
    }
}

