package org.rakhmonov.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.dto.request.PermissionRequest;
import org.rakhmonov.authservice.dto.response.PermissionResponse;
import org.rakhmonov.authservice.entity.Permission;
import org.rakhmonov.authservice.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "Permission creation, update, and management operations")
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping("/test")
    @Operation(
        summary = "Test endpoint",
        description = "Simple test endpoint to verify access"
    )
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Permission controller is accessible!");
    }

    @PostMapping
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Create new permission",
        description = "Creates a new permission with specified resource and action (Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Permission created successfully",
            content = @Content(schema = @Schema(implementation = PermissionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Super Admin privileges required"
        )
    })
    public ResponseEntity<PermissionResponse> createPermission(
        @Parameter(description = "Permission creation details", required = true)
        @RequestBody PermissionRequest permissionRequest
    ) {
        PermissionResponse permission = permissionService.createPermission(permissionRequest);
        return ResponseEntity.ok(permission);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Update existing permission",
        description = "Updates an existing permission with new details (Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Permission updated successfully",
            content = @Content(schema = @Schema(implementation = PermissionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Super Admin privileges required"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Permission not found"
        )
    })
    public ResponseEntity<PermissionResponse> updatePermission(
        @Parameter(description = "Permission ID to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "Updated permission details", required = true)
        @RequestBody PermissionRequest permissionRequest
    ) {
        PermissionResponse permission = permissionService.updatePermission(id, permissionRequest);
        return ResponseEntity.ok(permission);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Delete permission",
        description = "Soft deletes a permission (Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Permission deleted successfully"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Super Admin privileges required"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Permission not found"
        )
    })
    public ResponseEntity<String> deletePermission(
        @Parameter(description = "Permission ID to delete", required = true)
        @PathVariable Long id
    ) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok("Permission deleted successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Get permission by ID",
        description = "Retrieves permission details by permission ID (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Permission found successfully",
            content = @Content(schema = @Schema(implementation = PermissionResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Permission not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<PermissionResponse> getPermission(
        @Parameter(description = "Permission ID to retrieve", required = true)
        @PathVariable Long id
    ) {
        PermissionResponse permission = permissionService.getPermission(id);
        return ResponseEntity.ok(permission);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Get all permissions",
        description = "Retrieves list of all permissions including deleted ones (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Permissions retrieved successfully",
            content = @Content(schema = @Schema(implementation = PermissionResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        List<PermissionResponse> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Get active permissions",
        description = "Retrieves list of only active (non-deleted) permissions (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Active permissions retrieved successfully",
            content = @Content(schema = @Schema(implementation = PermissionResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<List<PermissionResponse>> getActivePermissions() {
        List<PermissionResponse> permissions = permissionService.getActivePermissions();
        return ResponseEntity.ok(permissions);
    }
}
