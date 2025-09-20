package org.rakhmonov.inventoryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {
    
    private List<ProductResponse> products;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;
    
    // Filter and search information
    private String searchQuery;
    private Long categoryId;
    private String categoryName;
    private String productType;
    private String status;
    private String sortBy;
    private String sortDirection;
    
    // Summary statistics
    private long totalProducts;
    private long warehouseProducts;
    private long marketplaceProducts;
    private long activeProducts;
    private long inactiveProducts;
    private long outOfStockProducts;
    
    // Business logic methods
    public boolean hasResults() {
        return products != null && !products.isEmpty();
    }
    
    public int getProductCount() {
        return products != null ? products.size() : 0;
    }
    
    public boolean hasFilters() {
        return searchQuery != null || categoryId != null || productType != null || status != null;
    }
    
    public String getFilterSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            summary.append("Search: ").append(searchQuery);
        }
        
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Category: ").append(categoryName);
        }
        
        if (productType != null && !productType.trim().isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Type: ").append(productType);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Status: ").append(status);
        }
        
        return summary.toString();
    }
}




