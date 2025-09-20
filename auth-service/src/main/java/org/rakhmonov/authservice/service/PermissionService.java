package org.rakhmonov.authservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.dto.request.PermissionRequest;
import org.rakhmonov.authservice.dto.response.PermissionResponse;
import org.rakhmonov.authservice.entity.Permission;
import org.rakhmonov.authservice.repo.PermissionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    @Transactional
    public PermissionResponse createPermission(PermissionRequest permissionRequest) {
        Permission permission = Permission.builder()
                .name(permissionRequest.getName())
                .description(permissionRequest.getDescription())
                .resource(permissionRequest.getResource())
                .action(permissionRequest.getAction())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        Permission saved = permissionRepository.save(permission);
        return PermissionResponse.fromEntity(saved);
    }

    @Transactional
    public PermissionResponse updatePermission(Long id, PermissionRequest permissionRequest) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        
        permission.setName(permissionRequest.getName());
        permission.setDescription(permissionRequest.getDescription());
        permission.setResource(permissionRequest.getResource());
        permission.setAction(permissionRequest.getAction());
        permission.setUpdatedAt(LocalDateTime.now());
        
        Permission saved = permissionRepository.save(permission);
        return PermissionResponse.fromEntity(saved);
    }

    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        
        // Soft delete
        permission.setIsDeleted(true);
        permission.setUpdatedAt(LocalDateTime.now());
        
        permissionRepository.save(permission);
    }

    public PermissionResponse getPermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        return PermissionResponse.fromEntity(permission);
    }

    public PermissionResponse getPermissionByName(String name) {
        Permission permission = permissionRepository.findByNameAndIsDeletedFalse(name)
                .orElseThrow(() -> new RuntimeException("Permission not found with name: " + name));
        return PermissionResponse.fromEntity(permission);
    }

    public List<PermissionResponse> getPermissionsByResource(String resource) {
        return permissionRepository.findByResourceAndIsDeletedFalse(resource)
                .stream()
                .map(PermissionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PermissionResponse> getPermissionsByAction(String action) {
        return permissionRepository.findByActionAndIsDeletedFalse(action)
                .stream()
                .map(PermissionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(PermissionResponse::fromEntity)
                .toList();
    }

    public List<PermissionResponse> getActivePermissions() {
        return permissionRepository.findByIsDeletedFalse()
                .stream()
                .map(PermissionResponse::fromEntity)
                .toList();
    }
}
