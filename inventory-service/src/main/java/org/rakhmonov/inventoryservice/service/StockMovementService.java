package org.rakhmonov.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.repo.StockMovementRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;
}
