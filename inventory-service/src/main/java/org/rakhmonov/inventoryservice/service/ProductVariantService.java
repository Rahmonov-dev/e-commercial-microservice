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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    @Value("${app.upload.dir:/home/baxti/uploads}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8082}")
    private String baseUrl;

    @Transactional
    public ProductVariantResponse createProductVariant(Long productId, ProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        String imageUrl = null;
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            String uniqueFileName = generateUniqueFileName(request.getImageUrl());
            imageUrl = "/uploads/" + uniqueFileName;
        }

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
                .imageUrl(imageUrl)
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .sortOrder(request.getSortOrder())
                .status(ProductVariant.VariantStatus.ACTIVE)
                .build();

        ProductVariant savedVariant = productVariantRepository.save(variant);
        ProductVariantResponse response = toResponse(savedVariant);
        if (response.getImageUrl() != null) {
            response.setImageUrl(buildImageUrl(response.getImageUrl()));
        }
        return response;
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
        
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty()) {
                deleteOldImage(variant.getImageUrl());
            }
            String uniqueFileName = generateUniqueFileName(request.getImageUrl());
            variant.setImageUrl("/uploads/" + uniqueFileName);
        }
        
        variant.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : variant.getStockQuantity());
        variant.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : variant.getIsDefault());
        variant.setSortOrder(request.getSortOrder());

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        ProductVariantResponse response = toResponse(updatedVariant);
        if (response.getImageUrl() != null) {
            response.setImageUrl(buildImageUrl(response.getImageUrl()));
        }
        return response;
    }

    public ProductVariantResponse getProductVariant(Long productId, Long variantId) {
        ProductVariant variant = productVariantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId, productId));
        ProductVariantResponse response = toResponse(variant);
        if (response.getImageUrl() != null) {
            response.setImageUrl(buildImageUrl(response.getImageUrl()));
        }
        return response;
    }

    public List<ProductVariantResponse> getProductVariants(Long productId) {
        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(variant -> {
                    ProductVariantResponse response = toResponse(variant);
                    if (response.getImageUrl() != null) {
                        response.setImageUrl(buildImageUrl(response.getImageUrl()));
                    }
                    return response;
                })
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

    private String generateUniqueFileName(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Image file is required");
        }
        
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("Image filename is required");
        }
        
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        String filePath = uploadDir + "/" + uniqueFileName;
        
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            image.transferTo(new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }

        return uniqueFileName;
    }

    private void deleteOldImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, filename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (Exception e) {
            log.error("Failed to delete old image: {}", imageUrl, e);
        }
    }

    private String buildImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        if (imagePath.startsWith("/")) {
            return baseUrl + imagePath;
        }
        return baseUrl + "/uploads/" + imagePath;
    }
}