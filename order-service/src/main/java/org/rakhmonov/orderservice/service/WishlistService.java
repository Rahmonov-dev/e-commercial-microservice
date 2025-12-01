package org.rakhmonov.orderservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.orderservice.dto.request.WishlistItemRequest;
import org.rakhmonov.orderservice.dto.request.WishlistRequest;
import org.rakhmonov.orderservice.dto.response.WishlistResponse;
import org.rakhmonov.orderservice.entity.Wishlist;
import org.rakhmonov.orderservice.entity.WishlistItem;
import org.rakhmonov.orderservice.repo.WishlistItemRepository;
import org.rakhmonov.orderservice.repo.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public WishlistResponse createWishlist(@Valid WishlistRequest wishlistRequest) {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(wishlistRequest.getUserId());
        return WishlistResponse.fromWishlist(wishlistRepository.save(wishlist));
    }

    public List<WishlistResponse> getAllWishlists() {
        return wishlistRepository.findAll().stream()
                .map(WishlistResponse::fromWishlist)
                .toList();
    }

    public WishlistResponse getWishlistById(Long id) {
        return WishlistResponse.
                fromWishlist(wishlistRepository.
                        findById(id).
                        orElseThrow(() -> new RuntimeException("Wishlist not found")));
    }

    public void deleteWishlist(Long id) {
        Wishlist wishlist = wishlistRepository.
                findById(id).
                orElseThrow(() -> new RuntimeException("Wishlist not found"));
        wishlist.setIsDeleted(true);
        wishlistRepository.save(wishlist);
    }

    public WishlistResponse getWishlistByUserId(Long userId) {
        Wishlist wishlist = wishlistRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found for user ID: " + userId));
        return WishlistResponse.fromWishlist(wishlist);
    }

    public WishlistResponse addItemToWishlist(Long wishlistId, @Valid WishlistItemRequest wishlistItemRequest) {
        Wishlist wishlist = wishlistRepository.
                findById(wishlistId).
                orElseThrow(() -> new RuntimeException("Wishlist not found"));
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setProductId(wishlistItemRequest.getProductId());
        wishlistItem.setProductName(wishlistItemRequest.getProductName());
        wishlistItem.setProductDescription(wishlistItemRequest.getProductDescription());
        wishlistItem.setProductImageUrl(wishlistItemRequest.getProductImageUrl());
        wishlistItem.setProductPrice(wishlistItemRequest.getProductPrice());
        wishlist.addItem(wishlistItem);

        return WishlistResponse.fromWishlist(wishlistRepository.save(wishlist)) ;
    }

    public void deleteItemFromWishlist(Long wishlistId, Long itemId) {
        Wishlist wishlist = wishlistRepository.
                findById(wishlistId).
                orElseThrow(() -> new RuntimeException("Wishlist not found"));
        wishlist.removeItem(wishlistItemRepository.
                findById(itemId).
                orElseThrow(() -> new RuntimeException("Wishlist item not found")));
        wishlistRepository.save(wishlist);
    }
}
