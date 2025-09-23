package org.rakhmonov.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.orderservice.entity.OrderItem;
import org.rakhmonov.orderservice.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    // TODO: Implement order item endpoints
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        // TODO: Implement create order item endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        // TODO: Implement get order item by ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        // TODO: Implement get order items by order ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByProductId(@PathVariable Long productId) {
        // TODO: Implement get order items by product ID endpoint
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem orderItem) {
        // TODO: Implement update order item endpoint
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        // TODO: Implement delete order item endpoint
        return ResponseEntity.ok().build();
    }
}


