package org.rakhmonov.inventoryservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.inventoryservice.entity.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    private String name;
    private String description;
    private Long parentId;
    // Mapper metod
    public Category toEntity(Category parent) {
        return Category.builder()
                .name(this.name)
                .description(this.description)
                .parent(parent)   // parent null bo‘lishi ham mumkin
                .isActive(true)
                .build();
    }
}
