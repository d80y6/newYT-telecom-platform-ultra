package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ProductPrice;
import com.yemenptc.bss.coreservice.service.ProductPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tmf-api/productPriceManagement/v5")
@RequiredArgsConstructor
@Tag(name = "Product Price Management", description = "TMF625 Product Price Management API")
public class ProductPriceController {

    private final ProductPriceService productPriceService;

    @PostMapping("/productPrice")
    @Operation(summary = "Create product price", description = "Creates a new product price")
    public ResponseEntity<ProductPrice> createPrice(@RequestBody ProductPrice request) {
        ProductPrice price = productPriceService.createPrice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(price);
    }

    @GetMapping("/productPrice")
    @Operation(summary = "List product prices", description = "Retrieves all product prices with pagination")
    public ResponseEntity<Page<ProductPrice>> listPrices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductPrice> prices = productPriceService.listPrices(PageRequest.of(page, size));
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/productPrice/{id}")
    @Operation(summary = "Get product price", description = "Retrieves a product price by ID")
    public ResponseEntity<ProductPrice> getPrice(@PathVariable String id) {
        ProductPrice price = productPriceService.getPrice(id);
        return ResponseEntity.ok(price);
    }

    @PutMapping("/productPrice/{id}")
    @Operation(summary = "Update product price", description = "Updates an existing product price")
    public ResponseEntity<ProductPrice> updatePrice(
            @PathVariable String id, 
            @RequestBody ProductPrice request) {
        ProductPrice price = productPriceService.updatePrice(id, request);
        return ResponseEntity.ok(price);
    }

    @DeleteMapping("/productPrice/{id}")
    @Operation(summary = "Delete product price", description = "Deletes a product price")
    public ResponseEntity<Void> deletePrice(@PathVariable String id) {
        productPriceService.deletePrice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/productPrice/product/{productOfferingId}")
    @Operation(summary = "Get prices by product offering", description = "Retrieves prices for a product offering")
    public ResponseEntity<List<ProductPrice>> getPricesByProductOffering(@PathVariable String productOfferingId) {
        List<ProductPrice> prices = productPriceService.getPricesByProductOffering(productOfferingId);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/productPrice/type/{priceType}")
    @Operation(summary = "Get prices by type", description = "Retrieves prices by type")
    public ResponseEntity<List<ProductPrice>> getPricesByType(@PathVariable ProductPrice.PriceType priceType) {
        List<ProductPrice> prices = productPriceService.getPricesByType(priceType);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/productPrice/calculate")
    @Operation(summary = "Calculate total price", description = "Calculates total price for a product offering")
    public ResponseEntity<BigDecimal> calculateTotalPrice(
            @RequestParam String productOfferingId,
            @RequestParam(defaultValue = "1") int quantity) {
        BigDecimal total = productPriceService.calculateTotalPrice(productOfferingId, quantity);
        return ResponseEntity.ok(total);
    }
}
