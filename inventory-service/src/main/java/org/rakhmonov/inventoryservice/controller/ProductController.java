package org.rakhmonov.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.service.ProductService;
import org.springframework.web.bind.annotation.*;

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
}
