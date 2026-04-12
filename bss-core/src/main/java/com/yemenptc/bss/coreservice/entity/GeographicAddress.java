package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * TMF653 Geographic Address Management.
 * Stores addresses with serviceability information.
 */
@Entity
@Table(name = "geographic_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeographicAddress extends BaseTmfEntity {

    @Column(name = "street_number", length = 20)
    private String streetNumber;

    @Column(length = 200)
    private String street;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String locality;

    @Column(name = "post_code", length = 20)
    private String postCode;

    @Column(length = 100)
    private String country = "YE";

    @Column(length = 100)
    private String governorate;

    @Column(name = "latitude", precision = 10, scale = 7)
    private java.math.BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private java.math.BigDecimal longitude;

    @Column(name = "ftth_coverage")
    private Boolean ftthCoverage = false;

    @Column(name = "adsl_coverage")
    private Boolean adslCoverage = false;

    @Column(name = "4g_coverage")
    private Boolean coverage4g = false;

    @Column(name = "nearest_olt", length = 100)
    private String nearestOlt;

    @Column(name = "copper_distance_m")
    private Integer copperDistanceM;

    @Override
    protected String getApiPath() {
        return "/tmf-api/geographicAddressManagement/v5/geographicAddress";
    }
}
