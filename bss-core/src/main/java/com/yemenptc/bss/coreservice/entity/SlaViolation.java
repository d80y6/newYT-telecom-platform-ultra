package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "sla_violations", indexes = {
    @Index(name = "idx_violation_sla", columnList = "slaContractId"),
    @Index(name = "idx_violation_date", columnList = "violationDate"),
    @Index(name = "idx_violation_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlaViolation extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sla_contract_id", nullable = false)
    private SlaContract slaContract;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "violation_date", nullable = false)
    private Instant violationDate;

    @Column(name = "threshold_value")
    private Double thresholdValue;

    @Column(name = "actual_value")
    private Double actualValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private ViolationSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ViolationStatus status;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "credit_applied", precision = 12, scale = 2)
    private Double creditApplied;

    @Column(name = "credit_applied_date")
    private Instant creditAppliedDate;

    @Column(name = "resolution_notes", length = 500)
    private String resolutionNotes;

    @Column(name = "acknowledged")
    private Boolean acknowledged;

    @Column(name = "acknowledged_by", length = 100)
    private String acknowledgedBy;

    @Column(name = "acknowledged_date")
    private Instant acknowledgedDate;

    @Override
    protected String getApiPath() {
        return "/tmf-api/slaManagement/v5/slaViolation";
    }

    public enum ViolationSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum ViolationStatus {
        OPEN, ACKNOWLEDGED, RESOLVED, APPEALED, CLOSED
    }
}
