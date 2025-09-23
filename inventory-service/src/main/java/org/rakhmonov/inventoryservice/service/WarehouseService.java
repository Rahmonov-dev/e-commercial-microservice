package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.WarehouseRequest;
import org.rakhmonov.inventoryservice.dto.response.InventoryResponse;
import org.rakhmonov.inventoryservice.dto.response.WarehouseResponse;
import org.rakhmonov.inventoryservice.entity.Warehouse;
import org.rakhmonov.inventoryservice.repo.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseResponse create(WarehouseRequest request) {
        Warehouse warehouse = WarehouseRequest.toEntity(request);
        warehouse = warehouseRepository.save(warehouse);
        return WarehouseResponse.toResponse(warehouse);
    }

    public WarehouseResponse getById(Long id) {
        return WarehouseResponse.toResponse(warehouseRepository.findById(id).orElse(null));
    }

    public List<WarehouseResponse> getAll() {
        return warehouseRepository.findAll().stream()
                .map(WarehouseResponse::toResponse)
                .toList();
    }

    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        WarehouseRequest.updateEntity(request, warehouse);
        Warehouse updated = warehouseRepository.save(warehouse);

        return WarehouseResponse.toResponse(updated);
    }

    public void delete(Long id) {
        warehouseRepository.deleteById(id);
    }

    public List<WarehouseResponse> getByStatus(String status) {
        return warehouseRepository.findByStatus(Warehouse.WarehouseStatus.valueOf(status)).stream()
                .map(WarehouseResponse::toResponse)
                .toList();
    }

    public List<WarehouseResponse> getByType(String type) {
        return warehouseRepository.findByType(Warehouse.WarehouseType.valueOf(type)).stream()
                .map(WarehouseResponse::toResponse)
                .toList();
    }

    public WarehouseResponse activate(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        warehouse.setStatus(Warehouse.WarehouseStatus.ACTIVE);
        return WarehouseResponse.toResponse(warehouseRepository.save(warehouse));
    }

    public WarehouseResponse updateStatus(Long id, String status) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        warehouse.setStatus(Warehouse.WarehouseStatus.valueOf(status));
        return WarehouseResponse.toResponse(warehouseRepository.save(warehouse));
    }

    public List<WarehouseResponse> search(String name, String city) {
        return warehouseRepository.findByNameContaining(name, city).stream()
                .map(WarehouseResponse::toResponse)
                .toList();
    }

    public List<InventoryResponse> getInventory(Long id) {
        return warehouseRepository.getInventory(id).stream()
                .map(InventoryResponse::toResponse)
                .toList();
    }
}
