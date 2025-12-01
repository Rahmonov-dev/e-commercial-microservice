package org.rakhmonov.orderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.event.OrderCreatedEvent;
import org.rakhmonov.orderservice.event.OrderStatusChangedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderKafkaProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final int KAFKA_TIMEOUT_SECONDS = 3; // 3 seconds timeout
    
    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Publishing OrderCreatedEvent to Kafka: {}", event);
            kafkaTemplate.send("order-created-topic", event)
                    .get(KAFKA_TIMEOUT_SECONDS, TimeUnit.SECONDS); // Wait with timeout
            log.info("OrderCreatedEvent published successfully to Kafka");
        } catch (TimeoutException e) {
            log.warn("Timeout publishing OrderCreatedEvent to Kafka ({}s): {}", KAFKA_TIMEOUT_SECONDS, e.getMessage());
            throw new RuntimeException("Timeout publishing OrderCreatedEvent to Kafka", e);
        } catch (Exception e) {
            log.error("Error publishing OrderCreatedEvent to Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish OrderCreatedEvent to Kafka", e);
        }
    }
    
    public void publishOrderStatusChanged(OrderStatusChangedEvent event) {
        try {
            log.info("Publishing OrderStatusChangedEvent to Kafka: {}", event);
            kafkaTemplate.send("order-status-changed-topic", event)
                    .get(KAFKA_TIMEOUT_SECONDS, TimeUnit.SECONDS); // Wait with timeout
            log.info("OrderStatusChangedEvent published successfully to Kafka");
        } catch (TimeoutException e) {
            log.warn("Timeout publishing OrderStatusChangedEvent to Kafka ({}s): {}", KAFKA_TIMEOUT_SECONDS, e.getMessage());
            throw new RuntimeException("Timeout publishing OrderStatusChangedEvent to Kafka", e);
        } catch (Exception e) {
            log.error("Error publishing OrderStatusChangedEvent to Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish OrderStatusChangedEvent to Kafka", e);
        }
    }
}



