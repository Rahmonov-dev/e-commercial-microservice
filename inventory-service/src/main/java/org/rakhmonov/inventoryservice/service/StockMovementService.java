package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.inventoryservice.dto.request.StockMovementRequest;
import org.rakhmonov.inventoryservice.dto.response.StockMovementResponse;
import org.rakhmonov.inventoryservice.entity.*;
import org.rakhmonov.inventoryservice.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public StockMovementResponse createStockMovement(StockMovementRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        // Warehouse ID is validated but not fetched since it's in a different service
        Long warehouseId = request.getWarehouseId();

        // Get current stock level for the product
        Integer currentStock = getCurrentStockLevel(product, warehouseId);
        Integer newStockLevel = calculateNewStockLevel(currentStock, request.getQuantity(), request.getMovementType());

        StockMovement stockMovement = StockMovementRequest.toEntity(request, product);
        stockMovement.setStockBefore(currentStock);
        stockMovement.setStockAfter(newStockLevel);

        StockMovement savedMovement = stockMovementRepository.save(stockMovement);

        // Update inventory if warehouse is specified
        if (warehouseId != null) {
            updateInventoryStock(product, warehouseId, newStockLevel);
        }

        log.info("Stock movement created: {} {} units for product {} ({} -> {})", 
                request.getMovementType(), request.getQuantity(), product.getName(), currentStock, newStockLevel);

        return StockMovementResponse.toResponse(savedMovement);
    }

    public StockMovementResponse getStockMovement(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock movement not found with id: " + id));
        return StockMovementResponse.toResponse(movement);
    }

    public List<StockMovementResponse> getAllStockMovements() {
        return stockMovementRepository.findAll()
                .stream()
                .map(StockMovementResponse::toResponse)
                .toList();
    }

    public List<StockMovementResponse> getStockMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(StockMovementResponse::toResponse)
                .toList();
    }

    public List<StockMovementResponse> getStockMovementsByWarehouse(Long warehouseId) {
        return stockMovementRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(StockMovementResponse::toResponse)
                .toList();
    }

    public List<StockMovementResponse> getStockMovementsByType(StockMovement.MovementType movementType) {
        return stockMovementRepository.findByMovementType(movementType)
                .stream()
                .map(StockMovementResponse::toResponse)
                .toList();
    }

    public List<StockMovementResponse> getStockMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByCreatedAtBetween(startDate, endDate)
                .stream()
                .map(StockMovementResponse::toResponse)
                .toList();
    }

    public List<StockMovementResponse> getStockMovementsByReference(String referenceNumber) {
        return stockMovementRepository.findByReferenceNumber(referenceNumber)
                .stream()
                .map(StockMovementResponse::toResponse)
                .toList();
    }

    public List<StockMovementResponse> getStockMovementsByReferenceType(String referenceType) {
        return stockMovementRepository.findByReferenceType(referenceType)
                .stream()
                .map(StockMovementResponse::toResponse)
                .toList();
    }

    public List<StockMovementResponse> getStockMovementsByCreatedBy(String createdBy) {
        return stockMovementRepository.findByCreatedBy(createdBy)
                .stream()
                .map(StockMovementResponse::toResponse)
                .toList();
    }

    @Transactional
    public void deleteStockMovement(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock movement not found with id: " + id));
        
        // Reverse the stock movement
        reverseStockMovement(movement);
        
        stockMovementRepository.delete(movement);
        log.info("Stock movement deleted and reversed: {}", id);
    }

    // Helper methods
    private Integer getCurrentStockLevel(Product product, Long warehouseId) {
        if (warehouseId != null) {
            // Get stock from specific warehouse
            return inventoryRepository.findByProductIdAndWarehouseId(product.getId(), warehouseId)
                    .map(Inventory::getCurrentStock)
                    .orElse(0);
        } else {
            // Get total stock across all warehouses
            return inventoryRepository.findByProduct(product)
                    .stream()
                    .mapToInt(Inventory::getCurrentStock)
                    .sum();
        }
    }

    private Integer calculateNewStockLevel(Integer currentStock, Integer quantity, StockMovement.MovementType movementType) {
        if (movementType == StockMovement.MovementType.IN || 
            movementType == StockMovement.MovementType.TRANSFER_IN ||
            movementType == StockMovement.MovementType.RELEASED) {
            return currentStock + quantity;
        } else {
            return Math.max(0, currentStock - quantity); // Prevent negative stock
        }
    }

    private void updateInventoryStock(Product product, Long warehouseId, Integer newStockLevel) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductIdAndWarehouseId(product.getId(), warehouseId);
        
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setCurrentStock(newStockLevel);
            inventoryRepository.save(inventory);
        } else {
            // Create new inventory record if it doesn't exist
            Inventory newInventory = Inventory.builder()
                    .product(product)
                    .warehouseId(warehouseId)
                    .currentStock(newStockLevel)
                    .reorderPoint(10) // Default reorder point
                    .build();
            inventoryRepository.save(newInventory);
        }
    }

    private void reverseStockMovement(StockMovement movement) {
        Product product = movement.getProduct();
        Long warehouseId = movement.getWarehouseId();
        
        if (warehouseId != null) {
            Integer currentStock = getCurrentStockLevel(product, warehouseId);
            Integer reversedStock = calculateNewStockLevel(currentStock, movement.getQuantity(), 
                    getReversedMovementType(movement.getMovementType()));
            updateInventoryStock(product, warehouseId, reversedStock);
        }
    }

    private StockMovement.MovementType getReversedMovementType(StockMovement.MovementType originalType) {
        return switch (originalType) {
            case IN -> StockMovement.MovementType.OUT;
            case OUT -> StockMovement.MovementType.IN;
            case TRANSFER_IN -> StockMovement.MovementType.TRANSFER_OUT;
            case TRANSFER_OUT -> StockMovement.MovementType.TRANSFER_IN;
            case RESERVED -> StockMovement.MovementType.RELEASED;
            case RELEASED -> StockMovement.MovementType.RESERVED;
            case ADJUSTMENT -> StockMovement.MovementType.ADJUSTMENT;
        };
    }
}
