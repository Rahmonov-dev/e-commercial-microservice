package org.rakhmonov.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.dto.request.OrderRequest;
import org.rakhmonov.orderservice.dto.response.OrderResponse;
import org.rakhmonov.orderservice.entity.Order;
import org.rakhmonov.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest, Authentication authentication) {
        String currentUserPhone = authentication.getName();
        log.info("Creating order for user: {}", currentUserPhone);
        OrderResponse order = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        log.info("Getting orders for user ID: {}", userId);
        Page<OrderResponse> orders = orderService.getOrdersByUserId(userId,  pageable);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<OrderResponse> orders = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByPaymentStatus(
            @PathVariable Order.PaymentStatus paymentStatus,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<OrderResponse> orders = orderService.getOrdersByPaymentStatus(paymentStatus, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUserIdAndStatus(
            @PathVariable Long userId,
            @PathVariable Order.OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<OrderResponse> orders = orderService.getOrdersByUserIdAndStatus(userId, status, pageable);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updatePaymentStatus(@PathVariable Long id, @RequestParam Order.PaymentStatus paymentStatus) {
        OrderResponse order = orderService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long id) {
        OrderResponse order = orderService.updateOrderStatus(id, Order.OrderStatus.CONFIRMED);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable Long id) {
        OrderResponse order = orderService.updateOrderStatus(id, Order.OrderStatus.SHIPPED);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable Long id) {
        OrderResponse order = orderService.updateOrderStatus(id, Order.OrderStatus.DELIVERED);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        OrderResponse order = orderService.updateOrderStatus(id, Order.OrderStatus.CANCELLED);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
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
