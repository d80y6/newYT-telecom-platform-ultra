package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.UsageRecord;
import com.yemenptc.bss.coreservice.repository.UsageRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsageManagementService {

    private final UsageRecordRepository usageRecordRepository;

    @Transactional
    public UsageRecord recordUsage(UsageRecord request) {
        log.info("Recording usage: account={}, type={}", request.getAccountId(), request.getUsageType());

        UsageRecord record = UsageRecord.builder()
            .accountId(request.getAccountId())
            .serviceId(request.getServiceId())
            .subscriptionId(request.getSubscriptionId())
            .usageType(request.getUsageType())
            .status(UsageRecord.UsageStatus.COLLECTED)
            .usageStartDate(request.getUsageStartDate())
            .usageEndDate(request.getUsageEndDate())
            .durationSeconds(request.getDurationSeconds())
            .volumeBytes(request.getVolumeBytes())
            .direction(request.getDirection())
            .destination(request.getDestination())
            .origin(request.getOrigin())
            .productId(request.getProductId())
            .offeringId(request.getOfferingId())
            .cdrId(request.getCdrId())
            .networkElement(request.getNetworkElement())
            .locationId(request.getLocationId())
            .locationZone(request.getLocationZone())
            .currency("YER")
            .build();

        if (request.getVolumeBytes() != null) {
            record.setVolumeMb(BigDecimal.valueOf(request.getVolumeBytes()).divide(BigDecimal.valueOf(1024 * 1024), 2, java.math.RoundingMode.HALF_UP));
        }

        UsageRecord saved = usageRecordRepository.save(record);
        log.info("Usage recorded: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public UsageRecord getUsage(UUID id) {
        return usageRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usage record not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<UsageRecord> listUsageRecords(Pageable pageable) {
        return usageRecordRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<UsageRecord> getUsageByAccount(String accountId, Pageable pageable) {
        return usageRecordRepository.findByAccountId(accountId, pageable);
    }

    @Transactional(readOnly = true)
    public List<UsageRecord> getUsageByDateRange(String accountId, LocalDateTime start, LocalDateTime end) {
        return usageRecordRepository.findByAccountIdAndUsageStartDateBetween(accountId, start, end);
    }

    @Transactional
    public UsageRecord rateUsage(UUID id, BigDecimal amount) {
        log.info("Rating usage record: {}, amount={}", id, amount);
        
        UsageRecord record = getUsage(id);
        record.setRatedAmount(amount);
        record.setRatingTimestamp(LocalDateTime.now());
        record.setRatingStatus("RATED");
        record.setStatus(UsageRecord.UsageStatus.RATED);
        
        return usageRecordRepository.save(record);
    }

    @Transactional
    public UsageRecord updateStatus(UUID id, UsageRecord.UsageStatus status) {
        UsageRecord record = getUsage(id);
        record.setStatus(status);
        return usageRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public long countByAccount(String accountId) {
        return usageRecordRepository.countByAccountIdAndStatus(accountId, UsageRecord.UsageStatus.COLLECTED);
    }
}