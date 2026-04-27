package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usage_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageEvent extends BaseTmfEntity {

    @Column(name = "event_id", unique = true, nullable = false, length = 100)
    private String eventId;

    @Column(name = "subscription_id", columnDefinition = "uuid")
    private UUID subscriptionId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "service_type", nullable = false, length = 30)
    private String serviceType;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "usage_value", precision = 15, scale = 4)
    private BigDecimal usageValue;

    @Column(name = "usage_unit", length = 20)
    private String usageUnit;

    @Column(name = "event_time")
    private Instant eventTime;

    @Column(name = "source_system", length = 50)
    private String sourceSystem;

    @Override
    protected String getApiPath() {
        return "/tmf-api/usageManagement/v5/usage";
    }
}
