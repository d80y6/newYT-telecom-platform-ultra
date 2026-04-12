package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "metrics", indexes = {
    @Index(name = "idx_metric_resource", columnList = "resourceId"),
    @Index(name = "idx_metric_name", columnList = "metricName"),
    @Index(name = "idx_metric_time", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Metric extends BaseTmfEntity {

    @Column(name = "resource_id", columnDefinition = "uuid")
    private java.util.UUID resourceId;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "metric_unit", length = 20)
    private String metricUnit;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "metric_type", length = 50)
    private String metricType;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Override
    protected String getApiPath() {
        return "/tmf-api/performanceManagement/v5/metric";
    }

    public enum MetricType { NETWORK, SYSTEM, APPLICATION, BUSINESS }
}
