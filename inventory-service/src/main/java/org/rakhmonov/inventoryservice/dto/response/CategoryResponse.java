package org.rakhmonov.inventoryservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.inventoryservice.entity.Category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long parentId;

    @Builder.Default
    private List<CategoryResponse> subCategories = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static CategoryResponse fromEntity(Category category) {
        if (category == null) return null;

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .createdAt(category.getCreatedAt())
                .subCategories(
                        category.getSubCategories() != null
                                ? category.getSubCategories().stream()
                                .map(CategoryResponse::fromEntity) // recursion
                                .toList()
                                : new ArrayList<>()
                )
                .build();
    }
}

