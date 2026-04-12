package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.FraudAlert;
import com.yemenptc.bss.coreservice.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudAlertRepository fraudAlertRepository;

    @Transactional
    public FraudAlert createAlert(FraudAlert request) {
        log.info("Creating fraud alert: type={}, severity={}", request.getAlertType(), request.getSeverity());

        FraudAlert alert = FraudAlert.builder()
            .alertId("FRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .alertType(request.getAlertType())
            .severity(determineSeverity(request.getAlertType(), request.getTriggerValue()))
            .status(FraudAlert.AlertStatus.NEW)
            .accountId(request.getAccountId())
            .customerId(request.getCustomerId())
            .subscriptionId(request.getSubscriptionId())
            .description(request.getDescription())
            .ruleId(request.getRuleId())
            .ruleName(request.getRuleName())
            .triggerValue(request.getTriggerValue())
            .thresholdValue(request.getThresholdValue())
            .detectionTime(LocalDateTime.now())
            .build();

        FraudAlert saved = fraudAlertRepository.save(alert);
        log.info("Fraud alert created: {}", saved.getAlertId());
        return saved;
    }

    @Transactional(readOnly = true)
    public FraudAlert getAlert(UUID id) {
        return fraudAlertRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Fraud alert not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<FraudAlert> listAlerts(Pageable pageable) {
        return fraudAlertRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<FraudAlert> findByStatus(FraudAlert.AlertStatus status, Pageable pageable) {
        return fraudAlertRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<FraudAlert> findBySeverity(FraudAlert.FraudSeverity severity, Pageable pageable) {
        return fraudAlertRepository.findBySeverity(severity, pageable);
    }

    @Transactional
    public FraudAlert reviewAlert(UUID id, String reviewedBy, String resolution, String notes) {
        log.info("Reviewing fraud alert {} by {}", id, reviewedBy);
        
        FraudAlert alert = getAlert(id);
        alert.setStatus(FraudAlert.AlertStatus.UNDER_REVIEW);
        alert.setReviewedBy(reviewedBy);
        alert.setReviewTime(LocalDateTime.now());
        alert.setResolution(resolution);
        alert.setResolutionNotes(notes);
        
        return fraudAlertRepository.save(alert);
    }

    @Transactional
    public FraudAlert confirmAlert(UUID id, String actionTaken) {
        log.info("Confirming fraud alert {}", id);
        
        FraudAlert alert = getAlert(id);
        alert.setStatus(FraudAlert.AlertStatus.CONFIRMED);
        alert.setActionTaken(actionTaken);
        
        return fraudAlertRepository.save(alert);
    }

    @Transactional
    public FraudAlert escalateAlert(UUID id) {
        log.info("Escalating fraud alert {}", id);
        
        FraudAlert alert = getAlert(id);
        alert.setStatus(FraudAlert.AlertStatus.ESCALATED);
        
        if (alert.getSeverity() == FraudAlert.FraudSeverity.LOW) {
            alert.setSeverity(FraudAlert.FraudSeverity.MEDIUM);
        } else if (alert.getSeverity() == FraudAlert.FraudSeverity.MEDIUM) {
            alert.setSeverity(FraudAlert.FraudSeverity.HIGH);
        }
        
        return fraudAlertRepository.save(alert);
    }

    @Transactional
    public FraudAlert resolveAlert(UUID id, String resolution, String notes) {
        log.info("Resolving fraud alert {}", id);
        
        FraudAlert alert = getAlert(id);
        alert.setStatus(FraudAlert.AlertStatus.RESOLVED);
        alert.setResolution(resolution);
        alert.setResolutionNotes(notes);
        
        return fraudAlertRepository.save(alert);
    }

    @Transactional
    public FraudAlert markFalsePositive(UUID id, String notes) {
        log.info("Marking fraud alert {} as false positive", id);
        
        FraudAlert alert = getAlert(id);
        alert.setStatus(FraudAlert.AlertStatus.FALSE_POSITIVE);
        alert.setFalsePositive(true);
        alert.setResolutionNotes(notes);
        
        return fraudAlertRepository.save(alert);
    }

    @Transactional
    public boolean checkUsageSpike(String accountId, BigDecimal currentUsage, BigDecimal threshold) {
        if (currentUsage.compareTo(threshold) > 0) {
            createAlert(FraudAlert.builder()
                .alertType(FraudAlert.FraudAlertType.USAGE_SPIKE)
                .accountId(accountId)
                .description("Usage spike detected: " + currentUsage + " exceeds threshold " + threshold)
                .triggerValue(currentUsage)
                .thresholdValue(threshold)
                .ruleId("RULE_USAGE_SPIKE")
                .ruleName("Usage Spike Detection")
                .build());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean checkUnusualPattern(String accountId, int failedAttempts, int threshold) {
        if (failedAttempts >= threshold) {
            createAlert(FraudAlert.builder()
                .alertType(FraudAlert.FraudAlertType.MULTIPLE_FAILED_AUTH)
                .accountId(accountId)
                .description("Multiple failed authentication attempts: " + failedAttempts)
                .triggerValue(BigDecimal.valueOf(failedAttempts))
                .thresholdValue(BigDecimal.valueOf(threshold))
                .ruleId("RULE_AUTH_FAILURES")
                .ruleName("Failed Auth Pattern Detection")
                .build());
            return true;
        }
        return false;
    }

    private FraudAlert.FraudSeverity determineSeverity(FraudAlert.FraudAlertType alertType, BigDecimal triggerValue) {
        if (triggerValue == null) return FraudAlert.FraudSeverity.MEDIUM;
        
        return switch (alertType) {
            case SIM_SWAP, IDENTITY_MISMATCH -> FraudAlert.FraudSeverity.CRITICAL;
            case USAGE_SPIKE -> triggerValue.compareTo(BigDecimal.valueOf(10000)) > 0 ? 
                FraudAlert.FraudSeverity.HIGH : FraudAlert.FraudSeverity.MEDIUM;
            case ROAMING_ABUSE -> triggerValue.compareTo(BigDecimal.valueOf(5000)) > 0 ? 
                FraudAlert.FraudSeverity.HIGH : FraudAlert.FraudSeverity.MEDIUM;
            default -> FraudAlert.FraudSeverity.MEDIUM;
        };
    }
}