package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PerformanceMetric extends BaseTmfEntity {

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "resource_id", length = 50)
    private String resourceId;

    @Column(name = "resource_type", length = 50)
    private String resourceType;

    @Column(name = "resource_name", length = 200)
    private String resourceName;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "ne_id", length = 100)
    private String neId;

    @Column(name = "collector_id", length = 50)
    private String collectorId;

    @Column(name = "collection_timestamp")
    private LocalDateTime collectionTimestamp;

    @Column(name = "metric_value", precision = 20, scale = 4)
    private BigDecimal metricValue;

    @Column(name = "unit", length = 30)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", length = 30)
    private MetricType metricType;

    @Column(name = "threshold_id", length = 50)
    private String thresholdId;

    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "location_id", length = 50)
    private String locationId;

    @Column(name = "region", length = 50)
    private String region;

    public enum MetricType {
        GAUGE, COUNTER, DERIVED, SYNTHETIC
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/performanceManagement/v5/performanceMetric";
    }
}