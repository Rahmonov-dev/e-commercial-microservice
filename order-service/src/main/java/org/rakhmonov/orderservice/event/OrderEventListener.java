package org.rakhmonov.orderservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.kafka.OrderKafkaProducer;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {
    
    private final OrderKafkaProducer orderKafkaProducer;
    private final RestTemplate restTemplate;
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent: Order {} created", event.getOrderNumber());
        
        // Try to publish to Kafka first
        try {
            orderKafkaProducer.publishOrderCreated(event);
            log.info("OrderCreatedEvent published to Kafka successfully");
        } catch (Exception e) {
            log.warn("Failed to publish OrderCreatedEvent to Kafka: {}. Trying fallback REST API...", e.getMessage());
            
            // Fallback: Direct REST API call to inventory service
            try {
                decreaseInventoryViaRestAPI(event);
                log.info("Inventory decreased via REST API fallback successfully");
            } catch (Exception restError) {
                log.error("Failed to decrease inventory via REST API fallback: {}", restError.getMessage(), restError);
                // Don't throw exception - order is already created, inventory update can be done manually
            }
        }

    }
    
    private void decreaseInventoryViaRestAPI(OrderCreatedEvent event) {
        if (event.getOrderItems() == null || event.getOrderItems().isEmpty()) {
            log.warn("Order items are null or empty, cannot decrease inventory");
            return;
        }
        
        // Extract productId and quantity from orderItems
        Map<Long, Integer> productQuantities = event.getOrderItems().stream()
                .collect(Collectors.toMap(
                        OrderItemEvent::getProductId,
                        OrderItemEvent::getQuantity,
                        Integer::sum
                ));
        
        log.info("Decreasing inventory via REST API for order {}: {}", event.getOrderNumber(), productQuantities);
        
        // Call inventory service REST API
        String inventoryServiceUrl = "http://localhost:8082/api/inventory/decrease-stock-for-order";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<Long, Integer>> request = new HttpEntity<>(productQuantities, headers);
        
        ResponseEntity<?> response = restTemplate.exchange(
                inventoryServiceUrl,
                HttpMethod.POST,
                request,
                Object.class
        );
        
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Inventory decreased successfully via REST API for order: {}", event.getOrderNumber());
        } else {
            throw new RuntimeException("Failed to decrease inventory: " + response.getStatusCode());
        }
    }
    
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Handling OrderStatusChangedEvent: Order {} status changed from {} to {}", 
                event.getOrderNumber(), event.getOldStatus(), event.getNewStatus());
        
        orderKafkaProducer.publishOrderStatusChanged(event);

    }
}

