package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ProductOffering;
import com.yemenptc.bss.coreservice.entity.ProductCategory;
import com.yemenptc.bss.coreservice.entity.ProductPrice;
import com.yemenptc.bss.coreservice.repository.ProductOfferingRepository;
import com.yemenptc.bss.coreservice.repository.ProductCategoryRepository;
import com.yemenptc.bss.coreservice.repository.ProductPriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductOfferingRepository offeringRepository;
    @Mock
    private ProductCategoryRepository categoryRepository;
    @Mock
    private ProductPriceRepository priceRepository;

    @InjectMocks
    private ProductService productService;

    private ProductOffering createTestOffering() {
        ProductOffering offering = new ProductOffering();
        offering.setName("Test Plan");
        offering.setServiceType("MOBILE");
        offering.setStatus(ProductOffering.ProductStatus.ACTIVE);
        return offering;
    }

    @Test
    void createProductOffering_Success() {
        ProductOffering request = createTestOffering();
        when(offeringRepository.save(any(ProductOffering.class))).thenAnswer(i -> {
            ProductOffering o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });

        ProductOffering result = productService.createProductOffering(request);

        assertNotNull(result);
        assertEquals("Test Plan", result.getName());
        verify(offeringRepository).save(any(ProductOffering.class));
    }

    @Test
    void getProductOffering_Success() {
        UUID id = UUID.randomUUID();
        ProductOffering offering = createTestOffering();
        offering.setId(id);
        when(offeringRepository.findById(id)).thenReturn(Optional.of(offering));

        ProductOffering result = productService.getProductOffering(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void listProductOfferings_Success() {
        Page<ProductOffering> page = new PageImpl<>(List.of());
        when(offeringRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<ProductOffering> result = productService.listProductOfferings(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void activateProduct_Success() {
        UUID id = UUID.randomUUID();
        ProductOffering offering = createTestOffering();
        offering.setId(id);
        offering.setStatus(ProductOffering.ProductStatus.INACTIVE);
        when(offeringRepository.findById(id)).thenReturn(Optional.of(offering));
        when(offeringRepository.save(any(ProductOffering.class))).thenAnswer(i -> i.getArgument(0));

        ProductOffering result = productService.activateProduct(id);

        assertNotNull(result);
        assertEquals(ProductOffering.ProductStatus.ACTIVE, result.getStatus());
    }

    @Test
    void createCategory_Success() {
        ProductCategory request = new ProductCategory();
        request.setName("Mobile Plans");
        when(categoryRepository.save(any(ProductCategory.class))).thenAnswer(i -> {
            ProductCategory c = i.getArgument(0);
            c.setId(UUID.randomUUID().toString());
            return c;
        });

        ProductCategory result = productService.createCategory(request);

        assertNotNull(result);
        assertEquals("Mobile Plans", result.getName());
    }

    @Test
    void createProductPrice_Success() {
        String productId = UUID.randomUUID().toString();
        ProductPrice request = new ProductPrice();
        request.setProductOfferingId(productId);
        request.setName("Monthly Fee");
        request.setPriceType(ProductPrice.PriceType.RECURRING);
        request.setPrice(BigDecimal.valueOf(1000));
        when(priceRepository.save(any(ProductPrice.class))).thenAnswer(i -> {
            ProductPrice p = i.getArgument(0);
            p.setId(UUID.randomUUID().toString());
            return p;
        });

        ProductPrice result = productService.createProductPrice(request);

        assertNotNull(result);
        assertEquals("Monthly Fee", result.getName());
    }
}
