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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth controller is accessible!");
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
        @Parameter(description = "User registration details", required = true)
        @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @Parameter(description = "User login credentials", required = true)
        @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
        @Parameter(description = "Refresh token request", required = true)
        @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
        @Parameter(description = "Bearer token for authentication", required = true)
        @RequestHeader("Authorization") String header,
        @Parameter(description = "Refresh token to invalidate", required = true)
        @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(request);
        return ResponseEntity.ok("User logged out successfully");
    }
}
