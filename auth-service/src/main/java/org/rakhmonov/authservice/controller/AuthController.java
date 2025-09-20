package org.rakhmonov.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.dto.request.LoginRequest;
import org.rakhmonov.authservice.dto.request.RefreshTokenRequest;
import org.rakhmonov.authservice.dto.request.RegisterRequest;
import org.rakhmonov.authservice.dto.response.AuthResponse;
import org.rakhmonov.authservice.dto.response.LoginResponse;
import org.rakhmonov.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/test")
    @Operation(
        summary = "Test endpoint",
        description = "Simple test endpoint to verify access"
    )
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth controller is accessible!");
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with default CUSTOMER role"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or user already exists"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<LoginResponse> register(
        @Parameter(description = "User registration details", required = true)
        @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticates user credentials and returns JWT tokens"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        )
    })
    public ResponseEntity<AuthResponse> login(
        @Parameter(description = "User login credentials", required = true)
        @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    @Operation(
        summary = "Refresh access token",
        description = "Generates new access token using refresh token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired refresh token"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        )
    })
    public ResponseEntity<AuthResponse> refreshToken(
        @Parameter(description = "Refresh token request", required = true)
        @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(
        summary = "User logout",
        description = "Invalidates user's refresh token and logs out the user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User logged out successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized access"
        )
    })
    public ResponseEntity<String> logout(
        @Parameter(description = "Bearer token for authentication", required = true)
        @RequestHeader("Authorization") String header,
        @Parameter(description = "Refresh token to invalidate", required = true)
        @RequestBody RefreshTokenRequest request
    ) {
        String accesToken=null;
        if (header!=null && header.startsWith("Bearer ")) {
            accesToken=header.substring(7);
        }
        authService.logout(accesToken,request);
        return ResponseEntity.ok("User logged out successfully");
    }
}
