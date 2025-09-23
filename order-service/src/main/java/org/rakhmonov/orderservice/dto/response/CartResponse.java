package org.rakhmonov.orderservice.dto.response;

import lombok.*;
import org.rakhmonov.orderservice.entity.Cart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private List<CartItemResponse> cartItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartResponse fromEntity(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .totalAmount(cart.getTotalAmount())
                .cartItems(cart.getCartItems() != null ? 
                    cart.getCartItems().stream()
                        .map(CartItemResponse::fromEntity)
                        .toList() : null)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}


