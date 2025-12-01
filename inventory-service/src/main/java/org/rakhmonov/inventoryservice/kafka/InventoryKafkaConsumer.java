package org.rakhmonov.inventoryservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.inventoryservice.event.InventoryDecreasedEvent;
import org.rakhmonov.inventoryservice.service.InventoryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class InventoryKafkaConsumer {
    
    private final InventoryService inventoryService;
    private final InventoryKafkaProducer inventoryKafkaProducer;
    
    @KafkaListener(topics = "order-created-topic", groupId = "inventory-service-group")
    public void handleOrderCreated(Map<String, Object> orderEvent) {
        try {
            log.info("Received OrderCreatedEvent: {}", orderEvent);
            
            String orderNumber = (String) orderEvent.get("orderNumber");
            
            // Get orderItems directly from event (optimized structure)
            List<Map<String, Object>> orderItems = (List<Map<String, Object>>) orderEvent.get("orderItems");
            if (orderItems == null || orderItems.isEmpty()) {
                log.warn("Order items are empty for order: {}", orderNumber);
                return;
            }
            
            log.info("Processing order: {} with {} items", orderNumber, orderItems.size());
            
            // Extract productId and quantity from orderItems
            Map<Long, Integer> productQuantities = orderItems.stream()
                    .collect(Collectors.toMap(
                            item -> ((Number) item.get("productId")).longValue(),
                            item -> ((Number) item.get("quantity")).intValue(),
                            Integer::sum
                    ));
            
            log.info("Decreasing inventory for order {}: {}", orderNumber, productQuantities);
            
            // Decrease inventory stock for all products
            try {
                inventoryService.decreaseStockForOrder(productQuantities);
                
                // Publish success event to order-service
                InventoryDecreasedEvent successEvent = new InventoryDecreasedEvent(
                        orderNumber,
                        true,
                        "Inventory decreased successfully"
                );
                inventoryKafkaProducer.publishInventoryDecreased(successEvent);
                
                log.info("Successfully decreased inventory for order: {}", orderNumber);
            } catch (Exception e) {
                log.error("Failed to decrease inventory for order {}: {}", orderNumber, e.getMessage(), e);
                
                // Publish failure event to order-service
                InventoryDecreasedEvent failureEvent = new InventoryDecreasedEvent(
                        orderNumber,
                        false,
                        "Failed to decrease inventory: " + e.getMessage()
                );
                inventoryKafkaProducer.publishInventoryDecreased(failureEvent);
            }
            
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "order-status-changed-topic", groupId = "inventory-service-group")
    public void handleOrderStatusChanged(Map<String, Object> orderEvent) {
        try {
            log.info("Received OrderStatusChangedEvent: {}", orderEvent);
            
            String orderNumber = (String) orderEvent.get("orderNumber");
            String newStatus = (String) orderEvent.get("newStatus");
            String oldStatus = (String) orderEvent.get("oldStatus");
            
            log.info("Order {} status changed from {} to {}", orderNumber, oldStatus, newStatus);
            
            // If order is cancelled, restore inventory
            if ("CANCELLED".equals(newStatus)) {
                log.info("Order {} was cancelled, need to restore inventory", orderNumber);
                
                // Get orderItems directly from event
                List<Map<String, Object>> orderItems = (List<Map<String, Object>>) orderEvent.get("orderItems");
                if (orderItems != null && !orderItems.isEmpty()) {
                    // Restore inventory by increasing stock
                    Map<Long, Integer> productQuantities = orderItems.stream()
                            .collect(Collectors.toMap(
                                    item -> ((Number) item.get("productId")).longValue(),
                                    item -> ((Number) item.get("quantity")).intValue(),
                                    Integer::sum
                            ));
                    
                    log.info("Restoring inventory for order {}: {}", orderNumber, productQuantities);
                    
                    // Increase stock (reverse the decrease)
                    inventoryService.increaseStockForOrder(productQuantities);
                    
                    log.info("Successfully restored inventory for order: {}", orderNumber);
                } else {
                    log.warn("Order items are empty for order: {}, cannot restore inventory", orderNumber);
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing OrderStatusChangedEvent: {}", e.getMessage(), e);
        }
    }
}



