package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    Page<AuditLog> findByPrincipalId(String principalId, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);

    Page<AuditLog> findByResourceId(String resourceId, Pageable pageable);

    Page<AuditLog> findByResourceType(String resourceType, Pageable pageable);

    Page<AuditLog> findByResult(String result, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    Page<AuditLog> findByTimestampBetween(@Param("start") Instant start, @Param("end") Instant end, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.principalId = :principalId AND a.timestamp BETWEEN :start AND :end")
    Page<AuditLog> findByPrincipalIdAndTimestampBetween(@Param("principalId") String principalId, @Param("start") Instant start, @Param("end") Instant end, Pageable pageable);

    long countByActionAndTimestampBetween(String action, Instant start, Instant end);

    @Query("DELETE FROM AuditLog a WHERE a.retentionUntil < :now")
    void deleteExpiredLogs(@Param("now") Instant now);
}
