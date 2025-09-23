package org.rakhmonov.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.dto.request.OrderRequest;
import org.rakhmonov.orderservice.dto.response.OrderResponse;
import org.rakhmonov.orderservice.entity.Order;
import org.rakhmonov.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse order = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<OrderResponse>> getOrdersByPaymentStatus(@PathVariable Order.PaymentStatus paymentStatus) {
        List<OrderResponse> orders = orderService.getOrdersByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserIdAndStatus(@PathVariable Long userId, @PathVariable Order.OrderStatus status) {
        List<OrderResponse> orders = orderService.getOrdersByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<OrderResponse> updatePaymentStatus(@PathVariable Long id, @RequestParam Order.PaymentStatus paymentStatus) {
        OrderResponse order = orderService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // Special endpoints for common operations
    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long id) {
        OrderResponse order = orderService.updateOrderStatus(id, Order.OrderStatus.CONFIRMED);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable Long id) {
        OrderResponse order = orderService.updateOrderStatus(id, Order.OrderStatus.SHIPPED);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable Long id) {
        OrderResponse order = orderService.updateOrderStatus(id, Order.OrderStatus.DELIVERED);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        OrderResponse order = orderService.updateOrderStatus(id, Order.OrderStatus.CANCELLED);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<OrderResponse> markOrderAsPaid(@PathVariable Long id) {
        OrderResponse order = orderService.updatePaymentStatus(id, Order.PaymentStatus.PAID);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/mark-payment-failed")
    public ResponseEntity<OrderResponse> markPaymentAsFailed(@PathVariable Long id) {
        OrderResponse order = orderService.updatePaymentStatus(id, Order.PaymentStatus.FAILED);
        return ResponseEntity.ok(order);
    }
}
