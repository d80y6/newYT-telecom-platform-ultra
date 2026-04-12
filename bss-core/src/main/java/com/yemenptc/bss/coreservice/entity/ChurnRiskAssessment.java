package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "churn_risk_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChurnRiskAssessment extends BaseTmfEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "churn_risk_score", precision = 5, scale = 2)
    private BigDecimal churnRiskScore;

    @Column(name = "risk_level")
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(name = "factors", columnDefinition = "TEXT")
    private String factors;

    @Column(name = "prediction_date")
    private LocalDateTime predictionDate;

    @Column(name = "recommended_actions", columnDefinition = "TEXT")
    private String recommendedActions;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/churnRisk";
    }
}