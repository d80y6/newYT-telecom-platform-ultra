package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sla_thresholds", indexes = {
    @Index(name = "idx_sla_metric", columnList = "metricName")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlaThreshold extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sla_contract_id", nullable = false)
    private SlaContract slaContract;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "threshold_value", nullable = false)
    private Double thresholdValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 10)
    private ThresholdOperator operator;

    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "auto_credit_enabled")
    private Boolean autoCreditEnabled;

    @Column(name = "credit_amount")
    private Double creditAmount;

    @Column(name = "notification_template", length = 200)
    private String notificationTemplate;

    @Override
    protected String getApiPath() {
        return "/tmf-api/slaManagement/v5/slaThreshold";
    }

    public enum ThresholdOperator { GT, LT, GE, LE, EQ }
}
