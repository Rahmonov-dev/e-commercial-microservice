package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.CategoryRequest;
import org.rakhmonov.inventoryservice.dto.response.CategoryResponse;
import org.rakhmonov.inventoryservice.entity.Category;
import org.rakhmonov.inventoryservice.repo.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // Create
    public CategoryResponse createCategory(CategoryRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
        }

        Category saved = categoryRepository.save(request.toEntity(parent));
        return CategoryResponse.fromEntity(saved);
    }

    // Update
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null); // root
        }

        Category saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    // Delete
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // Get All (root + subCategories)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getParent() == null)
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    // Get By Id
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryResponse.fromEntity(category);
    }

    public List<CategoryResponse> searchCategories(String name) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(Category::getIsActive)
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    public CategoryResponse activateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setIsActive(true);
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    public CategoryResponse deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setIsActive(false);
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }
}
