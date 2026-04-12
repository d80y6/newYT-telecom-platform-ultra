package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "service_catalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCatalog extends BaseTmfEntity {

    @Column(name = "catalog_id", unique = true, length = 50)
    private String catalogId;

    @Column(name = "catalog_name", nullable = false, length = 200)
    private String catalogName;

    @Column(name = "catalog_description", length = 500)
    private String catalogDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "catalog_status", length = 20)
    private CatalogStatus status;

    @Column(name = "catalog_version", length = 20)
    private String catalogVersion;

    @Column(name = "valid_from")
    private java.time.Instant validFrom;

    @Column(name = "valid_to")
    private java.time.Instant validTo;

    @Column(name = "locale", length = 10)
    private String locale;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "service_count")
    private Integer serviceCount;

    public enum CatalogStatus {
        ACTIVE, DRAFT, SUSPENDED, RETIRED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/serviceCatalog/v5/serviceCatalog";
    }
}
