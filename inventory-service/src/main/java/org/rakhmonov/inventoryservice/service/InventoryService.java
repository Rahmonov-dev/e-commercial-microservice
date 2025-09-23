package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.InventoryRequest;
import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.entity.Inventory;
import org.rakhmonov.inventoryservice.entity.Product;
import org.rakhmonov.inventoryservice.entity.Warehouse;
import org.rakhmonov.inventoryservice.repo.InventoryRepository;
import org.rakhmonov.inventoryservice.repo.ProductRepository;
import org.rakhmonov.inventoryservice.repo.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public Inventory createInventory(InventoryRequest inventoryRequest) {
        Product product = productRepository.findById(inventoryRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(inventoryRequest.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return inventoryRepository.save(InventoryRequest.toEntity(inventoryRequest, product, warehouse));
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

}
