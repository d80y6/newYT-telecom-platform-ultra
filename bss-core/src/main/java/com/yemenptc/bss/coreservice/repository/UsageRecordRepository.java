package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.UsageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, UUID> {

    Page<UsageRecord> findByAccountId(String accountId, Pageable pageable);

    Page<UsageRecord> findByServiceId(String serviceId, Pageable pageable);

    Page<UsageRecord> findByUsageType(UsageRecord.UsageType usageType, Pageable pageable);

    Page<UsageRecord> findByStatus(UsageRecord.UsageStatus status, Pageable pageable);

    List<UsageRecord> findByAccountIdAndUsageStartDateBetween(
            String accountId, LocalDateTime start, LocalDateTime end);

    long countByAccountIdAndStatus(String accountId, UsageRecord.UsageStatus status);
}