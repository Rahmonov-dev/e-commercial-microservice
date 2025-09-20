package org.rakhmonov.inventoryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductVariantNotFoundException extends RuntimeException {
    public ProductVariantNotFoundException(Long variantId, Long productId) {
        super("Product variant not found with ID: " + variantId + " for product ID: " + productId);
    }
    
    public ProductVariantNotFoundException(Long variantId) {
        super("Product variant not found with ID: " + variantId);
    }
}




