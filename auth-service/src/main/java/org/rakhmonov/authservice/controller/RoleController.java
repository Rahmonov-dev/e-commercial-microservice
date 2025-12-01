package org.rakhmonov.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.dto.request.RoleRequest;
import org.rakhmonov.authservice.dto.response.RoleResponse;
import org.rakhmonov.authservice.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Role creation, update, and management operations")
public class RoleController {
    private final RoleService roleService;

    @PostMapping
//    @PreAuthorize("hasRole('SUPER_ADMIN')")

    public ResponseEntity<RoleResponse> createRole(
        @Parameter(description = "Role creation details", required = true)
        @RequestBody RoleRequest roleRequest
    ) {
        RoleResponse role = roleService.createRole(roleRequest);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoleResponse> updateRole(
        @Parameter(description = "Role ID to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "Updated role details", required = true)
        @RequestBody RoleRequest roleRequest
    ) {
        RoleResponse role = roleService.updateRole(id, roleRequest);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteRole(
        @Parameter(description = "Role ID to delete", required = true)
        @PathVariable Long id
    ) {
        roleService.deleteRole(id);
        return ResponseEntity.ok("Role deleted successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoleResponse> getRole(
        @Parameter(description = "Role ID to retrieve", required = true)
        @PathVariable Long id
    ) {
        RoleResponse role = roleService.getRole(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<RoleResponse>> getActiveRoles() {
        List<RoleResponse> roles = roleService.getActiveRoles();
        return ResponseEntity.ok(roles);
    }
}
