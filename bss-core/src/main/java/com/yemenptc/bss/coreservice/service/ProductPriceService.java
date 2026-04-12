package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ProductPrice;
import com.yemenptc.bss.coreservice.repository.ProductPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductPriceService {

    private final ProductPriceRepository productPriceRepository;

    @Transactional
    public ProductPrice createPrice(ProductPrice request) {
        log.info("Creating product price: {}", request.getName());

        ProductPrice price = ProductPrice.builder()
            .id(request.getId() != null ? request.getId() : UUID.randomUUID().toString())
            .productOfferingId(request.getProductOfferingId())
            .name(request.getName())
            .priceType(request.getPriceType())
            .price(request.getPrice())
            .currency(request.getCurrency() != null ? request.getCurrency() : "YER")
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .build();

        ProductPrice saved = productPriceRepository.save(price);
        log.info("Product price created: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public ProductPrice getPrice(String id) {
        return productPriceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product price not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProductPrice> listPrices(Pageable pageable) {
        return productPriceRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<ProductPrice> getPricesByProductOffering(String productOfferingId) {
        return productPriceRepository.findByProductOfferingId(productOfferingId);
    }

    @Transactional(readOnly = true)
    public List<ProductPrice> getPricesByType(ProductPrice.PriceType priceType) {
        return productPriceRepository.findByPriceType(priceType);
    }

    @Transactional
    public ProductPrice updatePrice(String id, ProductPrice request) {
        ProductPrice price = getPrice(id);
        if (request.getPrice() != null) price.setPrice(request.getPrice());
        if (request.getName() != null) price.setName(request.getName());
        if (request.getValidFrom() != null) price.setValidFrom(request.getValidFrom());
        if (request.getValidTo() != null) price.setValidTo(request.getValidTo());
        return productPriceRepository.save(price);
    }

    @Transactional
    public void deletePrice(String id) {
        ProductPrice price = getPrice(id);
        productPriceRepository.delete(price);
        log.info("Product price deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalPrice(String productOfferingId, int quantity) {
        List<ProductPrice> prices = getPricesByProductOffering(productOfferingId);
        BigDecimal total = BigDecimal.ZERO;
        
        for (ProductPrice price : prices) {
            if (price.getPriceType() == ProductPrice.PriceType.ONE_TIME) {
                total = total.add(price.getPrice());
            } else if (price.getPriceType() == ProductPrice.PriceType.RECURRING) {
                total = total.add(price.getPrice());
            } else if (price.getPriceType() == ProductPrice.PriceType.USAGE) {
                total = total.add(price.getPrice().multiply(BigDecimal.valueOf(quantity)));
            }
        }
        
        return total;
    }
}
