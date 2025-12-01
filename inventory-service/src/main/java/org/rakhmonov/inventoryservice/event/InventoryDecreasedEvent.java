package org.rakhmonov.inventoryservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when inventory is successfully decreased for an order
 * This event is consumed by order-service to update order status to CONFIRMED
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDecreasedEvent {
    private String orderNumber;
    private boolean success;
    private String message; // Optional: error message if success is false
}




