package org.rakhmonov.orderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.entity.Order;
import org.rakhmonov.orderservice.repo.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderKafkaConsumer {
    
    private final OrderRepository orderRepository;
    
    @KafkaListener(topics = "inventory-decreased-topic", groupId = "order-service-group")
    @Transactional
    public void handleInventoryDecreased(Map<String, Object> event) {
        try {
            log.info("Received InventoryDecreasedEvent: {}", event);
            
            String orderNumber = (String) event.get("orderNumber");
            Boolean success = (Boolean) event.get("success");
            
            if (orderNumber == null) {
                log.warn("Order number is null in InventoryDecreasedEvent");
                return;
            }
            
            // Find order by order number
            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElse(null);
            
            if (order == null) {
                log.warn("Order not found with order number: {}", orderNumber);
                return;
            }
            
            if (success != null && success) {
                if (order.getStatus() == Order.OrderStatus.PENDING) {
                    order.setStatus(Order.OrderStatus.CONFIRMED);
                    orderRepository.save(order);
                    log.info("Order {} status updated to CONFIRMED after successful inventory decrease", orderNumber);
                } else {
                    log.info("Order {} status is already {}, not updating", orderNumber, order.getStatus());
                }
            } else {
                log.warn("Inventory decrease failed for order {}: {}", orderNumber, event.get("message"));
            }
            
        } catch (Exception e) {
            log.error("Error processing InventoryDecreasedEvent: {}", e.getMessage(), e);
        }
    }
}




