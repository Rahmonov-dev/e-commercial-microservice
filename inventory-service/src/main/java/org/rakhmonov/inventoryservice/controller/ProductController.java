package org.rakhmonov.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.ProductRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController - Handles product management operations
 * 
 * Two types of users can create and update products:
 * 1. SELLER (Third-party seller) - Creates/updates products for dropshipping
 * 2. INVENTORY_MANAGER - Creates/updates warehouse products
 * 
 * All users can view products, but only INVENTORY_MANAGER can delete products.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse product = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse product = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String q) {
        List<ProductResponse> products = productService.searchProducts(q);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable Long id) {
        ProductResponse product = productService.activateProduct(id);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable Long id) {
        ProductResponse product = productService.deactivateProduct(id);
        return ResponseEntity.ok(product);
    }
}
