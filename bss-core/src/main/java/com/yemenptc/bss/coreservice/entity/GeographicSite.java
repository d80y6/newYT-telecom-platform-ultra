package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * TMF656 Geographic Site Management.
 * Stores sites: data centers, buildings, exchanges.
 */
@Entity
@Table(name = "geographic_sites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeographicSite extends BaseTmfEntity {

    @Column(name = "site_name", nullable = false, length = 200)
    private String siteName;

    @Enumerated(EnumType.STRING)
    @Column(name = "site_type", nullable = false, length = 30)
    private SiteType siteType;

    @Column(name = "address_id", columnDefinition = "uuid")
    private UUID addressId;

    @Column(name = "parent_site_id", columnDefinition = "uuid")
    private UUID parentSiteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SiteStatus status = SiteStatus.ACTIVE;

    @Column(length = 100)
    private String region;

    @Override
    protected String getApiPath() {
        return "/tmf-api/geographicSiteManagement/v4/geographicSite";
    }

    public enum SiteType {
        EXCHANGE, DATA_CENTER, TOWER, BUILDING, CUSTOMER_PREMISE, COLOCATION
    }

    public enum SiteStatus { ACTIVE, INACTIVE, MAINTENANCE, DECOMMISSIONED }
}
