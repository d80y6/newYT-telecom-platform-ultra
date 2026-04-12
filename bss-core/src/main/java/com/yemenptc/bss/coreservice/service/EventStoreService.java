package com.yemenptc.bss.coreservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yemenptc.bss.coreservice.entity.AuditEvent;
import com.yemenptc.bss.coreservice.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventStoreService {

    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;
    private final Optional<KafkaTemplate<String, String>> kafkaTemplate;

    private static final String AUDIT_TOPIC = "bss-audit-events";
    private static final int RETENTION_YEARS = 7;

    public AuditEvent logEvent(AuditEvent event) {
        log.info("Logging audit event: type={}, entity={}, action={}",
            event.getEventType(), event.getEntityType(), event.getAction());

        AuditEvent saved = auditEventRepository.save(event);

        kafkaTemplate.ifPresent(template -> {
            try {
                String eventJson = objectMapper.writeValueAsString(saved);
                template.send(AUDIT_TOPIC, event.getEventId(), eventJson);
                log.debug("Published audit event to Kafka: {}", event.getEventId());
            } catch (Exception e) {
                log.error("Failed to publish audit event to Kafka: {}", e.getMessage());
            }
        });

        return saved;
    }

    public AuditEvent logStateChange(String entityType, UUID entityId, String action,
                                       Object previousState, Object newState,
                                       UUID userId, String username, String ipAddress) {
        try {
            AuditEvent event = AuditEvent.builder()
                .eventType("STATE_CHANGE")
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .previousState(previousState != null ? objectMapper.writeValueAsString(previousState) : null)
                .newState(newState != null ? objectMapper.writeValueAsString(newState) : null)
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .serviceName("bss-core")
                .success(true)
                .build();

            return logEvent(event);
        } catch (Exception e) {
            log.error("Failed to log state change event: {}", e.getMessage());
            throw new RuntimeException("Failed to log audit event", e);
        }
    }

    public AuditEvent logAction(String entityType, UUID entityId, String action,
                                 UUID userId, String username, String ipAddress,
                                 boolean success, String errorMessage) {
        AuditEvent event = AuditEvent.builder()
            .eventType("ACTION")
            .entityType(entityType)
            .entityId(entityId)
            .action(action)
            .userId(userId)
            .username(username)
            .ipAddress(ipAddress)
            .serviceName("bss-core")
            .success(success)
            .errorMessage(errorMessage)
            .build();

        return logEvent(event);
    }

    public Page<AuditEvent> getEntityHistory(String entityType, UUID entityId, Pageable pageable) {
        return auditEventRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
    }

    public Page<AuditEvent> getUserActivity(UUID userId, Pageable pageable) {
        return auditEventRepository.findByUserId(userId, pageable);
    }

    public List<AuditEvent> getEventsByCorrelationId(String correlationId) {
        return auditEventRepository.findByCorrelationId(correlationId);
    }

    public Page<AuditEvent> getEventsByTimeRange(Instant start, Instant end, Pageable pageable) {
        return auditEventRepository.findByTimestampBetween(start, end, pageable);
    }

    public List<AuditEvent> getRecentEntityEvents(String entityType, UUID entityId, int hours) {
        Instant since = Instant.now().minus(hours, ChronoUnit.HOURS);
        return auditEventRepository.findRecentEventsForEntity(entityType, entityId, since);
    }

    public void applyRetentionPolicy() {
        Instant cutoff = Instant.now().minus(RETENTION_YEARS, ChronoUnit.YEARS);
        long count = auditEventRepository.countEventsBeforeCutoff(cutoff);

        if (count > 0) {
            log.info("Found {} audit events older than {} years for archival",
                count, RETENTION_YEARS);
        }
    }
}
