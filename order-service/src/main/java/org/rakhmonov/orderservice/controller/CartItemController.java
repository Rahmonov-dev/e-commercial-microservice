package org.rakhmonov.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.orderservice.entity.CartItem;
import org.rakhmonov.orderservice.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    // TODO: Implement cart item endpoints
    @PostMapping
    public ResponseEntity<CartItem> addItemToCart(@RequestBody CartItem cartItem) {
        // TODO: Implement add item to cart endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItem> getCartItemById(@PathVariable Long id) {
        // TODO: Implement get cart item by ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<CartItem>> getCartItemsByCartId(@PathVariable Long cartId) {
        // TODO: Implement get cart items by cart ID endpoint
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<CartItem> getCartItemByCartAndProduct(@PathVariable Long cartId, @PathVariable Long productId) {
        // TODO: Implement get cart item by cart and product endpoint
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<CartItem> updateCartItemQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        // TODO: Implement update cart item quantity endpoint
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long id) {
        // TODO: Implement remove item from cart endpoint
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<Void> removeItemFromCartByProduct(@PathVariable Long cartId, @PathVariable Long productId) {
        // TODO: Implement remove item from cart by product endpoint
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cart/{cartId}/clear")
    public ResponseEntity<Void> clearCartItems(@PathVariable Long cartId) {
        // TODO: Implement clear cart items endpoint
        return ResponseEntity.ok().build();
    }
}


