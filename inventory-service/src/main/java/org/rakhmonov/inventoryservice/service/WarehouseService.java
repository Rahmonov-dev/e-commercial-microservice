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
    private final WarehouseRepository warehousRepository;

    public WarehouseResponse create(WarehouseRequest request) {
        Warehouse warehouse = WarehouseRequest.toEntity(request);
        warehouse = warehousRepository.save(warehouse);
        return WarehouseResponse.toResponse(warehouse);
    }

    public WarehouseResponse getById(Long id) {
        return WarehouseResponse.toResponse(warehousRepository.findById(id).orElse(null));
    }

    public List<WarehouseResponse> getAll() {
        return warehousRepository.findAll().stream()
                .map(WarehouseResponse::toResponse)
                .toList();
    }

    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        WarehouseRequest.updateEntity(request, warehouse);
        Warehouse updated = warehousRepository.save(warehouse);

        return WarehouseResponse.toResponse(updated);
    }

    public void delete(Long id) {
        warehousRepository.deleteById(id);
    }

    public List<WarehouseResponse> getByStatus(String status) {
        return warehousRepository.findByStatus(Warehouse.WarehouseStatus.valueOf(status)).stream()
                .map(WarehouseResponse::toResponse)
                .toList();
    }

    public List<WarehouseResponse> getByType(String type) {
        return warehousRepository.findByType(Warehouse.WarehouseType.valueOf(type)).stream()
                .map(WarehouseResponse::toResponse)
                .toList();
    }

    public WarehouseResponse activate(Long id) {
        Warehouse warehouse = warehousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        warehouse.setStatus(Warehouse.WarehouseStatus.ACTIVE);
        return WarehouseResponse.toResponse(warehousRepository.save(warehouse));
    }

    public WarehouseResponse updateStatus(Long id, String status) {
        Warehouse warehouse = warehousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        warehouse.setStatus(Warehouse.WarehouseStatus.valueOf(status));
        return WarehouseResponse.toResponse(warehousRepository.save(warehouse));
    }

    public List<WarehouseResponse> search(String name, String city) {
        return warehousRepository.findByNameContaining(name, city).stream()
                .map(WarehouseResponse::toResponse)
                .toList();
    }

    public List<InventoryResponse> getInventory(Long id) {
        return warehousRepository.getInventory(id).stream()
                .map(InventoryResponse::toResponse)
                .toList();
    }
}
