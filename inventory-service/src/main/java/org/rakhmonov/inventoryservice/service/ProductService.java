package org.rakhmonov.inventoryservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.ProductRequest;
import org.rakhmonov.inventoryservice.dto.request.ProductWithImageRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.dto.response.CategorySummaryResponse;
import org.rakhmonov.inventoryservice.dto.response.ThirdPartySellerSummaryResponse;
import org.rakhmonov.inventoryservice.dto.response.InventorySummaryResponse;
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

}
