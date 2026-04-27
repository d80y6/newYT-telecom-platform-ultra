package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    Page<AuditEvent> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);

    Page<AuditEvent> findByUserId(UUID userId, Pageable pageable);

    Page<AuditEvent> findByUsername(String username, Pageable pageable);

    Page<AuditEvent> findByTimestampBetween(Instant start, Instant end, Pageable pageable);

    List<AuditEvent> findByCorrelationId(String correlationId);

    @Query("SELECT a FROM AuditEvent a WHERE a.entityType = :entityType AND a.entityId = :entityId AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<AuditEvent> findRecentEventsForEntity(
        @Param("entityType") String entityType,
        @Param("entityId") UUID entityId,
        @Param("since") Instant since
    );

    @Query("SELECT a FROM AuditEvent a WHERE a.action = :action AND a.success = false ORDER BY a.timestamp DESC")
    Page<AuditEvent> findFailedActions(@Param("action") String action, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.timestamp < :cutoff")
    long countEventsBeforeCutoff(@Param("cutoff") Instant cutoff);

    List<AuditEvent> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, UUID entityId);

    List<AuditEvent> findByUserIdOrderByTimestampDesc(UUID userId);

    List<AuditEvent> findByEventTypeOrderByTimestampDesc(String eventType);

    java.util.Optional<AuditEvent> findByEventId(String eventId);

    List<AuditEvent> findByTimestampBetweenOrderByTimestampDesc(Instant start, Instant end);

    List<AuditEvent> findBySuccessOrderByTimestampDesc(boolean success);
}
