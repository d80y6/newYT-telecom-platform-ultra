package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscription_bundles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bundle_id", unique = true, nullable = false, length = 50)
    private String bundleId;

    @Column(name = "subscription_id", nullable = false, length = 36)
    private String subscriptionId;

    @Column(name = "plan_id", nullable = false, length = 50)
    private String planId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 30)
    private PricingRule.ServiceType serviceType;

    @Column(name = "bundle_name", nullable = false, length = 100)
    private String bundleName;

    @Column(name = "total_units", nullable = false, precision = 15, scale = 4)
    private BigDecimal totalUnits;

    @Column(name = "used_units", precision = 15, scale = 4)
    @Builder.Default
    private BigDecimal usedUnits = BigDecimal.ZERO;

    @Column(name = "remaining_units", precision = 15, scale = 4)
    private BigDecimal remainingUnits;

    @Column(name = "unit_type", nullable = false, length = 20)
    private String unitType;

    @Column(name = "reset_period", length = 20)
    private String resetPeriod;

    @Column(name = "last_reset_date")
    private Instant lastResetDate;

    @Column(name = "next_reset_date")
    private Instant nextResetDate;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "auto_renew")
    @Builder.Default
    private Boolean autoRenew = false;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Integer version;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (bundleId == null) bundleId = "BUNDLE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (remainingUnits == null) remainingUnits = totalUnits;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public BigDecimal calculateUsagePercentage() {
        if (totalUnits.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return usedUnits.multiply(BigDecimal.valueOf(100))
                .divide(totalUnits, 4, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal deductUnits(BigDecimal units) {
        if (units.compareTo(remainingUnits) > 0) {
            BigDecimal deducted = remainingUnits;
            remainingUnits = BigDecimal.ZERO;
            usedUnits = totalUnits;
            return deducted;
        }
        remainingUnits = remainingUnits.subtract(units);
        usedUnits = usedUnits.add(units);
        return units;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean needsReset() {
        if (nextResetDate == null) return false;
        return Instant.now().isAfter(nextResetDate);
    }

    public void performScheduledReset() {
        usedUnits = BigDecimal.ZERO;
        remainingUnits = totalUnits;
        lastResetDate = Instant.now();
        if ("MONTHLY".equals(resetPeriod)) {
            nextResetDate = lastResetDate.plusSeconds(30 * 24 * 60 * 60L);
        } else if ("WEEKLY".equals(resetPeriod)) {
            nextResetDate = lastResetDate.plusSeconds(7 * 24 * 60 * 60L);
        } else if ("DAILY".equals(resetPeriod)) {
            nextResetDate = lastResetDate.plusSeconds(24 * 60 * 60L);
        }
    }
}
