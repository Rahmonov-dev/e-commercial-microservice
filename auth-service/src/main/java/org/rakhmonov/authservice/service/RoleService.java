package org.rakhmonov.authservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.dto.request.RoleRequest;
import org.rakhmonov.authservice.dto.response.RoleResponse;
import org.rakhmonov.authservice.entity.Role;
import org.rakhmonov.authservice.entity.Permission;
import org.rakhmonov.authservice.repo.RoleRepository;
import org.rakhmonov.authservice.repo.PermissionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public RoleResponse createRole(RoleRequest roleRequest) {
        Role role = Role.builder()
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        
        // Save the role first to get the ID
        Role saved = roleRepository.save(role);
        
        if (roleRequest.getPermissionIds() != null && !roleRequest.getPermissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(roleRequest.getPermissionIds());
            if (permissions.size() != roleRequest.getPermissionIds().size()) {
                throw new RuntimeException("Some permissions not found");
            }
            saved.setPermissions(permissions);
            saved = roleRepository.save(saved);
        }
        
        return RoleResponse.fromEntity(saved);
    }

    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest roleRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setUpdatedAt(LocalDateTime.now());
        
        // Update permissions if provided
        if (roleRequest.getPermissionIds() != null) {
            if (roleRequest.getPermissionIds().isEmpty()) {
                // Clear all permissions
                role.setPermissions(new ArrayList<>());
            } else {
                // Update with new permissions
                List<Permission> permissions = permissionRepository.findAllById(roleRequest.getPermissionIds());
                if (permissions.size() != roleRequest.getPermissionIds().size()) {
                    throw new RuntimeException("Some permissions not found");
                }
                role.setPermissions(permissions);
            }
        }
        
        Role saved = roleRepository.save(role);
        return RoleResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        // Soft delete
        role.setIsDeleted(true);
        role.setUpdatedAt(LocalDateTime.now());
        
        roleRepository.save(role);
    }

    public RoleResponse getRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return RoleResponse.fromEntity(role);
    }

    public RoleResponse getRoleByName(String name) {
        Role role = roleRepository.findByNameAndIsDeletedFalse(name)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
        return RoleResponse.fromEntity(role);
    }

    @Transactional
    public RoleResponse assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findByIdAndIsDeletedFalse(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new RuntimeException("Some permissions not found");
        }
        
        role.setPermissions(permissions);
        
        Role saved = roleRepository.save(role);
        return RoleResponse.fromEntity(saved);
    }

    @Transactional
    public RoleResponse removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findByIdAndIsDeletedFalse(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        List<Permission> currentPermissions = role.getPermissions();
        List<Permission> permissionsToRemove = permissionRepository.findAllById(permissionIds);
        
        currentPermissions.removeAll(permissionsToRemove);
        role.setPermissions(currentPermissions);
        
        Role saved = roleRepository.save(role);
        return RoleResponse.fromEntity(saved);
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository
                .findAll()
                .stream()
                .map(RoleResponse::fromEntity)
                .toList();
    }

    public List<RoleResponse> getActiveRoles() {
        return roleRepository
                .findByIsDeletedFalse()
                .stream()
                .map(RoleResponse::fromEntity)
                .toList();
    }
}
