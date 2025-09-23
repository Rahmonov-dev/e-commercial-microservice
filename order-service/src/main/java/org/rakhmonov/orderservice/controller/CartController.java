package org.rakhmonov.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.dto.request.CartRequest;
import org.rakhmonov.orderservice.dto.response.CartItemResponse;
import org.rakhmonov.orderservice.dto.response.CartResponse;
import org.rakhmonov.orderservice.service.CartItemService;
import org.rakhmonov.orderservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestParam Long userId) {
        CartResponse cart = cartService.createCart(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable Long id) {
        CartResponse cart = cartService.getCartById(id);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable Long userId) {
        CartResponse cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/user/{userId}/get-or-create")
    public ResponseEntity<CartResponse> getOrCreateCart(@PathVariable Long userId) {
        CartResponse cart = cartService.getOrCreateCart(userId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        List<CartResponse> carts = cartService.getAllCarts();
        return ResponseEntity.ok(carts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // Cart Item endpoints
    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItemToCart(@Valid @RequestBody CartRequest cartRequest) {
        CartItemResponse cartItem = cartItemService.addItemToCart(cartRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<CartItemResponse> getCartItemById(@PathVariable Long id) {
        CartItemResponse cartItem = cartItemService.getCartItemById(id);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/{cartId}/items")
    public ResponseEntity<List<CartItemResponse>> getCartItemsByCartId(@PathVariable Long cartId) {
        List<CartItemResponse> cartItems = cartItemService.getCartItemsByCartId(cartId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/user/{userId}/items")
    public ResponseEntity<List<CartItemResponse>> getCartItemsByUserId(@PathVariable Long userId) {
        CartResponse cart = cartService.getCartByUserId(userId);
        List<CartItemResponse> cartItems = cartItemService.getCartItemsByCartId(cart.getId());
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/{cartId}/items/product/{productId}")
    public ResponseEntity<CartItemResponse> getCartItemByCartAndProduct(@PathVariable Long cartId, @PathVariable Long productId) {
        CartItemResponse cartItem = cartItemService.getCartItemByCartAndProduct(cartId, productId);
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/items/{id}/quantity")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        CartItemResponse cartItem = cartItemService.updateCartItemQuantity(id, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long id) {
        cartItemService.removeItemFromCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items/product/{productId}")
    public ResponseEntity<Void> removeItemFromCartByProduct(@PathVariable Long cartId, @PathVariable Long productId) {
        cartItemService.removeItemFromCartByProduct(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items/clear")
    public ResponseEntity<Void> clearCartItems(@PathVariable Long cartId) {
        cartItemService.clearCartItems(cartId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/items/clear")
    public ResponseEntity<Void> clearCartItemsByUserId(@PathVariable Long userId) {
        CartResponse cart = cartService.getCartByUserId(userId);
        cartItemService.clearCartItems(cart.getId());
        return ResponseEntity.noContent().build();
    }
}
