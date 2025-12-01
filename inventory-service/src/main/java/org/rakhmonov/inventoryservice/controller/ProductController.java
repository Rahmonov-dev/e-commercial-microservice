package org.rakhmonov.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.ProductRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.service.ProductService;
import org.rakhmonov.inventoryservice.util.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(@Valid @ModelAttribute ProductRequest productRequest) {
        String currentUser = JwtUtils.getCurrentUserPhoneNumber();
        String currentRole = JwtUtils.getCurrentUserRole();
        System.out.println("Creating product by user: " + currentUser + " with role: " + currentRole);
        
        ProductResponse product = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping(value = "/{id}" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @ModelAttribute ProductRequest productRequest) {
        String currentUser = JwtUtils.getCurrentUserPhoneNumber();
        String currentRole = JwtUtils.getCurrentUserRole();
        
        System.out.println("Updating product by user: " + currentUser + " with role: " + currentRole);
        
        ProductResponse product = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20, sort = "createdAt")
            org.springframework.data.domain.Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable) {
        Page<ProductResponse> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<ProductResponse>> getProductsBySellerId(
            @PathVariable Long sellerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        Long currentUserId = JwtUtils.getCurrentUserId();
        String currentRole = JwtUtils.getCurrentUserRole();
        
        if (currentUserId == null || (!currentUserId.equals(sellerId) &&
            !(currentRole != null && (currentRole.equals("ROLE_ADMIN") || currentRole.equals("ADMIN") || 
                                      currentRole.equals("ROLE_SUPER_ADMIN") || currentRole.equals("SUPER_ADMIN"))))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Page<ProductResponse> products = productService.getProductsBySellerId(sellerId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/my-products")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        Long currentUserId = JwtUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Page<ProductResponse> products = productService.getProductsBySellerId(currentUserId, pageable);
        return ResponseEntity.ok(products);
    }
}