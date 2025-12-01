package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.ProductRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.entity.*;
import org.rakhmonov.inventoryservice.exception.CategoryNotFoundException;
import org.rakhmonov.inventoryservice.exception.ProductNotFoundException;
import org.rakhmonov.inventoryservice.repo.CategoryRepository;
import org.rakhmonov.inventoryservice.repo.InventoryRepository;
import org.rakhmonov.inventoryservice.repo.ProductRepository;
import org.rakhmonov.inventoryservice.util.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    @Value("${app.upload.dir:/home/baxti/uploads}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8082}")
    private String baseUrl;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productRequest.getCategoryId()));

        String imageUrl = null;
        if (productRequest.getImageUrl() != null && !productRequest.getImageUrl().isEmpty()) {
            String uniqueFileName = generateUniqueFileName(productRequest.getImageUrl());
            imageUrl = "/uploads/" + uniqueFileName;
        }
        Long thirdPartySellerId = null;
        Long supplierId = null;
        String userRole = JwtUtils.getCurrentUserRole();
        Long currentUserId = JwtUtils.getCurrentUserId();
        if (userRole != null) {
            if (userRole.equals("ROLE_SELLER") || userRole.equals("SELLER")) {
                thirdPartySellerId = currentUserId;
                supplierId = null;
            } else if (userRole.equals("ROLE_ADMIN") || userRole.equals("ADMIN")) {
                supplierId = productRequest.getSupplierId();
            }
        } else {
            thirdPartySellerId = productRequest.getThirdPartySellerId();
            supplierId = productRequest.getSupplierId();
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .sku(productRequest.getSku())
                .barcode(productRequest.getBarcode())
                .imageUrl(imageUrl)
                .category(category)
                .thirdPartySellerId(thirdPartySellerId)
                .supplierId(supplierId)
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        
        // Create inventory if inventory fields are provided
        if (productRequest.getWarehouseId() != null) {
            Inventory inventory = Inventory.builder()
                    .product(savedProduct)
                    .warehouseId(productRequest.getWarehouseId())
                    .currentStock(productRequest.getCurrentStock() != null ? productRequest.getCurrentStock() : 0)
                    .reorderPoint(productRequest.getReorderPoint() != null ? productRequest.getReorderPoint() : 10)
                    .unitCost(productRequest.getUnitCost())
                    .isDeleted(false)
                    .build();
            Inventory savedInventory = inventoryRepository.save(inventory);
            // Set inventory to product entity for proper relationship
            savedProduct.setInventory(savedInventory);
            savedProduct = productRepository.save(savedProduct);
        }
        
        // Reload product with inventory relationship
        Product productWithInventory = productRepository.findById(savedProduct.getId())
                .orElse(savedProduct);
        
        ProductResponse response = ProductResponse.fromEntity(productWithInventory);
        if (response.getImageUrl() != null) {
            response.setImageUrl(buildImageUrl(response.getImageUrl()));
        }
        return response;
    }
    public String generateUniqueFileName(MultipartFile image) {
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
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/"));
            Path filePath = Paths.get(uploadDir, filename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (Exception e) {
            System.err.println("Failed to delete old image: " + imageUrl + " - " + e.getMessage());
        }
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (productRequest.getName() != null && !productRequest.getName().trim().isEmpty()) {
            product.setName(productRequest.getName());
        }
        if (productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }
        if (productRequest.getPrice() != null) {
            product.setPrice(productRequest.getPrice());
        }
        if (productRequest.getSku() != null) {
            product.setSku(productRequest.getSku());
        }
        if (productRequest.getBarcode() != null) {
            product.setBarcode(productRequest.getBarcode());
        }
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
            product.setCategory(category);
        }
        
        if (productRequest.getImageUrl() != null && !productRequest.getImageUrl().isEmpty()) {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                deleteOldImage(product.getImageUrl());
            }
            String uniqueFileName = generateUniqueFileName(productRequest.getImageUrl());
            product.setImageUrl("/uploads/" + uniqueFileName);
        }
        String userRole = JwtUtils.getCurrentUserRole();
        Long currentUserId = JwtUtils.getCurrentUserId();
        
        // Check authorization: only seller can update their own products, admin can update any
        if (userRole != null && (userRole.equals("ROLE_SELLER") || userRole.equals("SELLER"))) {
            if (currentUserId == null || !currentUserId.equals(product.getThirdPartySellerId())) {
                throw new RuntimeException("You can only update your own products");
            }
        } else if (userRole != null && (userRole.equals("ROLE_ADMIN") || userRole.equals("ADMIN"))) {
            // Admin can update any product
            if (productRequest.getSupplierId() != null) {
                product.setSupplierId(productRequest.getSupplierId());
            }
        } else {
            throw new RuntimeException("Unauthorized to update product");
        }
        Product updatedProduct = productRepository.save(product);
        ProductResponse response = ProductResponse.fromEntity(updatedProduct);
        if (response.getImageUrl() != null) {
            response.setImageUrl(buildImageUrl(response.getImageUrl()));
        }
        return response;
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

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        // Load inventory if exists
        if (product.getInventory() == null) {
            List<Inventory> inventories = inventoryRepository.findByProduct(product);
            if (!inventories.isEmpty()) {
                product.setInventory(inventories.get(0));
            }
        }
        
        ProductResponse response = ProductResponse.fromEntity(product);
        if (response.getImageUrl() != null) {
            response.setImageUrl(buildImageUrl(response.getImageUrl()));
        }
        return response;
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> {
                    // Load inventory if exists
                    if (product.getInventory() == null) {
                        List<Inventory> inventories = inventoryRepository.findByProduct(product);
                        if (!inventories.isEmpty()) {
                            product.setInventory(inventories.get(0));
                        }
                    }
                    ProductResponse response = ProductResponse.fromEntity(product);
                    if (response.getImageUrl() != null) {
                        response.setImageUrl(buildImageUrl(response.getImageUrl()));
                    }
                    return response;
                });
    }

    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
        List<Long> categoryIds = getAllCategoryIdsRecursive(categoryId);
        categoryIds.add(categoryId);

        return productRepository.findByCategoryId(categoryIds, pageable)
                .map(product -> {
                    // Load inventory if exists
                    if (product.getInventory() == null) {
                        List<Inventory> inventories = inventoryRepository.findByProduct(product);
                        if (!inventories.isEmpty()) {
                            product.setInventory(inventories.get(0));
                        }
                    }
                    ProductResponse response = ProductResponse.fromEntity(product);
                    if (response.getImageUrl() != null) {
                        response.setImageUrl(buildImageUrl(response.getImageUrl()));
                    }
                    return response;
                });
    }

    public Page<ProductResponse> getProductsBySellerId(Long sellerId, Pageable pageable) {
        return productRepository.findByThirdPartySellerId(sellerId, pageable)
                .map(product -> {
                    if (product.getInventory() == null) {
                        List<Inventory> inventories = inventoryRepository.findByProduct(product);
                        if (!inventories.isEmpty()) {
                            product.setInventory(inventories.get(0));
                        }
                    }
                    ProductResponse response = ProductResponse.fromEntity(product);
                    if (response.getImageUrl() != null) {
                        response.setImageUrl(buildImageUrl(response.getImageUrl()));
                    }
                    return response;
                });
    }
    private List<Long> getAllCategoryIdsRecursive(Long categoryId) {
        List<Long> allIds = new ArrayList<>();
        List<Long> directChildren = categoryRepository.findCategoryChildrenIds(categoryId);
        
        if (directChildren != null && !directChildren.isEmpty()) {
            allIds.addAll(directChildren);
            for (Long childId : directChildren) {
                allIds.addAll(getAllCategoryIdsRecursive(childId));
            }
        }
        
        return allIds;
    }


    public Page<ProductResponse> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, pageable)
                .map(product -> {
                    if (product.getInventory() == null) {
                        List<Inventory> inventories = inventoryRepository.findByProduct(product);
                        if (!inventories.isEmpty()) {
                            product.setInventory(inventories.get(0));
                        }
                    }
                    ProductResponse response = ProductResponse.fromEntity(product);
                    if (response.getImageUrl() != null) {
                        response.setImageUrl(buildImageUrl(response.getImageUrl()));
                    }
                    return response;
                });
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        String userRole = JwtUtils.getCurrentUserRole();
        Long currentUserId = JwtUtils.getCurrentUserId();
        if (userRole != null && (userRole.equals("ROLE_SELLER") || userRole.equals("SELLER"))) {
            if (currentUserId == null || !currentUserId.equals(product.getThirdPartySellerId())) {
                throw new RuntimeException("You can only delete your own products");
            }
        } else if (userRole == null || (!userRole.equals("ROLE_ADMIN") && !userRole.equals("ADMIN") && 
                                         !userRole.equals("ROLE_SUPER_ADMIN") && !userRole.equals("SUPER_ADMIN"))) {
            throw new RuntimeException("Unauthorized to delete product");
        }
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    @Transactional
    public ProductResponse activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        product.setIsActive(true);
        Product updatedProduct = productRepository.save(product);
        ProductResponse response = ProductResponse.fromEntity(updatedProduct);
        if (response.getImageUrl() != null) {
            response.setImageUrl(buildImageUrl(response.getImageUrl()));
        }
        return response;
    }

    @Transactional
    public ProductResponse deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        product.setIsActive(false);
        Product updatedProduct = productRepository.save(product);
        ProductResponse response = ProductResponse.fromEntity(updatedProduct);
        if (response.getImageUrl() != null) {
            response.setImageUrl(buildImageUrl(response.getImageUrl()));
        }
        return response;
    }
}

