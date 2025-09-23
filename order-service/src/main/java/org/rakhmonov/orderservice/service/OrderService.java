package org.rakhmonov.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.dto.request.OrderRequest;
import org.rakhmonov.orderservice.dto.response.OrderResponse;
import org.rakhmonov.orderservice.entity.Order;
import org.rakhmonov.orderservice.entity.OrderItem;
import org.rakhmonov.orderservice.exception.OrderNotFoundException;
import org.rakhmonov.orderservice.repo.OrderRepository;
import org.rakhmonov.orderservice.repo.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Generate unique order number
        String orderNumber = generateOrderNumber();
        
        // Calculate total amount
        BigDecimal totalAmount = orderRequest.getOrderItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order entity
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(orderRequest.getUserId())
                .status(Order.OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .shippingAddress(orderRequest.getShippingAddress())
                .paymentMethod(orderRequest.getPaymentMethod())
                .paymentStatus(Order.PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Create order items
        List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                .map(itemRequest -> {
                    BigDecimal itemTotal = itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                    return OrderItem.builder()
                            .order(savedOrder)
                            .productId(itemRequest.getProductId())
                            .productName(itemRequest.getProductName())
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(itemRequest.getUnitPrice())
                            .totalAmount(itemTotal)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                })
                .toList();

        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        log.info("Order created successfully with order number: {}", orderNumber);
        return OrderResponse.fromEntity(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated to {} for order ID: {}", status, id);
        
        return OrderResponse.fromEntity(updatedOrder);
    }

    @Transactional
    public OrderResponse updatePaymentStatus(Long id, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        
        order.setPaymentStatus(paymentStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        // If payment is successful, update order status to confirmed
        if (paymentStatus == Order.PaymentStatus.PAID && order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.CONFIRMED);
        }
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Payment status updated to {} for order ID: {}", paymentStatus, id);
        
        return OrderResponse.fromEntity(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        
        // Only allow deletion of pending orders
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot delete order with status: " + order.getStatus());
        }
        
        orderRepository.delete(order);
        log.info("Order deleted with ID: {}", id);
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with order number: " + orderNumber));
        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    public List<OrderResponse> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    public List<OrderResponse> getOrdersByUserIdAndStatus(Long userId, Order.OrderStatus status) {
        return orderRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
