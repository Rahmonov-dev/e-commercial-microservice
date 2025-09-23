package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.ProductRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.entity.*;
import org.rakhmonov.inventoryservice.exception.CategoryNotFoundException;
import org.rakhmonov.inventoryservice.exception.ProductNotFoundException;
import org.rakhmonov.inventoryservice.repo.CategoryRepository;
import org.rakhmonov.inventoryservice.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productRequest.getCategoryId()));

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .sku(productRequest.getSku())
                .barcode(productRequest.getBarcode())
                .imageUrl(productRequest.getImageUrl())
                .category(category)
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productRequest.getCategoryId()));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setSku(productRequest.getSku());
        product.setBarcode(productRequest.getBarcode());
        product.setImageUrl(productRequest.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    public List<ProductResponse> searchProducts(String searchTerm) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        product.setIsActive(true);
        Product updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }

    @Transactional
    public ProductResponse deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        product.setIsActive(false);
        Product updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }
}
