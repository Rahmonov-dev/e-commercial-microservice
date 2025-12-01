package org.rakhmonov.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.dto.request.CartRequest;
import org.rakhmonov.orderservice.dto.response.CartItemResponse;
import org.rakhmonov.orderservice.entity.Cart;
import org.rakhmonov.orderservice.entity.CartItem;
import org.rakhmonov.orderservice.repo.CartItemRepository;
import org.rakhmonov.orderservice.repo.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Transactional
    public CartItemResponse addItemToCart(CartRequest cartRequest) {
        // Get or create cart for user
        Cart cart = cartRepository.findByUserId(cartRequest.getUserId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(cartRequest.getUserId())
                            .totalAmount(BigDecimal.ZERO)
                            .build();
                    return cartRepository.save(newCart);
                });

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), cartRequest.getProductId());
        
        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Update quantity if item exists
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartRequest.getQuantity());
            cartItem.setTotalAmount(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        } else {
            // Create new cart item
            BigDecimal itemTotal = cartRequest.getUnitPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity()));
            cartItem = CartItem.builder()
                    .cart(cart)
                    .productId(cartRequest.getProductId())
                    .productName(cartRequest.getProductName())
                    .quantity(cartRequest.getQuantity())
                    .unitPrice(cartRequest.getUnitPrice())
                    .totalAmount(itemTotal)
                    .build();
        }

        CartItem savedItem = cartItemRepository.save(cartItem);
        
        // Update cart total
        updateCartTotal(cart.getId());
        
        log.info("Item added to cart for user ID: {}, product ID: {}", cartRequest.getUserId(), cartRequest.getProductId());
        return CartItemResponse.fromEntity(savedItem);
    }

    public CartItemResponse getCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + id));
        return CartItemResponse.fromEntity(cartItem);
    }

    public List<CartItemResponse> getCartItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId)
                .stream()
                .map(CartItemResponse::fromEntity)
                .toList();
    }

    public CartItemResponse getCartItemByCartAndProduct(Long cartId, Long productId) {
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found for cart ID: " + cartId + " and product ID: " + productId));
        return CartItemResponse.fromEntity(cartItem);
    }

    @Transactional
    public CartItemResponse updateCartItemQuantity(Long id, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + id));
        
        cartItem.setQuantity(quantity);
        cartItem.setTotalAmount(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setUpdatedAt(LocalDateTime.now());
        
        CartItem updatedItem = cartItemRepository.save(cartItem);
        
        updateCartTotal(cartItem.getCart().getId());
        
        log.info("Cart item quantity updated to {} for item ID: {}", quantity, id);
        return CartItemResponse.fromEntity(updatedItem);
    }

    @Transactional
    public void removeItemFromCart(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + id));
        
        Long cartId = cartItem.getCart().getId();
        cartItemRepository.delete(cartItem);
        
        updateCartTotal(cartId);
        
        log.info("Cart item removed with ID: {}", id);
    }

    @Transactional
    public void removeItemFromCartByProduct(Long cartId, Long productId) {
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found for cart ID: " + cartId + " and product ID: " + productId));
        
        cartItemRepository.delete(cartItem);
        
        updateCartTotal(cartId);
        
        log.info("Cart item removed for cart ID: {} and product ID: {}", cartId, productId);
    }

    @Transactional
    public void clearCartItems(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
        cartItemRepository.deleteAll(cartItems);
        
        cartService.updateCartTotal(cartId, BigDecimal.ZERO);
        
        log.info("All cart items cleared for cart ID: {}", cartId);
    }

    private void updateCartTotal(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        cartService.updateCartTotal(cartId, totalAmount);
    }
}
