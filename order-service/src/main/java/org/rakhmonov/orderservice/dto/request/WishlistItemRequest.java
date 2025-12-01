package org.rakhmonov.orderservice.dto.request;

import lombok.Data;

@Data
public class WishlistItemRequest {
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private Double productPrice;
}
