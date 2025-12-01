package org.rakhmonov.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.dto.request.OrderRequest;
import org.rakhmonov.orderservice.dto.response.OrderResponse;
import org.rakhmonov.orderservice.entity.Order;
import org.rakhmonov.orderservice.entity.OrderItem;
import org.rakhmonov.orderservice.exception.OrderNotFoundException;
import org.rakhmonov.orderservice.event.OrderCreatedEvent;
import org.rakhmonov.orderservice.event.OrderEventPublisher;
import org.rakhmonov.orderservice.event.OrderItemEvent;
import org.rakhmonov.orderservice.event.OrderStatusChangedEvent;
import org.rakhmonov.orderservice.repo.OrderRepository;
import org.rakhmonov.orderservice.repo.OrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        String orderNumber = generateOrderNumber();
        
        BigDecimal totalAmount = orderRequest.getOrderItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        OrderResponse orderResponse = OrderResponse.fromEntity(savedOrder);
        
        List<OrderItemEvent> orderItemEvents = orderItems.stream()
                .map(item -> new OrderItemEvent(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());
        
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderNumber,
                orderItemEvents
        );
        orderEventPublisher.publishOrderCreated(orderCreatedEvent);
        
        log.info("Order created successfully with order number: {}", orderNumber);
        return orderResponse;
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderResponse.fromEntity(order);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findByIsDeletedFalse(pageable)
                .map(OrderResponse::fromEntity);
    }

    public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdAndIsDeletedFalse(userId, pageable)
                .map(OrderResponse::fromEntity);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        
        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated to {} for order ID: {}", status, id);
        
        // Publish OrderStatusChangedEvent to Kafka if status changed
        if (oldStatus != status) {
            // Create optimized event with only necessary data
            List<OrderItemEvent> orderItemEvents = updatedOrder.getOrderItems() != null ?
                    updatedOrder.getOrderItems().stream()
                            .map(item -> new OrderItemEvent(item.getProductId(), item.getQuantity()))
                            .collect(Collectors.toList()) : null;
            
            OrderStatusChangedEvent statusChangedEvent = new OrderStatusChangedEvent(
                    updatedOrder.getOrderNumber(),
                    oldStatus,
                    status,
                    orderItemEvents
            );
            orderEventPublisher.publishOrderStatusChanged(statusChangedEvent);
        }
        
        return OrderResponse.fromEntity(updatedOrder);
    }

    @Transactional
    public OrderResponse updatePaymentStatus(Long id, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        
        order.setPaymentStatus(paymentStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
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

    public Page<OrderResponse> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusAndIsDeletedFalse(status, pageable)
                .map(OrderResponse::fromEntity);
    }

    public Page<OrderResponse> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus, Pageable pageable) {
        return orderRepository.findByPaymentStatus(paymentStatus, pageable)
                .map(OrderResponse::fromEntity);
    }

    public Page<OrderResponse> getOrdersByUserIdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(OrderResponse::fromEntity);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
