package org.rakhmonov.orderservice.dto.response;

import org.rakhmonov.orderservice.entity.WishlistItem;

public record WishlistItemResponse(Long id,
                                   Long productId,
                                   String productName,
                                   String productDescription,
                                   String productImageUrl,
                                   Double productPrice) {
    public static WishlistItemResponse fromWishlistItem(WishlistItem wishlistItem) {
        if (wishlistItem == null) {
            return null;
        }
        return new WishlistItemResponse(
                wishlistItem.getId(),
                wishlistItem.getProductId(),
                wishlistItem.getProductName(),
                wishlistItem.getProductDescription(),
                wishlistItem.getProductImageUrl(),
                wishlistItem.getProductPrice()
        );
    }
}
