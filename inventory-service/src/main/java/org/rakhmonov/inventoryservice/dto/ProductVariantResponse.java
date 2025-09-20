package org.rakhmonov.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.inventoryservice.entity.ProductVariant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {
    
    private Long id;
    private Long productId;
    private String productName;
    private String variantName;
    private String variantValue;
    private String sku;
    private String barcode;
    private BigDecimal price;
    private BigDecimal cost;
    private BigDecimal weight;
    private String dimensions;
    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isDefault;
    private String status;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    private BigDecimal effectivePrice;
    private Boolean inStock;
    private Boolean canReserve;
    
    public static ProductVariantResponse fromEntity(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .productName(variant.getProduct().getName())
                .variantName(variant.getVariantName())
                .variantValue(variant.getVariantValue())
                .sku(variant.getSku())
                .barcode(variant.getBarcode())
                .price(variant.getPrice())
                .cost(variant.getCost())
                .weight(variant.getWeight())
                .dimensions(variant.getDimensions())
                .imageUrl(variant.getImageUrl())
                .stockQuantity(variant.getStockQuantity())
                .isDefault(variant.getIsDefault())
                .status(variant.getStatus().name())
                .sortOrder(variant.getSortOrder())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .effectivePrice(variant.getEffectivePrice())
                .inStock(variant.isInStock())
                .canReserve(variant.canReserve(1)) // Check if can reserve at least 1
                .build();
    }
}




