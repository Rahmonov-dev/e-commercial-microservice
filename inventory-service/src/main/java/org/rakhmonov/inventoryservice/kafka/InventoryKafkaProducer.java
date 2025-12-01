  package org.rakhmonov.inventoryservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.inventoryservice.event.InventoryDecreasedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryKafkaProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final int KAFKA_TIMEOUT_SECONDS = 3;
    
    public void publishInventoryDecreased(InventoryDecreasedEvent event) {
        try {
            log.info("Publishing InventoryDecreasedEvent to Kafka: {}", event);
            kafkaTemplate.send("inventory-decreased-topic", event)
                    .get(KAFKA_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.info("InventoryDecreasedEvent published successfully to Kafka");
        } catch (TimeoutException e) {
            log.warn("Timeout publishing InventoryDecreasedEvent to Kafka ({}s): {}", KAFKA_TIMEOUT_SECONDS, e.getMessage());
        } catch (Exception e) {
            log.error("Error publishing InventoryDecreasedEvent to Kafka: {}", e.getMessage(), e);
        }
    }
}




