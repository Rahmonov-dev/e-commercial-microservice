package org.rakhmonov.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.dto.request.UserUpdateRequest;
import org.rakhmonov.authservice.dto.request.UserRoleUpdateRequest;
import org.rakhmonov.authservice.dto.response.UserResponse;
import org.rakhmonov.authservice.entity.User;
import org.rakhmonov.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "User management and profile operations")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Get all active users",
        description = "Retrieves list of all active users (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<List<UserResponse>> getAllActiveUsers() {
        List<UserResponse> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves user details by user ID (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<UserResponse> getUserById(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long id
    ) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Get users by role",
        description = "Retrieves all users with a specific role (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<List<UserResponse>> getUsersByRole(
        @Parameter(description = "Role name to filter by", required = true)
        @PathVariable String roleName
    ) {
        List<UserResponse> users = userService.getUsersByRole(roleName);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Get users by status",
        description = "Retrieves all users with a specific status (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status value"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<List<UserResponse>> getUsersByStatus(
        @Parameter(description = "User status (ACTIVE, INACTIVE, SUSPENDED)", required = true)
        @PathVariable String status
    ) {
        User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
        List<UserResponse> users = userService.getUsersByStatus(userStatus);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/phone/{phoneNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Get user by phone number",
        description = "Retrieves user details by phone number (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<UserResponse> getUserByPhoneNumber(
        @Parameter(description = "Phone number to search for", required = true)
        @PathVariable String phoneNumber
    ) {
        UserResponse user = userService.getUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Update user profile",
        description = "Updates the authenticated user's profile information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        )
    })
    public ResponseEntity<UserResponse> updateUserProfile(
        @Parameter(description = "User profile update details", required = true)
        @RequestBody UserUpdateRequest request,
        Authentication authentication
    ) {
        String phoneNumber = authentication.getName();
        UserResponse updatedUser = userService.updateUser(request, phoneNumber);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Update user role",
        description = "Updates a user's role (Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User role updated successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
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
            description = "User not found"
        )
    })
    public ResponseEntity<UserResponse> updateUserRole(
        @Parameter(description = "User ID to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "New role assignment", required = true)
        @RequestBody UserRoleUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateUserRole(id, request.getRoleId());
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Activate user",
        description = "Activates a suspended or inactive user (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User activated successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<UserResponse> activateUser(
        @Parameter(description = "User ID to activate", required = true)
        @PathVariable Long id
    ) {
        UserResponse user = userService.activateUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Deactivate user",
        description = "Deactivates an active user (Admin/Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User deactivated successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - insufficient privileges"
        )
    })
    public ResponseEntity<UserResponse> deactivateUser(
        @Parameter(description = "User ID to deactivate", required = true)
        @PathVariable Long id
    ) {
        UserResponse user = userService.deactivateUser(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Delete user",
        description = "Soft deletes a user (Super Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Super Admin privileges required"
        )
    })
    public ResponseEntity<String> deleteUser(
        @Parameter(description = "User ID to delete", required = true)
        @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
