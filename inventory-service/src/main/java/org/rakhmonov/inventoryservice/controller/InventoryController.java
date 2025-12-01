//package org.rakhmonov.inventoryservice.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.rakhmonov.inventoryservice.dto.request.DecreaseStockRequest;
//import org.rakhmonov.inventoryservice.entity.Inventory;
//import org.rakhmonov.inventoryservice.service.InventoryService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/inventory")
//@RequiredArgsConstructor
//public class InventoryController {
//    private final InventoryService inventoryService;
//
//    @PostMapping("/decrease-stock")
//    public ResponseEntity<Inventory> decreaseStock(@RequestBody DecreaseStockRequest request) {
//        try {
//            Inventory updatedInventory = inventoryService.decreaseStock(
//                    request.getProductId(),
//                    request.getQuantity(),
//                    request.getWarehouseId()
//            );
//            return ResponseEntity.ok(updatedInventory);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//    }
//
//
//    @PostMapping("/decrease-stock-for-order")
//    public ResponseEntity<List<Inventory>> decreaseStockForOrder(@RequestBody Map<Long, Integer> productQuantities) {
//        try {
//            List<Inventory> updatedInventories = inventoryService.decreaseStockForOrder(productQuantities);
//            return ResponseEntity.ok(updatedInventories);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//    }
//}
//
//
//
//
//
//

