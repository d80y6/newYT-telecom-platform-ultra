package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AnalyticsMetric extends BaseTmfEntity {

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_category", nullable = false, length = 50)
    private String metricCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_source", length = 30)
    private DataSource dataSource;

    @Column(name = "account_id", length = 50)
    private String accountId;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    @Column(name = "value", precision = 20, scale = 4)
    private BigDecimal value;

    @Column(name = "previous_value", precision = 20, scale = 4)
    private BigDecimal previousValue;

    @Column(name = "change_percent", precision = 8, scale = 2)
    private BigDecimal changePercent;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "aggregation_type", length = 30)
    private String aggregationType;

    @Column(name = "dimensions", columnDefinition = "TEXT")
    private String dimensions;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    public enum DataSource {
        BILLING, USAGE, NETWORK, CUSTOMER, TRANSACTION
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/analytics/v5/metric";
    }
}