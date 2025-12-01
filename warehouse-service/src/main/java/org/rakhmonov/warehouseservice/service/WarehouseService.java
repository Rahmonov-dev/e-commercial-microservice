package org.rakhmonov.warehouseservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.warehouseservice.dto.request.WarehouseRequest;
import org.rakhmonov.warehouseservice.dto.response.WarehouseResponse;
import org.rakhmonov.warehouseservice.entity.Warehouse;
import org.rakhmonov.warehouseservice.repo.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {
    
    private final WarehouseRepository warehouseRepository;
    
    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        log.info("Creating warehouse: {}", request.getName());
        
        // Check if warehouse name already exists
        if (warehouseRepository.existsByName(request.getName())) {
            throw new RuntimeException("Warehouse with this name already exists");
        }
        
        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .capacity(request.getCapacity())
                .currentOccupancy(0)
                .status(Warehouse.WarehouseStatus.ACTIVE)
                .isDeleted(false)
                .build();
        
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        log.info("Created warehouse with ID: {}", savedWarehouse.getId());
        
        return WarehouseResponse.fromEntity(savedWarehouse);
    }
    
    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        log.info("Updating warehouse: {}", id);
        
        Warehouse warehouse = warehouseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!warehouse.getName().equals(request.getName()) && warehouseRepository.existsByName(request.getName())) {
            throw new RuntimeException("Warehouse with this name already exists");
        }
        
        // Update fields
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setCity(request.getCity());
        warehouse.setState(request.getState());
        warehouse.setCountry(request.getCountry());
        warehouse.setPostalCode(request.getPostalCode());
        warehouse.setPhoneNumber(request.getPhoneNumber());
        warehouse.setEmail(request.getEmail());
        
        // Update capacity only if new capacity is not less than current occupancy
        if (request.getCapacity() != null) {
            if (warehouse.getCurrentOccupancy() != null && request.getCapacity() < warehouse.getCurrentOccupancy()) {
                throw new RuntimeException("New capacity cannot be less than current occupancy");
            }
            warehouse.setCapacity(request.getCapacity());
        }
        
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        log.info("Updated warehouse: {}", updatedWarehouse.getId());
        
        return WarehouseResponse.fromEntity(updatedWarehouse);
    }
    
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        return WarehouseResponse.fromEntity(warehouse);
    }
    
    public Page<WarehouseResponse> getAllWarehouses(Pageable pageable) {
        return warehouseRepository.findByIsDeletedFalse(pageable)
                .map(WarehouseResponse::fromEntity);
    }
    
    public Page<WarehouseResponse> getWarehousesByStatus(Warehouse.WarehouseStatus status, Pageable pageable) {
        return warehouseRepository.findByStatusAndIsDeletedFalse(status, pageable)
                .map(WarehouseResponse::fromEntity);
    }
    
    public Page<WarehouseResponse> getWarehousesByCity(String city, Pageable pageable) {
        return warehouseRepository.findByCityAndIsDeletedFalse(city, pageable)
                .map(WarehouseResponse::fromEntity);
    }
    
    public Page<WarehouseResponse> getWarehousesByCountry(String country, Pageable pageable) {
        return warehouseRepository.findByCountryAndIsDeletedFalse(country, pageable)
                .map(WarehouseResponse::fromEntity);
    }
    
    public Page<WarehouseResponse> searchWarehousesByName(String name, Pageable pageable) {
        return warehouseRepository.searchByName(name, pageable)
                .map(WarehouseResponse::fromEntity);
    }
    
    public Page<WarehouseResponse> getWarehousesWithAvailableCapacity(Integer minAvailable, Pageable pageable) {
        return warehouseRepository.findWarehousesWithAvailableCapacity(minAvailable, pageable)
                .map(WarehouseResponse::fromEntity);
    }
    
    @Transactional
    public WarehouseResponse activateWarehouse(Long id) {
        log.info("Activating warehouse: {}", id);
        
        Warehouse warehouse = warehouseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        warehouse.setStatus(Warehouse.WarehouseStatus.ACTIVE);
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        
        log.info("Activated warehouse: {}", updatedWarehouse.getId());
        return WarehouseResponse.fromEntity(updatedWarehouse);
    }
    
    @Transactional
    public WarehouseResponse deactivateWarehouse(Long id) {
        log.info("Deactivating warehouse: {}", id);
        
        Warehouse warehouse = warehouseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        warehouse.setStatus(Warehouse.WarehouseStatus.INACTIVE);
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        
        log.info("Deactivated warehouse: {}", updatedWarehouse.getId());
        return WarehouseResponse.fromEntity(updatedWarehouse);
    }
    
    @Transactional
    public WarehouseResponse setMaintenanceMode(Long id) {
        log.info("Setting warehouse to maintenance mode: {}", id);
        
        Warehouse warehouse = warehouseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        warehouse.setStatus(Warehouse.WarehouseStatus.MAINTENANCE);
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        
        log.info("Warehouse set to maintenance mode: {}", updatedWarehouse.getId());
        return WarehouseResponse.fromEntity(updatedWarehouse);
    }
    
    @Transactional
    public WarehouseResponse updateOccupancy(Long id, Integer occupancy) {
        log.info("Updating occupancy for warehouse: {} to {}", id, occupancy);
        
        Warehouse warehouse = warehouseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        if (occupancy < 0) {
            throw new RuntimeException("Occupancy cannot be negative");
        }
        
        if (warehouse.getCapacity() != null && occupancy > warehouse.getCapacity()) {
            throw new RuntimeException("Occupancy cannot exceed capacity");
        }
        
        warehouse.setCurrentOccupancy(occupancy);
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        
        log.info("Updated occupancy for warehouse: {} to {}", updatedWarehouse.getId(), occupancy);
        return WarehouseResponse.fromEntity(updatedWarehouse);
    }
    
    @Transactional
    public void deleteWarehouse(Long id) {
        log.info("Deleting warehouse: {}", id);
        
        Warehouse warehouse = warehouseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        // Soft delete
        warehouse.setIsDeleted(true);
        warehouseRepository.save(warehouse);
        
        log.info("Deleted warehouse: {}", id);
    }
    
    public long countWarehousesByStatus(Warehouse.WarehouseStatus status) {
        return warehouseRepository.countByStatus(status);
    }
}


