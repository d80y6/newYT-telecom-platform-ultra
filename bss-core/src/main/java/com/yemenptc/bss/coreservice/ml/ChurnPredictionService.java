package com.yemenptc.bss.coreservice.ml;

import com.yemenptc.bss.coreservice.entity.Customer;
import com.yemenptc.bss.coreservice.entity.UsageRecord;
import com.yemenptc.bss.coreservice.entity.TroubleTicket;
import com.yemenptc.bss.coreservice.repository.CustomerRepository;
import com.yemenptc.bss.coreservice.repository.UsageRecordRepository;
import com.yemenptc.bss.coreservice.repository.TroubleTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * ML Churn Prediction Service
 * 
 * Implements machine learning pipeline for customer churn prediction
 * enhanced for Phase 2 with production-grade feature engineering.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChurnPredictionService {

    private final CustomerRepository customerRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final TroubleTicketRepository troubleTicketRepository;

    /**
     * Run churn prediction daily at 2 AM
     * Updates churn risk scores for all active customers
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void runDailyChurnPrediction() {
        log.info("Starting daily churn prediction job");
        
        List<Customer> activeCustomers = customerRepository
            .findByStatus(Customer.CustomerStatus.ACTIVE, PageRequest.of(0, 10000))
            .getContent();
        
        int processed = 0;
        for (Customer customer : activeCustomers) {
            try {
                BigDecimal riskScore = calculateChurnRisk(customer);
                updateChurnRiskScore(customer, riskScore);
                processed++;
                
                // Log high-risk customers
                if (riskScore.compareTo(new BigDecimal("0.7")) >= 0) {
                    log.warn("High churn risk detected: customer={}, score={}", 
                        customer.getId(), riskScore);
                }
            } catch (Exception e) {
                log.error("Error calculating churn risk for customer {}: {}", 
                    customer.getId(), e.getMessage());
            }
        }
        
        log.info("Churn prediction completed: {} customers processed", processed);
    }

    /**
     * Calculate churn risk score for a customer
     * 
     * @param customer Customer to analyze
     * @return Risk score between 0.0 (low risk) and 1.0 (high risk)
     */
    public BigDecimal calculateChurnRisk(Customer customer) {
        log.debug("Calculating churn risk for customer: {}", customer.getId());
        
        // Feature 1: Usage decline (weight: 0.35)
        BigDecimal usageDecline = calculateUsageDecline(customer);
        
        // Feature 2: Payment issues (weight: 0.25)
        BigDecimal paymentRisk = calculatePaymentRisk(customer);
        
        // Feature 3: Service issues (weight: 0.20)
        BigDecimal serviceRisk = calculateServiceRisk(customer);
        
        // Feature 4: Account age factor (weight: 0.20)
        BigDecimal accountAgeRisk = calculateAccountAgeRisk(customer);
        
        // Weighted sum
        BigDecimal totalRisk = usageDecline
            .multiply(new BigDecimal("0.35"))
            .add(paymentRisk.multiply(new BigDecimal("0.25")))
            .add(serviceRisk.multiply(new BigDecimal("0.20")))
            .add(accountAgeRisk.multiply(new BigDecimal("0.20")));
        
        // Normalize to 0-1 range
        return totalRisk.min(BigDecimal.ONE).max(BigDecimal.ZERO)
            .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Calculate usage decline factor
     * Compares usage in last 30 days vs previous 30 days
     * 
     * @param customer Customer to analyze
     * @return Risk factor 0.0-1.0
     */
    private BigDecimal calculateUsageDecline(Customer customer) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime thirtyDaysAgo = now.minus(30, ChronoUnit.DAYS);
            LocalDateTime sixtyDaysAgo = now.minus(60, ChronoUnit.DAYS);
            
            // Get usage records for last 30 days
            List<UsageRecord> recentUsage = usageRecordRepository
                .findByAccountIdAndUsageStartDateBetween(
                    customer.getId().toString(), thirtyDaysAgo, now);
            
            // Get usage records for previous 30 days
            List<UsageRecord> previousUsage = usageRecordRepository
                .findByAccountIdAndUsageStartDateBetween(
                    customer.getId().toString(), sixtyDaysAgo, thirtyDaysAgo);
            
            // Calculate total usage (weighted by type)
            BigDecimal recentVolume = calculateWeightedUsage(recentUsage);
            BigDecimal previousVolume = calculateWeightedUsage(previousUsage);
            
            if (previousVolume.compareTo(BigDecimal.ZERO) <= 0) {
                // New customer or no previous usage
                return new BigDecimal("0.3");
            }
            
            BigDecimal declineRatio = BigDecimal.ONE.subtract(
                recentVolume.divide(previousVolume, 4, RoundingMode.HALF_UP));
            
            // If usage increased, low risk
            if (declineRatio.compareTo(BigDecimal.ZERO) < 0) {
                return BigDecimal.ZERO;
            }
            
            // Normalize decline to risk score (50% decline = 100% risk)
            return declineRatio.multiply(new BigDecimal("2.0")).min(BigDecimal.ONE);
            
        } catch (Exception e) {
            log.warn("Error calculating usage decline for customer {}: {}", 
                customer.getId(), e.getMessage());
            return new BigDecimal("0.5");  // Unknown = medium risk
        }
    }

    /**
     * Calculate weighted usage volume
     * 
     * @param usageRecords List of usage records
     * @return Weighted usage total
     */
    private BigDecimal calculateWeightedUsage(List<UsageRecord> usageRecords) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (UsageRecord record : usageRecords) {
            switch (record.getUsageType()) {
                case VOICE:
                    // Voice: seconds * 0.01 (weight)
                    total = total.add(
                        BigDecimal.valueOf(record.getDurationSeconds() != null ? 
                            record.getDurationSeconds() : 0)
                        .multiply(new BigDecimal("0.01")));
                    break;
                case DATA:
                    // Data: MB * 1.0 (weight)
                    total = total.add(
                        record.getVolumeMb() != null ? record.getVolumeMb() : BigDecimal.ZERO);
                    break;
                case SMS:
                    // SMS: count * 0.5 (weight)
                    total = total.add(
                        BigDecimal.valueOf(record.getCount() != null ? 
                            record.getCount() : 0)
                        .multiply(new BigDecimal("0.5")));
                    break;
                default:
                    break;
            }
        }
        
        return total;
    }

    /**
     * Calculate payment risk factor
     * 
     * @param customer Customer to analyze
     * @return Risk factor 0.0-1.0
     */
    private BigDecimal calculatePaymentRisk(Customer customer) {
        BigDecimal outstandingBalance = customer.getOutstandingBalance();
        
        if (outstandingBalance == null || outstandingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;  // No outstanding balance = low risk
        }
        
        // Risk increases with outstanding balance
        BigDecimal creditLimit = customer.getCreditLimit() != null ? 
            customer.getCreditLimit() : new BigDecimal("1000");
        
        if (creditLimit.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("0.5");
        }
        
        BigDecimal utilization = outstandingBalance.divide(creditLimit, 4, RoundingMode.HALF_UP);
        
        // 100% utilization = 100% risk
        return utilization.min(BigDecimal.ONE);
    }

    /**
     * Calculate service risk factor
     * 
     * @param customer Customer to analyze
     * @return Risk factor 0.0-1.0
     */
    private BigDecimal calculateServiceRisk(Customer customer) {
        // Query trouble ticket system for this customer
        List<TroubleTicket> tickets = troubleTicketRepository.findByPartyId(customer.getId());

        if (tickets == null || tickets.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long recentTickets = tickets.stream()
            .filter(t -> t.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30).atZone(java.time.ZoneId.systemDefault()).toInstant()))
            .count();

        if (recentTickets == 0) return new BigDecimal("0.1");
        if (recentTickets == 1) return new BigDecimal("0.3");
        if (recentTickets < 3) return new BigDecimal("0.6");

        return BigDecimal.ONE; // 3+ tickets in 30 days = high service risk
    }
    /**
     * Calculate account age risk factor
     * 
     * @param customer Customer to analyze
     * @return Risk factor 0.0-1.0
     */
    private BigDecimal calculateAccountAgeRisk(Customer customer) {
        if (customer.getCreatedAt() == null) {
            return new BigDecimal("0.3");
        }
        
        long daysSinceCreation = ChronoUnit.DAYS.between(
            customer.getCreatedAt(), LocalDateTime.now());
        
        if (daysSinceCreation < 30) {
            // New customers have higher churn risk
            return new BigDecimal("0.7");
        } else if (daysSinceCreation < 90) {
            return new BigDecimal("0.4");
        } else if (daysSinceCreation < 365) {
            return new BigDecimal("0.2");
        } else {
            // Loyal customers have lower churn risk
            return new BigDecimal("0.1");
        }
    }

    /**
     * Update customer's churn risk score
     * 
     * @param customer Customer to update
     * @param riskScore New risk score
     */
    private void updateChurnRiskScore(Customer customer, BigDecimal riskScore) {
        customer.setChurnRiskScore(riskScore);
        
        // Update customer segment based on risk
        if (riskScore.compareTo(new BigDecimal("0.7")) >= 0) {
            customer.setSegments(List.of("HIGH_CHURN_RISK"));
        } else if (riskScore.compareTo(new BigDecimal("0.4")) >= 0) {
            customer.setSegments(List.of("MEDIUM_CHURN_RISK"));
        } else {
            customer.setSegments(List.of("LOW_CHURN_RISK"));
        }
        
        customerRepository.save(customer);
    }

    /**
     * Get high-risk customers for intervention
     * 
     * @return List of high-risk customers
     */
    public List<Customer> getHighRiskCustomers() {
        return customerRepository.findByChurnRiskScoreGreaterThanEqual(
            new BigDecimal("0.7"), PageRequest.of(0, 1000));
    }

    /**
     * Get churn statistics
     * 
     * @return Churn statistics
     */
    public Map<String, Object> getChurnStatistics() {
        long totalCustomers = customerRepository.count();
        long highRisk = customerRepository.countByChurnRiskScoreGreaterThanEqual(
            new BigDecimal("0.7"));
        long mediumRisk = customerRepository.countByChurnRiskScoreBetween(
            new BigDecimal("0.4"), new BigDecimal("0.7"));
        long lowRisk = customerRepository.countByChurnRiskScoreLessThan(
            new BigDecimal("0.4"));
        
        return Map.of(
            "totalCustomers", totalCustomers,
            "highRisk", highRisk,
            "mediumRisk", mediumRisk,
            "lowRisk", lowRisk,
            "highRiskPercentage", totalCustomers > 0 ? 
                (highRisk * 100.0 / totalCustomers) : 0.0
        );
    }
}
