package com.yemenptc.bss.coreservice.audit;

import com.yemenptc.bss.coreservice.entity.AuditEvent;
import com.yemenptc.bss.coreservice.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditEventRepository auditEventRepository;
    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;
    
    private static final String AUDIT_TOPIC = "audit.events";
    
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(AuditEventRequest request) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(request.getEventType())
                    .entityType(request.getEntityType())
                    .entityId(request.getEntityId() != null ? UUID.fromString(request.getEntityId()) : null)
                    .action(request.getAction())
                    .userId(request.getUserId() != null ? UUID.fromString(request.getUserId()) : null)
                    .username(request.getUserName())
                    .ipAddress(request.getIpAddress())
                    .previousState(request.getOldValue())
                    .newState(request.getNewValue())
                    .success(request.getStatus() == AuditEvent.EventStatus.SUCCESS)
                    .build();
            
            auditEventRepository.save(event);
            
            try {
                kafkaTemplate.send(AUDIT_TOPIC, event.getEventId(), event);
            } catch (Exception e) {
                log.warn("Failed to send audit event to Kafka: {}", e.getMessage());
            }
            
            log.debug("Audit event logged: {} {} {} by {}", 
                    request.getAction(), request.getEntityType(), request.getEntityId(), request.getUserId());
            
        } catch (Exception e) {
            log.error("Failed to log audit event: {}", e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public List<AuditEvent> getEventsByEntity(String entityType, String entityId) {
        return auditEventRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, UUID.fromString(entityId));
    }
    
    @Transactional(readOnly = true)
    public List<AuditEvent> getEventsByUser(String userId) {
        return auditEventRepository.findByUserIdOrderByTimestampDesc(UUID.fromString(userId));
    }
    
    @Transactional(readOnly = true)
    public List<AuditEvent> getEventsByType(String eventType) {
        return auditEventRepository.findByEventTypeOrderByTimestampDesc(eventType);
    }
    
    @Transactional(readOnly = true)
    public Optional<AuditEvent> getEventById(String eventId) {
        return auditEventRepository.findByEventId(eventId);
    }
    
    @Transactional(readOnly = true)
    public List<AuditEvent> getEventsByTimeRange(Instant start, Instant end) {
        return auditEventRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
    
    @Transactional(readOnly = true)
    public List<AuditEvent> getFailedEvents() {
        return auditEventRepository.findBySuccessOrderByTimestampDesc(false);
    }
    
    public void logCreate(String entityType, String entityId, Object newValue, AuditContext context) {
        logEvent(AuditEventRequest.builder()
                .eventType("CREATE")
                .entityType(entityType)
                .entityId(entityId)
                .action("CREATE")
                .newValue(serializeValue(newValue))
                .userId(context.getUserId())
                .userName(context.getUserName())
                .ipAddress(context.getIpAddress())
                .userAgent(context.getUserAgent())
                .status(AuditEvent.EventStatus.SUCCESS)
                .build());
    }
    
    public void logUpdate(String entityType, String entityId, Object oldValue, Object newValue, 
            AuditContext context) {
        logEvent(AuditEventRequest.builder()
                .eventType("UPDATE")
                .entityType(entityType)
                .entityId(entityId)
                .action("UPDATE")
                .oldValue(serializeValue(oldValue))
                .newValue(serializeValue(newValue))
                .userId(context.getUserId())
                .userName(context.getUserName())
                .ipAddress(context.getIpAddress())
                .userAgent(context.getUserAgent())
                .status(AuditEvent.EventStatus.SUCCESS)
                .build());
    }
    
    public void logDelete(String entityType, String entityId, Object oldValue, AuditContext context) {
        logEvent(AuditEventRequest.builder()
                .eventType("DELETE")
                .entityType(entityType)
                .entityId(entityId)
                .action("DELETE")
                .oldValue(serializeValue(oldValue))
                .userId(context.getUserId())
                .userName(context.getUserName())
                .ipAddress(context.getIpAddress())
                .userAgent(context.getUserAgent())
                .status(AuditEvent.EventStatus.SUCCESS)
                .build());
    }
    
    public void logAccess(String entityType, String entityId, AuditContext context) {
        logEvent(AuditEventRequest.builder()
                .eventType("ACCESS")
                .entityType(entityType)
                .entityId(entityId)
                .action("READ")
                .userId(context.getUserId())
                .userName(context.getUserName())
                .ipAddress(context.getIpAddress())
                .userAgent(context.getUserAgent())
                .status(AuditEvent.EventStatus.SUCCESS)
                .build());
    }
    
    public void logFailure(String entityType, String entityId, String action, String errorMessage,
            AuditContext context) {
        logEvent(AuditEventRequest.builder()
                .eventType("FAILURE")
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .userId(context.getUserId())
                .userName(context.getUserName())
                .ipAddress(context.getIpAddress())
                .userAgent(context.getUserAgent())
                .metadata(errorMessage)
                .status(AuditEvent.EventStatus.FAILURE)
                .build());
    }
    
    private String serializeValue(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(value);
        } catch (Exception e) {
            return value.toString();
        }
    }
    
    @lombok.Builder
    @lombok.Data
    public static class AuditEventRequest {
        private String eventType;
        private String entityType;
        private String entityId;
        private String action;
        private String userId;
        private String userName;
        private String ipAddress;
        private String userAgent;
        private String oldValue;
        private String newValue;
        private String metadata;
        private AuditEvent.EventStatus status;
    }
    
    @lombok.Builder
    @lombok.Data
    public static class AuditContext {
        private String userId;
        private String userName;
        private String ipAddress;
        private String userAgent;
        private String sessionId;
        private String requestId;
    }
}
