package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class FraudAlert extends BaseTmfEntity {

    @Column(name = "alert_id", unique = true, length = 50)
    private String alertId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 30)
    private FraudAlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private FraudSeverity severity = FraudSeverity.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AlertStatus status = AlertStatus.NEW;

    @Column(name = "account_id", length = 50)
    private String accountId;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "subscription_id", length = 50)
    private String subscriptionId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rule_id", length = 50)
    private String ruleId;

    @Column(name = "rule_name", length = 100)
    private String ruleName;

    @Column(name = "trigger_value", precision = 20, scale = 4)
    private BigDecimal triggerValue;

    @Column(name = "threshold_value", precision = 20, scale = 4)
    private BigDecimal thresholdValue;

    @Column(name = "detection_time")
    private LocalDateTime detectionTime;

    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    @Column(name = "resolution", length = 50)
    private String resolution;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "action_taken", length = 100)
    private String actionTaken;

    @Column(name = "false_positive")
    private Boolean falsePositive = false;

    public enum FraudAlertType {
        USAGE_SPIKE, UNUSUAL_PATTERN, ROAMING_ABUSE, BILLING_ANOMALY, 
        SIM_SWAP, IDENTITY_MISMATCH, MULTIPLE_FAILED_AUTH, PREMIUM_SERVICE_ABUSE
    }

    public enum FraudSeverity {
        CRITICAL, HIGH, MEDIUM, LOW
    }

    public enum AlertStatus {
        NEW, UNDER_REVIEW, CONFIRMED, ESCALATED, RESOLVED, FALSE_POSITIVE
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/fraudManagement/v5/alert";
    }
}