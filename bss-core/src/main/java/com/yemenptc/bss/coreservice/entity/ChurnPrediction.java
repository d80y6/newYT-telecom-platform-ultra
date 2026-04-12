package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "churn_predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChurnPrediction extends BaseTmfEntity {

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Column(name = "model_version", length = 20)
    private String modelVersion;

    @Column(name = "churn_probability", precision = 5, scale = 4)
    private BigDecimal churnProbability;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    @Column(name = "prediction_date")
    private LocalDateTime predictionDate;

    @Column(name = "prediction_expiry")
    private LocalDateTime predictionExpiry;

    @Column(name = "contributing_factors", columnDefinition = "TEXT")
    private String contributingFactors;

    @Column(name = "recommended_actions", columnDefinition = "TEXT")
    private String recommendedActions;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "last_interaction_score")
    private Integer lastInteractionScore;

    @Column(name = "usage_decline_percent", precision = 5, scale = 2)
    private BigDecimal usageDeclinePercent;

    @Column(name = "support_ticket_count")
    private Integer supportTicketCount;

    @Column(name = "days_since_last_recharge")
    private Integer daysSinceLastRecharge;

    @Column(name = "arpu_trend", length = 10)
    private String arpuTrend;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PredictionStatus status;

    public enum RiskLevel {
        CRITICAL, HIGH, MEDIUM, LOW
    }

    public enum PredictionStatus {
        ACTIVE, EXPIRED, ACTION_TAKEN
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/mlModels/v5/churnPrediction";
    }
}