package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rating_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingRecord extends BaseTmfEntity {

    @Column(name = "event_id", unique = true, nullable = false, length = 100)
    private String eventId;

    @Column(name = "subscription_id", columnDefinition = "uuid")
    private UUID subscriptionId;

    @Column(name = "service_type", nullable = false, length = 30)
    private String serviceType;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "usage_value", precision = 15, scale = 4)
    private BigDecimal usageValue;

    @Column(name = "usage_unit", length = 20)
    private String usageUnit;

    @Column(name = "charged_amount", precision = 15, scale = 2)
    private BigDecimal chargedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_status", nullable = false, length = 20)
    private RatingStatus ratingStatus = RatingStatus.PENDING;

    @Column(name = "rated_at")
    private Instant ratedAt;

    @Override
    protected String getApiPath() {
        return "/tmf-api/usageManagement/v5/rating";
    }

    public enum RatingStatus { PENDING, RATED, FAILED }
}
