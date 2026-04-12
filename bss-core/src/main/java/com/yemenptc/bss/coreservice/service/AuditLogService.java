package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.AuditLog;
import com.yemenptc.bss.coreservice.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public AuditLog logAction(String principalId, String action, String resourceType, String resourceId,
                              String oldValue, String newValue, String ipAddress, String userAgent, String result) {
        AuditLog auditLog = AuditLog.builder()
            .principalId(principalId)
            .action(action)
            .resourceType(resourceType)
            .resourceId(resourceId)
            .oldValue(oldValue)
            .newValue(newValue)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .result(result)
            .timestamp(Instant.now())
            .retentionUntil(Instant.now().plusSeconds(7L * 365 * 24 * 60 * 60))
            .build();

        auditLog = auditLogRepository.save(auditLog);

        kafkaTemplate.send("audit.events", principalId, auditLog);

        return auditLog;
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByPrincipalId(String principalId, Pageable pageable) {
        return auditLogRepository.findByPrincipalId(principalId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByResource(String resourceId, Pageable pageable) {
        return auditLogRepository.findByResourceId(resourceId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByTimeRange(Instant start, Instant end, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(start, end, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredLogs() {
        auditLogRepository.deleteExpiredLogs(Instant.now());
        log.info("Expired audit logs cleaned up");
    }
}
