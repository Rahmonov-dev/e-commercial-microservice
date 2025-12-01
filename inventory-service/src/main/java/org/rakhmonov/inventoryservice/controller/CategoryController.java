package org.rakhmonov.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.CategoryRequest;
import org.rakhmonov.inventoryservice.dto.response.CategoryResponse;
import org.rakhmonov.inventoryservice.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String test() {
        return "Security is working";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')  or hasRole('SUPER_ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        // Convert List to Page manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), categories.size());
        
        // Create a proper Page from the list
        Page<CategoryResponse> result = new org.springframework.data.domain.PageImpl<>(
                start < categories.size() ? categories.subList(start, end) : java.util.Collections.emptyList(),
                pageable,
                categories.size()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CategoryResponse>> searchCategories(
            @RequestParam String name,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        List<CategoryResponse> categories = categoryService.searchCategories(name);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), categories.size());
        Page<CategoryResponse> result = new org.springframework.data.domain.PageImpl<>(
                categories.subList(start, end),
                pageable,
                categories.size()
        );
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<CategoryResponse> deactivateCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deactivateCategory(id));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<CategoryResponse> activateCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.activateCategory(id));
    }
}
