package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Pricing Rule - defines rate tables for different service types
 * Supports time-based, volume-based, and destination-based rating
 */
@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "rule_id", unique = true, nullable = false, length = 50)
    private String ruleId;

    @Column(name = "plan_id", nullable = false, length = 50)
    private String planId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 30)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_type", nullable = false, length = 20)
    private RatingType ratingType;

    @Column(name = "usage_unit", nullable = false, length = 20)
    private String usageUnit;

    @Column(name = "rate_per_unit", nullable = false, precision = 15, scale = 4)
    private BigDecimal ratePerUnit;

    @Column(name = "flat_fee", precision = 15, scale = 2)
    private BigDecimal flatFee;

    @Column(name = "included_units")
    private BigDecimal includedUnits;

    @Column(name = "billing_unit", length = 20)
    private String billingUnit;

    @Column(name = "min_charge", precision = 15, scale = 2)
    private BigDecimal minCharge;

    @Column(name = "max_charge", precision = 15, scale = 2)
    private BigDecimal maxCharge;

    @Column(name = "peak_rate", precision = 15, scale = 4)
    private BigDecimal peakRate;

    @Column(name = "off_peak_rate", precision = 15, scale = 4)
    private BigDecimal offPeakRate;

    @Column(name = "peak_start_hour")
    private Integer peakStartHour;

    @Column(name = "peak_end_hour")
    private Integer peakEndHour;

    @Column(name = "destination_prefix", length = 20)
    private String destinationPrefix;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "effective_from")
    private Instant effectiveFrom;

    @Column(name = "effective_to")
    private Instant effectiveTo;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (ruleId == null) ruleId = "PR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public enum ServiceType {
        VOICE, SMS, DATA, VAS, ROAMING, INTERNATIONAL, SUBSCRIPTION, USAGE
    }

    public enum RatingType {
        TIME_BASED,      // Per second/minute
        VOLUME_BASED,    // Per MB/GB
        FLAT_RATE,       // Fixed charge
        TIERED,          // Volume tiers
        DESTINATION      // Based on destination
    }

    /**
     * Check if this rule is currently effective
     */
    public boolean isEffective() {
        Instant now = Instant.now();
        boolean afterStart = effectiveFrom == null || !now.isBefore(effectiveFrom);
        boolean beforeEnd = effectiveTo == null || now.isBefore(effectiveTo);
        return isActive && afterStart && beforeEnd;
    }
}
