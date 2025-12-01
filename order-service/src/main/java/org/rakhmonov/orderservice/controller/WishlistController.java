package org.rakhmonov.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.orderservice.dto.request.WishlistItemRequest;
import org.rakhmonov.orderservice.dto.request.WishlistRequest;
import org.rakhmonov.orderservice.dto.response.WishlistResponse;
import org.rakhmonov.orderservice.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistResponse> createWishlist(@Valid @RequestBody WishlistRequest wishlistRequest) {
        WishlistResponse wishlist = wishlistService.createWishlist(wishlistRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlist);
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getAllWishlists() {
        List<WishlistResponse> wishlists = wishlistService.getAllWishlists();
        return ResponseEntity.ok(wishlists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishlistResponse> getWishlistById(@PathVariable Long id) {
        WishlistResponse wishlist = wishlistService.getWishlistById(id);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Long id) {
        wishlistService.deleteWishlist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<WishlistResponse> getWishlistsByUserId(@PathVariable Long userId) {
        WishlistResponse wishlists = wishlistService.getWishlistByUserId(userId);
        return ResponseEntity.ok(wishlists);
    }

    @PostMapping("/{wishlistId}/items")
    public ResponseEntity<WishlistResponse> addItemToWishlist(@PathVariable Long wishlistId, @Valid @RequestBody WishlistItemRequest wishlistItemRequest) {
        WishlistResponse wishlistItem = wishlistService.addItemToWishlist(wishlistId, wishlistItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistItem);
    }

    @DeleteMapping("/{wishlistId}/items/{itemId}")
    public ResponseEntity<Void> deleteItemFromWishlist(@PathVariable Long wishlistId, @PathVariable Long itemId) {
        wishlistService.deleteItemFromWishlist(wishlistId, itemId);
        return ResponseEntity.noContent().build();
    }

}
