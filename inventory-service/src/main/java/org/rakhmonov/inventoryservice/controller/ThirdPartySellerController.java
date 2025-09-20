package org.rakhmonov.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.inventoryservice.dto.request.ThirdPartySellerRequest;
import org.rakhmonov.inventoryservice.dto.response.ProductResponse;
import org.rakhmonov.inventoryservice.dto.response.ThirdPartySellerResponse;
import org.rakhmonov.inventoryservice.service.ThirdPartySellerService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/third-party-sellers")
public class ThirdPartySellerController {
    private final ThirdPartySellerService thirdPartySellerService;

    @PostMapping
    public ResponseEntity<ThirdPartySellerResponse> createThirdPartySeller(@RequestBody ThirdPartySellerRequest request) {
        return ResponseEntity.ok(thirdPartySellerService.createThirdPartySeller(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThirdPartySellerResponse> updateThirdPartySeller(@PathVariable Long id, @RequestBody ThirdPartySellerRequest request) {
        return ResponseEntity.ok(thirdPartySellerService.updateThirdPartySeller(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThirdPartySeller(@PathVariable Long id) {
        thirdPartySellerService.deleteThirdPartySeller(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThirdPartySellerResponse> getThirdPartySellerById(@PathVariable Long id) {
        return ResponseEntity.ok(thirdPartySellerService.getThirdPartySellerById(id));
    }

    @GetMapping
    public ResponseEntity<List<ThirdPartySellerResponse>> getAllThirdPartySellers() {
        return ResponseEntity.ok(thirdPartySellerService.getAllThirdPartySellers());
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ThirdPartySellerResponse> activateThirdPartySeller(@PathVariable Long id) {
        return ResponseEntity.ok(thirdPartySellerService.activateThirdPartySeller(id));
    }

    @PatchMapping("/{id}/inactive")
    public ResponseEntity<ThirdPartySellerResponse> deactivateThirdPartySeller(@PathVariable Long id) {
        return ResponseEntity.ok(thirdPartySellerService.deactivateThirdPartySeller(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ThirdPartySellerResponse>> searchThirdPartySellers(@RequestParam String name) {
        return ResponseEntity.ok(thirdPartySellerService.searchThirdPartySellers(name));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ThirdPartySellerResponse>> getThirdPartySellersByPage(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size,
                                                                                     @RequestParam(defaultValue = "id") String sort) {
        return ResponseEntity.ok(thirdPartySellerService.getThirdPartySellersByPage(page, size, sort));
    }
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductResponse>> getProductsByThirdPartySellerId(@PathVariable Long id) {
        return ResponseEntity.ok(thirdPartySellerService.getProductsByThirdPartySellerId(id));
    }
}
