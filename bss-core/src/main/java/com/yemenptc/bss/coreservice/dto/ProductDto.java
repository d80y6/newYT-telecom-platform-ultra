package com.yemenptc.bss.coreservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private String status;
    private String serviceType;
    private String categoryId;
    private Boolean isBundle;
    private String bundleType;
    private Instant createdAt;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductOfferingCreateRequest {
    private String name;
    private String description;
    private String serviceType;
    private String categoryId;
    private Boolean isBundle;
    private String bundleType;
    private List<ProductCharacteristicCreateRequest> characteristics;
    private List<ProductPriceCreateRequest> prices;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductOfferingUpdateRequest {
    private String name;
    private String description;
    private String status;
    private String categoryId;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductCharacteristicCreateRequest {
    private String name;
    private String description;
    private String valueType;
    private String value;
    private String unitOfMeasure;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private List<String> allowedValues;
    private Integer sortOrder;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductCharacteristicDto {
    private String id;
    private String productOfferingId;
    private String name;
    private String description;
    private String valueType;
    private String value;
    private String unitOfMeasure;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductPriceCreateRequest {
    private String name;
    private String description;
    private String priceType;
    private BigDecimal price;
    private String currency;
    private String unitOfMeasure;
    private Boolean taxIncluded;
    private Instant validFrom;
    private Instant validTo;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductPriceDto {
    private String id;
    private String productOfferingId;
    private String name;
    private String priceType;
    private BigDecimal price;
    private String currency;
    private Instant validFrom;
    private Instant validTo;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductCategoryCreateRequest {
    private String name;
    private String description;
    private String parentId;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductCategoryDto {
    private String id;
    private String name;
    private String description;
    private String parentId;
    private Integer level;
    private String path;
    private Boolean isActive;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductBundleCreateRequest {
    private String name;
    private String description;
    private String bundleType;
    private List<String> productOfferingIds;
    private BigDecimal discountPercent;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductValidationRequest {
    private String productOfferingId;
    private String serviceType;
    private List<ProductCharacteristicCreateRequest> characteristics;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProductValidationResult {
    private boolean valid;
    private List<String> errors;
    private List<String> warnings;
}
