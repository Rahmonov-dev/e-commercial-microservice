package org.rakhmonov.orderservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent: {}", event);
        eventPublisher.publishEvent(event);
    }
    
    public void publishOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Publishing OrderStatusChangedEvent: {}", event);
        eventPublisher.publishEvent(event);
    }
}

