package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "charging_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingRule extends BaseTmfEntity {

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "rule_type", length = 50)
    private String ruleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "charging_type", length = 30)
    private ChargingType chargingType;

    @Column(name = "rate_amount", precision = 15, scale = 4)
    private BigDecimal rateAmount;

    @Column(name = "rate_unit", length = 20)
    private String rateUnit;

    @Column(name = "currency", length = 3)
    private String currency = "YER";

    @Column(name = "priority", length = 20)
    private String priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RuleStatus status = RuleStatus.ACTIVE;

    @Column(name = "service_type", length = 50)
    private String serviceType;

    @Column(name = "time_from")
    private LocalDateTime timeFrom;

    @Column(name = "time_to")
    private LocalDateTime timeTo;

    @Column(name = "day_of_week", length = 20)
    private String dayOfWeek;

    public enum ChargingType { FLAT_RATE, TIME_BASED, VOLUME_BASED, EVENT_BASED, TIERED }
    public enum RuleStatus { ACTIVE, INACTIVE, SUSPENDED }

    @Override
    protected String getApiPath() {
        return "/tmf-api/productCharging/v5/chargingRule";
    }
}