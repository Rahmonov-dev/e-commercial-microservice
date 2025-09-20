package org.rakhmonov.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.inventoryservice.dto.ProductVariantRequest;
import org.rakhmonov.inventoryservice.dto.ProductVariantResponse;
import org.rakhmonov.inventoryservice.service.ProductVariantService;
import org.rakhmonov.inventoryservice.util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService productVariantService;

}