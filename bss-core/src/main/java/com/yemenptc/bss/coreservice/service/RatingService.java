package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.entity.RatingRecord;
import com.yemenptc.bss.coreservice.entity.SubscriptionBundle;
import com.yemenptc.bss.coreservice.rating.RatingEngine;
import com.yemenptc.bss.coreservice.repository.UsageEventRepository;
import com.yemenptc.bss.coreservice.repository.RatingRecordRepository;
import com.yemenptc.bss.coreservice.repository.SubscriptionBundleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {

    private final UsageEventRepository usageEventRepository;
    private final RatingRecordRepository ratingRecordRepository;
    private final SubscriptionBundleRepository subscriptionBundleRepository;
    private final RatingEngine ratingEngine;

    @Transactional
    public RatingRecord rateUsage(UsageEvent request) {
        log.info("Rating usage event: {} for subscription: {}", 
                request.getEventId(), request.getSubscriptionId());
        
        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(request);
        
        if (result.getBundleDeductions() != null) {
            for (RatingEngine.BundleDeduction deduction : result.getBundleDeductions()) {
                subscriptionBundleRepository.findByBundleId(deduction.getBundleId())
                        .ifPresent(bundle -> {
                            bundle.setRemainingUnits(deduction.getRemainingInBundle());
                            subscriptionBundleRepository.save(bundle);
                        });
            }
        }
        
        List<String> appliedRulesStr = result.getAppliedRules() != null ? 
                result.getAppliedRules() : new ArrayList<>();
        
        RatingRecord record = RatingRecord.builder()
                .eventId(result.getEventId())
                .subscriptionId(result.getSubscriptionId())
                .serviceType(result.getServiceType())
                .eventType(result.getEventType())
                .usageValue(result.getUsageValue())
                .usageUnit(request.getUsageUnit())
                .chargedAmount(result.getChargedAmount())
                .ratingStatus(result.getRatingStatus())
                .ratedAt(result.getRatedAt())
                .build();
        
        log.info("Rated event {} with charge {} {}", 
                result.getEventId(), result.getChargedAmount(), result.getCurrency());
        
        return ratingRecordRepository.save(record);
    }

    @Transactional
    public RatingRecord batchRate(List<UsageEvent> events) {
        log.info("Batch rating {} events", events.size());
        
        BigDecimal totalCharged = BigDecimal.ZERO;
        BigDecimal totalUnits = BigDecimal.ZERO;
        int successCount = 0;
        int failCount = 0;
        
        for (UsageEvent event : events) {
            try {
                RatingRecord record = rateUsage(event);
                totalCharged = totalCharged.add(record.getChargedAmount());
                if (record.getUsageValue() != null) {
                    totalUnits = totalUnits.add(record.getUsageValue());
                }
                successCount++;
            } catch (Exception e) {
                log.error("Failed to rate event {}: {}", event.getEventId(), e.getMessage());
                failCount++;
            }
        }
        
        String batchId = "BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        RatingRecord batchRecord = RatingRecord.builder()
                .eventId(batchId)
                .serviceType("BATCH")
                .eventType("BATCH_RATING")
                .usageValue(totalUnits)
                .usageUnit("VARIABLE")
                .chargedAmount(totalCharged)
                .ratingStatus(failCount == 0 ? RatingRecord.RatingStatus.RATED : 
                             (successCount > 0 ? RatingRecord.RatingStatus.PENDING : RatingRecord.RatingStatus.FAILED))
                .ratedAt(Instant.now())
                .build();
        
        log.info("Batch rating complete: {} succeeded, {} failed, total charge: {}", 
                successCount, failCount, totalCharged);
        
        return ratingRecordRepository.save(batchRecord);
    }

    @Transactional(readOnly = true)
    public RatingRecord getRatingRecord(UUID id) {
        return ratingRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating record not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<RatingRecord> listRatingRecords(Pageable pageable) {
        return ratingRecordRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<RatingRecord> getRatingsBySubscription(UUID subscriptionId) {
        return ratingRecordRepository.findBySubscriptionId(subscriptionId);
    }

    @Transactional(readOnly = true)
    public List<UsageEvent> getUsageEventsBySubscription(UUID subscriptionId) {
        return usageEventRepository.findBySubscriptionId(subscriptionId);
    }

    @Transactional(readOnly = true)
    public Page<UsageEvent> listUsageEvents(Pageable pageable) {
        return usageEventRepository.findAll(pageable);
    }

    @Transactional
    public UsageEvent createUsageEvent(UsageEvent request) {
        UsageEvent event = UsageEvent.builder()
                .eventId(request.getEventId())
                .subscriptionId(request.getSubscriptionId())
                .serviceType(request.getServiceType())
                .eventType(request.getEventType())
                .usageValue(request.getUsageValue())
                .usageUnit(request.getUsageUnit())
                .eventTime(request.getEventTime() != null ? request.getEventTime() : Instant.now())
                .sourceSystem(request.getSourceSystem())
                .build();
        return usageEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateEstimatedCharge(UsageEvent event) {
        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(event);
        return result.getChargedAmount();
    }
}
