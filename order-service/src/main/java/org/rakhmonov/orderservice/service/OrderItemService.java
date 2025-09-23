package org.rakhmonov.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.orderservice.entity.OrderItem;
import org.rakhmonov.orderservice.repo.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    // TODO: Implement order item management methods
    public OrderItem createOrderItem(OrderItem orderItem) {
        // TODO: Implement order item creation logic
        return null;
    }

    public OrderItem getOrderItemById(Long id) {
        // TODO: Implement get order item by ID logic
        return null;
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        // TODO: Implement get order items by order ID logic
        return null;
    }

    public List<OrderItem> getOrderItemsByProductId(Long productId) {
        // TODO: Implement get order items by product ID logic
        return null;
    }

    public OrderItem updateOrderItem(OrderItem orderItem) {
        // TODO: Implement update order item logic
        return null;
    }

    public void deleteOrderItem(Long id) {
        // TODO: Implement delete order item logic
    }
}


