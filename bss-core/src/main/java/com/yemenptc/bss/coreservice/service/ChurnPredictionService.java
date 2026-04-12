package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ChurnPrediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChurnPredictionService {

    @Transactional
    public ChurnPrediction predictChurn(String customerId) {
        log.info("Running churn prediction for customer: {}", customerId);

        double probability = calculateChurnProbability(customerId);
        BigDecimal churnProb = BigDecimal.valueOf(probability).setScale(4, RoundingMode.HALF_UP);

        ChurnPrediction prediction = ChurnPrediction.builder()
            .customerId(customerId)
            .modelVersion("v2.1.0")
            .churnProbability(churnProb)
            .riskLevel(determineRiskLevel(churnProb))
            .predictionDate(LocalDateTime.now())
            .predictionExpiry(LocalDateTime.now().plusDays(30))
            .confidenceScore(BigDecimal.valueOf(85 + new Random().nextInt(10)))
            .contributingFactors(generateContributingFactors(customerId))
            .recommendedActions(generateRecommendations(churnProb))
            .status(ChurnPrediction.PredictionStatus.ACTIVE)
            .build();

        log.info("Churn prediction completed: {} - probability: {}", customerId, churnProb);
        return prediction;
    }

    @Transactional(readOnly = true)
    public ChurnPrediction getPrediction(String customerId) {
        log.info("Retrieving churn prediction for customer: {}", customerId);
        
        return ChurnPrediction.builder()
            .customerId(customerId)
            .modelVersion("v2.1.0")
            .churnProbability(BigDecimal.valueOf(0.45))
            .riskLevel(ChurnPrediction.RiskLevel.MEDIUM)
            .predictionDate(LocalDateTime.now().minusDays(5))
            .predictionExpiry(LocalDateTime.now().plusDays(25))
            .confidenceScore(BigDecimal.valueOf(87.5))
            .contributingFactors("{\"factors\": [\"Usage decline\", \"Support tickets\", \"Payment delays\"]}")
            .recommendedActions("{\"actions\": [\"Offer discount\", \"Personalized retention call\", \"Loyalty points\"]}")
            .status(ChurnPrediction.PredictionStatus.ACTIVE)
            .build();
    }

    @Transactional(readOnly = true)
    public List<ChurnPrediction> getHighRiskCustomers(int limit) {
        log.info("Fetching high risk customers, limit: {}", limit);
        
        List<ChurnPrediction> predictions = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            predictions.add(ChurnPrediction.builder()
                .customerId("CUST-" + String.format("%05d", i + 1))
                .churnProbability(BigDecimal.valueOf(0.7 + new Random().nextDouble() * 0.25))
                .riskLevel(ChurnPrediction.RiskLevel.HIGH)
                .predictionDate(LocalDateTime.now())
                .confidenceScore(BigDecimal.valueOf(80 + new Random().nextInt(15)))
                .build());
        }
        
        predictions.sort((a, b) -> b.getChurnProbability().compareTo(a.getChurnProbability()));
        return predictions;
    }

    @Transactional
    public Map<String, Object> getChurnAnalytics() {
        log.info("Generating churn analytics");
        
        return Map.of(
            "totalPredictions", 15000,
            "criticalRisk", 250,
            "highRisk", 850,
            "mediumRisk", 2200,
            "lowRisk", 11700,
            "averageChurnProbability", 0.32,
            "modelAccuracy", 0.87,
            "retentionSuccessRate", 0.65,
            "period", "LAST_30_DAYS"
        );
    }

    @Transactional
    public ChurnPrediction triggerRetentionAction(String customerId, String actionType) {
        log.info("Triggering retention action for {}: {}", customerId, actionType);

        ChurnPrediction prediction = getPrediction(customerId);
        prediction.setStatus(ChurnPrediction.PredictionStatus.ACTION_TAKEN);
        
        return prediction;
    }

    private double calculateChurnProbability(String customerId) {
        double baseProbability = 0.15;
        
        int hash = customerId.hashCode();
        double variance = (hash % 100) / 100.0;
        
        return Math.min(0.95, baseProbability + (variance * 0.6));
    }

    private ChurnPrediction.RiskLevel determineRiskLevel(BigDecimal probability) {
        double prob = probability.doubleValue();
        if (prob >= 0.7) return ChurnPrediction.RiskLevel.CRITICAL;
        if (prob >= 0.5) return ChurnPrediction.RiskLevel.HIGH;
        if (prob >= 0.3) return ChurnPrediction.RiskLevel.MEDIUM;
        return ChurnPrediction.RiskLevel.LOW;
    }

    private String generateContributingFactors(String customerId) {
        List<String> factors = Arrays.asList(
            "Usage decline over last 90 days",
            "Multiple support tickets in last month",
            "Payment behavior change detected",
            "Reduced engagement score",
            "Competitive offer exposure"
        );
        
        return "{\"factors\": " + factors.subList(0, 2 + Math.abs(customerId.hashCode()) % 3) + "}";
    }

    private String generateRecommendations(BigDecimal probability) {
        List<String> actions = new ArrayList<>();
        
        if (probability.doubleValue() > 0.7) {
            actions.add("Immediate retention team contact");
            actions.add("Offer personalized discount (15-20%)");
            actions.add("Priority customer support access");
        } else if (probability.doubleValue() > 0.5) {
            actions.add("Proactive customer outreach");
            actions.add("Loyalty points bonus");
            actions.add("Service upgrade offer");
        } else {
            actions.add("Monitor closely");
            actions.add("Share useful content");
        }
        
        return "{\"actions\": " + actions + "}";
    }
}