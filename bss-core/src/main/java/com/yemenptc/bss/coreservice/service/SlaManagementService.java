package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.SlaContract;
import com.yemenptc.bss.coreservice.entity.SlaContract.SlaStatus;
import com.yemenptc.bss.coreservice.entity.SlaThreshold;
import com.yemenptc.bss.coreservice.entity.SlaViolation;
import com.yemenptc.bss.coreservice.entity.SlaViolation.ViolationStatus;
import com.yemenptc.bss.coreservice.repository.SlaContractRepository;
import com.yemenptc.bss.coreservice.repository.SlaThresholdRepository;
import com.yemenptc.bss.coreservice.repository.SlaViolationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlaManagementService {

    private final SlaContractRepository slaContractRepository;
    private final SlaThresholdRepository slaThresholdRepository;
    private final SlaViolationRepository slaViolationRepository;

    @Transactional
    public SlaContract createSlaContract(SlaContract contract) {
        contract.setStatus(SlaStatus.DRAFT);
        
        log.info("Creating SLA contract: {}", contract.getSlaName());
        return slaContractRepository.save(contract);
    }

    @Transactional
    public SlaContract activateSlaContract(String contractId) {
        SlaContract contract = slaContractRepository.findById(UUID.fromString(contractId))
            .orElseThrow(() -> new RuntimeException("SLA Contract not found: " + contractId));
        
        contract.setStatus(SlaStatus.ACTIVE);
        
        log.info("Activating SLA contract: {}", contractId);
        return slaContractRepository.save(contract);
    }

    @Transactional
    public SlaContract suspendSlaContract(String contractId) {
        SlaContract contract = slaContractRepository.findById(UUID.fromString(contractId))
            .orElseThrow(() -> new RuntimeException("SLA Contract not found: " + contractId));
        
        contract.setStatus(SlaStatus.SUSPENDED);
        
        log.info("Suspending SLA contract: {}", contractId);
        return slaContractRepository.save(contract);
    }

    @Transactional
    public SlaContract renewSlaContract(String contractId) {
        SlaContract contract = slaContractRepository.findById(UUID.fromString(contractId))
            .orElseThrow(() -> new RuntimeException("SLA Contract not found: " + contractId));
        
        contract.setStatus(SlaStatus.ACTIVE);
        
        if (contract.getEndDate() != null) {
            contract.setStartDate(contract.getEndDate().plus(1, ChronoUnit.DAYS));
            contract.setEndDate(contract.getStartDate().plus(1, ChronoUnit.YEARS));
        }
        
        log.info("Renewing SLA contract: {}", contractId);
        return slaContractRepository.save(contract);
    }

    public List<SlaContract> getSlaContractsByCustomer(String customerId) {
        return slaContractRepository.findByCustomerId(customerId);
    }

    public List<SlaContract> getSlaContractsByService(String serviceId) {
        return slaContractRepository.findByServiceId(serviceId);
    }

    public List<SlaContract> getActiveSlaContracts() {
        return slaContractRepository.findByStatus(SlaStatus.ACTIVE);
    }

    public List<SlaContract> getExpiringContracts(int daysAhead) {
        Instant start = Instant.now();
        Instant end = start.plus(daysAhead, ChronoUnit.DAYS);
        return slaContractRepository.findByStatusAndExpiringBetween(SlaStatus.ACTIVE, start, end);
    }

    public SlaContract getSlaContract(String contractId) {
        return slaContractRepository.findById(UUID.fromString(contractId))
            .orElseThrow(() -> new RuntimeException("SLA Contract not found: " + contractId));
    }

    @Transactional
    public SlaThreshold createThreshold(SlaThreshold threshold) {
        log.info("Creating SLA threshold for metric: {}", threshold.getMetricName());
        return slaThresholdRepository.save(threshold);
    }

    public List<SlaThreshold> getThresholdsByMetric(String metricName) {
        return slaThresholdRepository.findByMetricName(metricName);
    }

    public List<SlaThreshold> getThresholdsBySla(String slaName) {
        return slaThresholdRepository.findBySlaName(slaName);
    }

    @Transactional
    public SlaViolation recordViolation(SlaViolation violation) {
        violation.setStatus(ViolationStatus.OPEN);
        
        log.warn("SLA violation recorded: {} for contract {}", 
            violation.getMetricName(), violation.getSlaContract().getId());
        
        SlaViolation saved = slaViolationRepository.save(violation);
        
        applyCreditToContract(violation.getSlaContract().getId().toString(), violation.getCreditApplied());
        
        return saved;
    }

    private void applyCreditToContract(String contractId, Double creditAmount) {
        if (creditAmount == null || creditAmount <= 0) return;
        
        slaContractRepository.findById(UUID.fromString(contractId)).ifPresent(contract -> {
            Double current = contract.getTotalCreditsApplied() != null ? contract.getTotalCreditsApplied() : 0.0;
            contract.setTotalCreditsApplied(current + creditAmount);
            slaContractRepository.save(contract);
        });
    }

    @Transactional
    public SlaViolation acknowledgeViolation(String violationId, String acknowledgedBy) {
        SlaViolation violation = slaViolationRepository.findById(UUID.fromString(violationId))
            .orElseThrow(() -> new RuntimeException("Violation not found: " + violationId));
        
        violation.setStatus(ViolationStatus.ACKNOWLEDGED);
        violation.setAcknowledged(true);
        violation.setAcknowledgedBy(acknowledgedBy);
        violation.setAcknowledgedDate(Instant.now());
        
        log.info("Violation {} acknowledged by {}", violationId, acknowledgedBy);
        return slaViolationRepository.save(violation);
    }

    @Transactional
    public SlaViolation resolveViolation(String violationId, String resolutionNotes) {
        SlaViolation violation = slaViolationRepository.findById(UUID.fromString(violationId))
            .orElseThrow(() -> new RuntimeException("Violation not found: " + violationId));
        
        violation.setStatus(ViolationStatus.RESOLVED);
        violation.setResolutionNotes(resolutionNotes);
        
        log.info("Violation {} resolved", violationId);
        return slaViolationRepository.save(violation);
    }

    public List<SlaViolation> getViolationsByContract(String contractId) {
        return slaViolationRepository.findBySlaContractId(contractId);
    }

    public List<SlaViolation> getOpenViolations() {
        return slaViolationRepository.findByStatus(ViolationStatus.OPEN);
    }

    public List<SlaViolation> getViolationsBySeverity(String severity) {
        return slaViolationRepository.findOpenViolationsBySeverity(severity);
    }

    public int getOpenViolationCount(String contractId) {
        return slaViolationRepository.countOpenViolationsByContract(contractId);
    }

    @Transactional
    public SlaContract updateSlaContract(SlaContract contract) {
        return slaContractRepository.save(contract);
    }

    @Transactional
    public void deleteSlaContract(String contractId) {
        slaContractRepository.deleteById(UUID.fromString(contractId));
        log.info("Deleted SLA contract: {}", contractId);
    }
}
