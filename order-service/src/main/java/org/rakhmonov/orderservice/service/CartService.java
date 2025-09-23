package org.rakhmonov.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.orderservice.dto.response.CartResponse;
import org.rakhmonov.orderservice.entity.Cart;
import org.rakhmonov.orderservice.exception.CartNotFoundException;
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
public class CartService {
    private final CartRepository cartRepository;

    @Transactional
    public CartResponse createCart(Long userId) {
        // Check if cart already exists for user
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return CartResponse.fromEntity(existingCart.get());
        }

        Cart cart = Cart.builder()
                .userId(userId)
                .totalAmount(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Cart savedCart = cartRepository.save(cart);
        log.info("Cart created for user ID: {}", userId);
        
        return CartResponse.fromEntity(savedCart);
    }

    public CartResponse getCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));
        return CartResponse.fromEntity(cart);
    }

    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));
        return CartResponse.fromEntity(cart);
    }

    public List<CartResponse> getAllCarts() {
        return cartRepository.findAll()
                .stream()
                .map(CartResponse::fromEntity)
                .toList();
    }

    @Transactional
    public CartResponse updateCartTotal(Long cartId, BigDecimal totalAmount) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
        
        cart.setTotalAmount(totalAmount);
        cart.setUpdatedAt(LocalDateTime.now());
        
        Cart updatedCart = cartRepository.save(cart);
        log.info("Cart total updated to {} for cart ID: {}", totalAmount, cartId);
        
        return CartResponse.fromEntity(updatedCart);
    }

    @Transactional
    public void deleteCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));
        
        cartRepository.delete(cart);
        log.info("Cart deleted with ID: {}", id);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));
        
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        
        cartRepository.save(cart);
        log.info("Cart cleared for user ID: {}", userId);
    }

    public CartResponse getOrCreateCart(Long userId) {
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return CartResponse.fromEntity(existingCart.get());
        }
        
        return createCart(userId);
    }
}
