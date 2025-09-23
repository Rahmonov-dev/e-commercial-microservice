package org.rakhmonov.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.inventoryservice.dto.request.ProductVariantRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductVariantResponse;
import org.rakhmonov.inventoryservice.service.ProductVariantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/variants")
@RequiredArgsConstructor
@Slf4j
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @PostMapping
    public ResponseEntity<ProductVariantResponse> createProductVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantRequest request) {
        ProductVariantResponse variant = productVariantService.createProductVariant(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(variant);
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<ProductVariantResponse> updateProductVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        ProductVariantResponse variant = productVariantService.updateProductVariant(productId, variantId, request);
        return ResponseEntity.ok(variant);
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<ProductVariantResponse> getProductVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId) {
        ProductVariantResponse variant = productVariantService.getProductVariant(productId, variantId);
        return ResponseEntity.ok(variant);
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantResponse>> getProductVariants(@PathVariable Long productId) {
        List<ProductVariantResponse> variants = productVariantService.getProductVariants(productId);
        return ResponseEntity.ok(variants);
    }

    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> deleteProductVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId) {
        productVariantService.deleteProductVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{variantId}/stock")
    public ResponseEntity<ProductVariantResponse> updateStock(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        ProductVariantResponse variant = productVariantService.updateStock(productId, variantId, quantity);
        return ResponseEntity.ok(variant);
    }

    @PatchMapping("/{variantId}/reserve")
    public ResponseEntity<ProductVariantResponse> reserveStock(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        ProductVariantResponse variant = productVariantService.reserveStock(productId, variantId, quantity);
        return ResponseEntity.ok(variant);
    }

    @PatchMapping("/{variantId}/release")
    public ResponseEntity<ProductVariantResponse> releaseReservedStock(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        ProductVariantResponse variant = productVariantService.releaseReservedStock(productId, variantId, quantity);
        return ResponseEntity.ok(variant);
    }

    @PatchMapping("/{variantId}/activate")
    public ResponseEntity<ProductVariantResponse> activateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId) {
        ProductVariantResponse variant = productVariantService.activateVariant(productId, variantId);
        return ResponseEntity.ok(variant);
    }

    @PatchMapping("/{variantId}/deactivate")
    public ResponseEntity<ProductVariantResponse> deactivateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId) {
        ProductVariantResponse variant = productVariantService.deactivateVariant(productId, variantId);
        return ResponseEntity.ok(variant);
    }
}