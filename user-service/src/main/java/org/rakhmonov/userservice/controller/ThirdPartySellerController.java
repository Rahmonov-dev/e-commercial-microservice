package org.rakhmonov.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.userservice.dto.request.ThirdPartySellerRequest;
import org.rakhmonov.userservice.dto.response.ThirdPartySellerResponse;
import org.rakhmonov.userservice.service.ThirdPartySellerService;
import org.rakhmonov.userservice.util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/third-party-sellers")
@RequiredArgsConstructor
@Tag(name = "Third Party Sellers", description = "Third party seller management endpoints")
@CrossOrigin(origins = "*")
public class ThirdPartySellerController {

    private final ThirdPartySellerService sellerService;

    @PostMapping
    @Operation(summary = "Register as third-party seller", description = "Register current user as a third-party seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Seller registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "User already registered as seller")
    })
    public ResponseEntity<ThirdPartySellerResponse> registerSeller(
            @Parameter(description = "Seller registration details", required = true)
            @Valid @RequestBody ThirdPartySellerRequest request,
            HttpServletRequest httpRequest
    ) {
        // Get token from header or SecurityContext
        String token = extractTokenFromRequest(httpRequest);
        
        ThirdPartySellerResponse response = sellerService.createSeller(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        // First try to get from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // Fallback: try to get from SecurityContext (if available)
        // Note: We can't get the raw token string from JwtAuthenticationToken
        // The token is already validated by Spring Security, but we need the raw token
        // for RestTemplate calls, so we rely on the Authorization header
        return null;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's seller profile", description = "Get seller profile for currently authenticated user")
    public ResponseEntity<ThirdPartySellerResponse> getMySellerProfile() {
        Long userId = JwtUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        ThirdPartySellerResponse response = sellerService.getSellerByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get seller by ID", description = "Get seller details by seller ID")
    public ResponseEntity<ThirdPartySellerResponse> getSellerById(
            @Parameter(description = "Seller ID", required = true)
            @PathVariable Long id
    ) {
        ThirdPartySellerResponse response = sellerService.getSellerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all sellers", description = "Get list of all registered sellers")
    public ResponseEntity<List<ThirdPartySellerResponse>> getAllSellers() {
        List<ThirdPartySellerResponse> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update seller", description = "Update seller information")
    public ResponseEntity<ThirdPartySellerResponse> updateSeller(
            @Parameter(description = "Seller ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated seller information", required = true)
            @Valid @RequestBody ThirdPartySellerRequest request
    ) {
        ThirdPartySellerResponse response = sellerService.updateSeller(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete seller", description = "Delete seller by ID")
    public ResponseEntity<Void> deleteSeller(
            @Parameter(description = "Seller ID", required = true)
            @PathVariable Long id
    ) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
