package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ProductOffering;
import com.yemenptc.bss.coreservice.entity.ProductCategory;
import com.yemenptc.bss.coreservice.entity.ProductPrice;
import com.yemenptc.bss.coreservice.service.ProductService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/productCatalogManagement/v5")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Product Offering endpoints
    @PostMapping("/productOffering")
    public ResponseEntity<TmfResponse<ProductOffering>> createOffering(@RequestBody ProductOffering request) {
        ProductOffering offering = productService.createProductOffering(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(offering, "ProductOffering"));
    }

    @GetMapping("/productOffering/{id}")
    public ResponseEntity<TmfResponse<ProductOffering>> getOffering(@PathVariable UUID id) {
        ProductOffering offering = productService.getProductOffering(id);
        return ResponseEntity.ok(TmfResponse.success(offering, "ProductOffering"));
    }

    @GetMapping("/productOffering")
    public ResponseEntity<TmfResponse<ProductOffering>> listOfferings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductOffering> result = productService.listProductOfferings(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "ProductOffering"));
    }

    @PutMapping("/productOffering/{id}")
    public ResponseEntity<TmfResponse<ProductOffering>> updateOffering(
            @PathVariable UUID id, @RequestBody ProductOffering request) {
        ProductOffering offering = productService.updateProductOffering(id, request);
        return ResponseEntity.ok(TmfResponse.success(offering, "ProductOffering"));
    }

    @PostMapping("/productOffering/{id}/activate")
    public ResponseEntity<TmfResponse<ProductOffering>> activateOffering(@PathVariable UUID id) {
        ProductOffering offering = productService.activateProduct(id);
        return ResponseEntity.ok(TmfResponse.success(offering, "ProductOffering"));
    }

    @PostMapping("/productOffering/{id}/deactivate")
    public ResponseEntity<TmfResponse<ProductOffering>> deactivateOffering(@PathVariable UUID id) {
        ProductOffering offering = productService.deactivateProduct(id);
        return ResponseEntity.ok(TmfResponse.success(offering, "ProductOffering"));
    }

    @DeleteMapping("/productOffering/{id}")
    public ResponseEntity<Void> deleteOffering(@PathVariable UUID id) {
        productService.deleteProductOffering(id);
        return ResponseEntity.noContent().build();
    }

    // Category endpoints
    @PostMapping("/category")
    public ResponseEntity<TmfResponse<ProductCategory>> createCategory(@RequestBody ProductCategory request) {
        ProductCategory category = productService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(category, "Category"));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<TmfResponse<ProductCategory>> getCategory(@PathVariable String id) {
        ProductCategory category = productService.getCategory(id);
        return ResponseEntity.ok(TmfResponse.success(category, "Category"));
    }

    @GetMapping("/category")
    public ResponseEntity<TmfResponse<ProductCategory>> listCategories() {
        List<ProductCategory> categories = productService.listCategories();
        return ResponseEntity.ok(TmfResponse.list(
                categories, categories.size(), 0, categories.size(), "Category"));
    }

    // Price endpoints
    @PostMapping("/price")
    public ResponseEntity<TmfResponse<ProductPrice>> createPrice(@RequestBody ProductPrice request) {
        ProductPrice price = productService.createProductPrice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(price, "Price"));
    }

    @GetMapping("/price/product/{productId}")
    public ResponseEntity<TmfResponse<ProductPrice>> getPricesByProduct(@PathVariable UUID productId) {
        List<ProductPrice> prices = productService.getPricesByProduct(productId.toString());
        return ResponseEntity.ok(TmfResponse.list(
                prices, prices.size(), 0, prices.size(), "Price"));
    }

    @GetMapping("/catalog")
    public ResponseEntity<TmfResponse<ProductCategory>> getCatalog() {
        List<ProductCategory> categories = productService.listCategories();
        return ResponseEntity.ok(TmfResponse.list(
                categories, categories.size(), 0, categories.size(), "Catalog"));
    }
}
