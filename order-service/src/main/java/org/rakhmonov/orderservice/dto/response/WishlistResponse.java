package org.rakhmonov.orderservice.dto.response;

import org.rakhmonov.orderservice.entity.Wishlist;

import java.util.List;
import java.util.ArrayList;

public record WishlistResponse(Long id,
                               Long userId,
                               List<WishlistItemResponse> wishlistItems)  {
    public static WishlistResponse fromWishlist(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }
        List<WishlistItemResponse> items = wishlist.getItems() != null 
            ? wishlist.getItems().stream()
                .map(WishlistItemResponse::fromWishlistItem)
                .filter(item -> item != null)
                .toList()
            : new ArrayList<>();
        
        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getUserId(),
                items
        );
    }
}
