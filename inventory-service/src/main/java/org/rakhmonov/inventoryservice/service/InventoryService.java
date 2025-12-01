package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.InventoryRequest;
import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.entity.Inventory;
import org.rakhmonov.inventoryservice.entity.Product;
import org.rakhmonov.inventoryservice.repo.InventoryRepository;
import org.rakhmonov.inventoryservice.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public Inventory createInventory(InventoryRequest inventoryRequest) {
        Product product = productRepository.findById(inventoryRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return inventoryRepository.save(InventoryRequest.toEntity(inventoryRequest, product));
    }

    public Inventory updateInventory(Long id, InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        Optional.ofNullable(inventoryRequest.getCurrentStock())
                .ifPresent(inventory::setCurrentStock);
        Optional.ofNullable(inventoryRequest.getReorderPoint())
                .ifPresent(inventory::setReorderPoint);
        Optional.ofNullable(inventoryRequest.getUnitCost())
                .ifPresent(inventory::setUnitCost);
        return inventoryRepository.save(inventory);
    }

    public Inventory getInventory(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(InventoryResponse::toResponse)
                .toList();
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    public List<InventoryResponse> filterInventory(String status, Long warehouseId, Long productId) {
        List<InventoryResponse> inventoryResponses = inventoryRepository.filterInventory(warehouseId, productId);
        return inventoryResponses.stream()
                .filter(inventoryResponse -> inventoryResponse
                        .getStockStatus().equals(status))
                .collect(Collectors.toList());
    }


    public Inventory decreaseStock(Long productId, Integer quantity, Long warehouseId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        Inventory inventory;
        if (warehouseId != null) {
            inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId + " and warehouse ID: " + warehouseId));
        } else {
            // Get first available inventory for the product
            List<Inventory> inventories = inventoryRepository.findByProduct(product);
            if (inventories.isEmpty()) {
                throw new RuntimeException("No inventory found for product ID: " + productId);
            }
            inventory = inventories.get(0);
        }
        
        // Check if enough stock is available
        if (inventory.getCurrentStock() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + inventory.getCurrentStock() + ", Requested: " + quantity);
        }
        
        // Decrease stock
        inventory.setCurrentStock(inventory.getCurrentStock() - quantity);
        return inventoryRepository.save(inventory);
    }


    public List<Inventory> decreaseStockForOrder(java.util.Map<Long, Integer> productQuantities) {
        return productQuantities.entrySet().stream()
                .map(entry -> decreaseStock(entry.getKey(), entry.getValue(), null))
                .collect(Collectors.toList());
    }


    public Inventory increaseStock(Long productId, Integer quantity, Long warehouseId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        Inventory inventory;
        if (warehouseId != null) {
            inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId + " and warehouse ID: " + warehouseId));
        } else {
            // Get first available inventory for the product
            List<Inventory> inventories = inventoryRepository.findByProduct(product);
            if (inventories.isEmpty()) {
                throw new RuntimeException("No inventory found for product ID: " + productId);
            }
            inventory = inventories.get(0);
        }
        
        // Increase stock
        inventory.setCurrentStock(inventory.getCurrentStock() + quantity);
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> increaseStockForOrder(java.util.Map<Long, Integer> productQuantities) {
        return productQuantities.entrySet().stream()
                .map(entry -> increaseStock(entry.getKey(), entry.getValue(), null))
                .collect(Collectors.toList());
    }

}
