package org.rakhmonov.orderservice.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent: Order {} created for user {}", 
                event.getOrderNumber(), event.getUserId());
        
        // Here you can add business logic like:
        // - Send confirmation email
        // - Update inventory
        // - Send notifications
        // - Update analytics
    }
    
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Handling OrderStatusChangedEvent: Order {} status changed from {} to {}", 
                event.getOrderNumber(), event.getOldStatus(), event.getNewStatus());
        
        // Here you can add business logic like:
        // - Send status update notifications
        // - Update inventory if cancelled
        // - Trigger shipping process
        // - Send delivery notifications
    }
}

