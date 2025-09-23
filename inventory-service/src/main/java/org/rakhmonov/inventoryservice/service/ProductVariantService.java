package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.inventoryservice.dto.request.ProductVariantRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductVariantResponse;
import org.rakhmonov.inventoryservice.entity.Product;
import org.rakhmonov.inventoryservice.entity.ProductVariant;
import org.rakhmonov.inventoryservice.exception.ProductNotFoundException;
import org.rakhmonov.inventoryservice.exception.ProductVariantNotFoundException;
import org.rakhmonov.inventoryservice.repo.ProductRepository;
import org.rakhmonov.inventoryservice.repo.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ProductVariantResponse createProductVariant(Long productId, ProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .variantName(request.getVariantName())
                .variantValue(request.getVariantValue())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .price(request.getPrice())
                .cost(request.getCost())
                .weight(request.getWeight())
                .dimensions(request.getDimensions())
                .imageUrl(request.getImageUrl())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .sortOrder(request.getSortOrder())
                .status(ProductVariant.VariantStatus.ACTIVE)
                .build();

        ProductVariant savedVariant = productVariantRepository.save(variant);
        return toResponse(savedVariant);
    }

    @Transactional
    public ProductVariantResponse updateProductVariant(Long productId, Long variantId, ProductVariantRequest request) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));

        variant.setVariantName(request.getVariantName());
        variant.setVariantValue(request.getVariantValue());
        variant.setSku(request.getSku());
        variant.setBarcode(request.getBarcode());
        variant.setPrice(request.getPrice());
        variant.setCost(request.getCost());
        variant.setWeight(request.getWeight());
        variant.setDimensions(request.getDimensions());
        variant.setImageUrl(request.getImageUrl());
        variant.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : variant.getStockQuantity());
        variant.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : variant.getIsDefault());
        variant.setSortOrder(request.getSortOrder());

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return toResponse(updatedVariant);
    }

    public ProductVariantResponse getProductVariant(Long productId, Long variantId) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));
        return toResponse(variant);
    }

    public List<ProductVariantResponse> getProductVariants(Long productId) {
        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteProductVariant(Long productId, Long variantId) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));
        productVariantRepository.delete(variant);
    }

    @Transactional
    public ProductVariantResponse updateStock(Long productId, Long variantId, Integer quantity) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));
        
        variant.setStockQuantity(quantity);
        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return toResponse(updatedVariant);
    }

    @Transactional
    public ProductVariantResponse reserveStock(Long productId, Long variantId, Integer quantity) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));
        
        variant.reserveStock(quantity);
        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return toResponse(updatedVariant);
    }

    @Transactional
    public ProductVariantResponse releaseReservedStock(Long productId, Long variantId, Integer quantity) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));
        
        variant.releaseReservedStock(quantity);
        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return toResponse(updatedVariant);
    }

    @Transactional
    public ProductVariantResponse activateVariant(Long productId, Long variantId) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));
        
        variant.setStatus(ProductVariant.VariantStatus.ACTIVE);
        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return toResponse(updatedVariant);
    }

    @Transactional
    public ProductVariantResponse deactivateVariant(Long productId, Long variantId) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));
        
        variant.setStatus(ProductVariant.VariantStatus.INACTIVE);
        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return toResponse(updatedVariant);
    }

    private ProductVariantResponse toResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
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
                .status(variant.getStatus())
                .sortOrder(variant.getSortOrder())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .isInStock(variant.isInStock())
                .hasPriceOverride(variant.getPrice() != null)
                .effectivePrice(variant.getEffectivePrice())
                .build();
    }
}