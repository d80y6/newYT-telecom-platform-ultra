package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.TmfCharacteristics;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_offerings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOffering extends TmfCharacteristics {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "service_type", nullable = false, length = 30)
    private String serviceType;

    @Column(name = "category_id", columnDefinition = "uuid")
    private UUID categoryId;

    @Column(name = "is_bundle")
    private Boolean isBundle = false;

    @Column(name = "bundle_type", length = 30)
    private String bundleType;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_to")
    private Instant validTo;

    @Override
    protected String getApiPath() {
        return "/tmf-api/productCatalogManagement/v5/productOffering";
    }

    public enum ProductStatus { ACTIVE, INACTIVE, DRAFT, RETIRED }
}
