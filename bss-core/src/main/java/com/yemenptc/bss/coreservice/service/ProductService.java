package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ProductOffering;
import com.yemenptc.bss.coreservice.entity.ProductCategory;
import com.yemenptc.bss.coreservice.entity.ProductPrice;
import com.yemenptc.bss.coreservice.repository.ProductOfferingRepository;
import com.yemenptc.bss.coreservice.repository.ProductCategoryRepository;
import com.yemenptc.bss.coreservice.repository.ProductPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Product Catalog Service - TMF620 Product Catalog Management API
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductOfferingRepository offeringRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductPriceRepository priceRepository;

    @Transactional
    public ProductOffering createProductOffering(ProductOffering request) {
        log.info("Creating product offering: {}", request.getName());
        
        ProductOffering offering = ProductOffering.builder()
            .name(request.getName())
            .description(request.getDescription())
            .status(ProductOffering.ProductStatus.ACTIVE)
            .serviceType(request.getServiceType())
            .categoryId(request.getCategoryId())
            .isBundle(request.getIsBundle() != null ? request.getIsBundle() : false)
            .bundleType(request.getBundleType())
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .build();
        
        return offeringRepository.save(offering);
    }

    @Transactional(readOnly = true)
    public ProductOffering getProductOffering(UUID id) {
        return offeringRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product offering not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProductOffering> listProductOfferings(Pageable pageable) {
        return offeringRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductOffering> getProductsByStatus(ProductOffering.ProductStatus status, Pageable pageable) {
        return offeringRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductOffering> getProductsByServiceType(String serviceType, Pageable pageable) {
        return offeringRepository.findByServiceType(serviceType, pageable);
    }

    @Transactional(readOnly = true)
    public List<ProductOffering> getBundles() {
        return offeringRepository.findByIsBundleTrue();
    }

    @Transactional
    public ProductOffering updateProductOffering(UUID id, ProductOffering request) {
        ProductOffering existing = getProductOffering(id);
        
        ProductOffering updated = ProductOffering.builder()
            .name(request.getName() != null ? request.getName() : existing.getName())
            .description(request.getDescription() != null ? request.getDescription() : existing.getDescription())
            .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
            .serviceType(request.getServiceType() != null ? request.getServiceType() : existing.getServiceType())
            .categoryId(request.getCategoryId() != null ? request.getCategoryId() : existing.getCategoryId())
            .isBundle(request.getIsBundle() != null ? request.getIsBundle() : existing.getIsBundle())
            .bundleType(request.getBundleType() != null ? request.getBundleType() : existing.getBundleType())
            .validFrom(request.getValidFrom() != null ? request.getValidFrom() : existing.getValidFrom())
            .validTo(request.getValidTo() != null ? request.getValidTo() : existing.getValidTo())
            .build();
        
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(Instant.now());
        updated.setVersion(existing.getVersion() + 1);
        
        return offeringRepository.save(updated);
    }

    @Transactional
    public ProductOffering activateProduct(UUID id) {
        ProductOffering offering = getProductOffering(id);
        offering.setStatus(ProductOffering.ProductStatus.ACTIVE);
        offering.setUpdatedAt(Instant.now());
        return offeringRepository.save(offering);
    }

    @Transactional
    public ProductOffering deactivateProduct(UUID id) {
        ProductOffering offering = getProductOffering(id);
        offering.setStatus(ProductOffering.ProductStatus.INACTIVE);
        offering.setUpdatedAt(Instant.now());
        return offeringRepository.save(offering);
    }

    @Transactional
    public void deleteProductOffering(UUID id) {
        if (!offeringRepository.existsById(id)) {
            throw new RuntimeException("Product offering not found: " + id);
        }
        offeringRepository.deleteById(id);
    }

    @Transactional
    public ProductCategory createCategory(ProductCategory request) {
        log.info("Creating category: {}", request.getName());
        
        ProductCategory category = ProductCategory.builder()
            .name(request.getName())
            .description(request.getDescription())
            .parentId(request.getParentId())
            .level(request.getParentId() != null ? 1 : 0)
            .isActive(true)
            .build();
        
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public ProductCategory getCategory(String id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductCategory> listCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public ProductPrice createProductPrice(ProductPrice request) {
        log.info("Creating price for product: {}", request.getProductOfferingId());
        
        ProductPrice price = ProductPrice.builder()
            .productOfferingId(request.getProductOfferingId())
            .name(request.getName())
            .priceType(request.getPriceType())
            .price(request.getPrice())
            .currency(request.getCurrency() != null ? request.getCurrency() : "YER")
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .build();
        
        return priceRepository.save(price);
    }

    @Transactional(readOnly = true)
    public List<ProductPrice> getPricesByProduct(String productId) {
        return priceRepository.findByProductOfferingId(productId);
    }
}
