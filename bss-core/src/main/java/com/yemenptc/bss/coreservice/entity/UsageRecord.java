package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class UsageRecord extends BaseTmfEntity {

    @Column(name = "account_id", nullable = false, length = 50)
    private String accountId;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "subscription_id", length = 50)
    private String subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", nullable = false, length = 30)
    private UsageType usageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private UsageStatus status = UsageStatus.COLLECTED;

    @Column(name = "usage_start_date", nullable = false)
    private LocalDateTime usageStartDate;

    @Column(name = "usage_end_date")
    private LocalDateTime usageEndDate;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "volume_bytes")
    private Long volumeBytes;

    @Column(name = "volume_mb", precision = 15, scale = 2)
    private BigDecimal volumeMb;

    @Column(name = "count")
    private Integer count;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", length = 20)
    private UsageDirection direction;

    @Column(name = "destination", length = 100)
    private String destination;

    @Column(name = "origin", length = 100)
    private String origin;

    @Column(name = "product_id", length = 50)
    private String productId;

    @Column(name = "offering_id", length = 50)
    private String offeringId;

    @Column(name = "rated_amount", precision = 15, scale = 2)
    private BigDecimal ratedAmount;

    @Column(name = "currency", length = 3)
    private String currency = "YER";

    @Column(name = "rating_timestamp")
    private LocalDateTime ratingTimestamp;

    @Column(name = "rating_status", length = 20)
    private String ratingStatus;

    @Column(name = "cdr_id", length = 100)
    private String cdrId;

    @Column(name = "network_element", length = 100)
    private String networkElement;

    @Column(name = "location_id", length = 50)
    private String locationId;

    @Column(name = "location_zone", length = 50)
    private String locationZone;

    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributes;

    // TMF648 Extended Fields
    @Column(name = "product_offering_id", length = 50)
    private String productOfferingId;

    @Column(name = "usage_context", length = 100)
    private String usageContext;

    @Column(name = "location_area_code", length = 20)
    private String locationAreaCode;

    @Column(name = "cell_id", length = 50)
    private String cellId;

    @Column(name = "imsi", length = 20)
    private String imsi;

    @Column(name = "imei", length = 20)
    private String imei;

    @Column(name = "apn", length = 100)
    private String apn;

    @Column(name = "charging_class", length = 30)
    private String chargingClass;

    public enum UsageType {
        VOICE, SMS, DATA, CONTENT, EVENT
    }

    public enum UsageStatus {
        COLLECTED, RATED, ADJUSTED, DISPUTED, CLEARED
    }

    public enum UsageDirection {
        INCOMING, OUTGOING, TRANSIT
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/usageManagement/v5/usage";
    }
}