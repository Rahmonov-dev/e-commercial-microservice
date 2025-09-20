package org.rakhmonov.inventoryservice.config;

import org.rakhmonov.inventoryservice.dto.request.ProductWithImageRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.math.BigDecimal;

@Component
public class ProductWithImageArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(ProductWithImageRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        
        MultipartHttpServletRequest request = webRequest.getNativeRequest(MultipartHttpServletRequest.class);
        if (request == null) {
            throw new IllegalArgumentException("Request must be multipart");
        }

        // Extract form data
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String sku = request.getParameter("sku");
        String barcode = request.getParameter("barcode");
        String categoryIdStr = request.getParameter("categoryId");
        String thirdPartySellerIdStr = request.getParameter("thirdPartySellerId");
        String currentStockStr = request.getParameter("currentStock");
        String minStockLevelStr = request.getParameter("minStockLevel");
        String maxStockLevelStr = request.getParameter("maxStockLevel");
        String reorderPointStr = request.getParameter("reorderPoint");
        String unitCostStr = request.getParameter("unitCost");
        
        // Get image file
        MultipartFile image = request.getFile("image");

        // Build the request object
        return ProductWithImageRequest.builder()
                .name(name)
                .description(description)
                .price(priceStr != null ? new BigDecimal(priceStr) : null)
                .sku(sku)
                .barcode(barcode)
                .categoryId(categoryIdStr != null ? Long.parseLong(categoryIdStr) : null)
                .thirdPartySellerId(thirdPartySellerIdStr != null ? Long.parseLong(thirdPartySellerIdStr) : null)
                .currentStock(currentStockStr != null ? Integer.parseInt(currentStockStr) : null)
                .minStockLevel(minStockLevelStr != null ? Integer.parseInt(minStockLevelStr) : null)
                .maxStockLevel(maxStockLevelStr != null ? Integer.parseInt(maxStockLevelStr) : null)
                .reorderPoint(reorderPointStr != null ? Integer.parseInt(reorderPointStr) : null)
                .unitCost(unitCostStr != null ? new BigDecimal(unitCostStr) : null)
                .image(image)
                .build();
    }
}



