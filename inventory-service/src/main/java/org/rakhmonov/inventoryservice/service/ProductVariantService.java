package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.inventoryservice.dto.ProductVariantRequest;
import org.rakhmonov.inventoryservice.dto.ProductVariantResponse;
import org.rakhmonov.inventoryservice.entity.Product;
import org.rakhmonov.inventoryservice.entity.ProductVariant;
import org.rakhmonov.inventoryservice.exception.ProductNotFoundException;
import org.rakhmonov.inventoryservice.exception.ProductVariantNotFoundException;
import org.rakhmonov.inventoryservice.repo.ProductRepository;
import org.rakhmonov.inventoryservice.repo.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

}