package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.BillingOutboxEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BillingOutboxRepository extends JpaRepository<BillingOutboxEntity, UUID> {

    List<BillingOutboxEntity> findByStatusOrderByCreatedAtAsc(BillingOutboxEntity.OutboxStatus status);

    Page<BillingOutboxEntity> findByStatus(BillingOutboxEntity.OutboxStatus status, Pageable pageable);

    List<BillingOutboxEntity> findByStatusAndRetryCountLessThan(BillingOutboxEntity.OutboxStatus status, int maxRetries);

    @Query("SELECT o FROM BillingOutboxEntity o WHERE o.status = :status AND o.createdAt < :cutoff ORDER BY o.createdAt ASC")
    List<BillingOutboxEntity> findStaleOutboxItems(
        @Param("status") BillingOutboxEntity.OutboxStatus status,
        @Param("cutoff") Instant cutoff
    );

    @Modifying
    @Query("UPDATE BillingOutboxEntity o SET o.status = :status, o.processedAt = :processedAt WHERE o.id = :id")
    void updateStatus(
        @Param("id") UUID id,
        @Param("status") BillingOutboxEntity.OutboxStatus status,
        @Param("processedAt") Instant processedAt
    );

    @Modifying
    @Query("UPDATE BillingOutboxEntity o SET o.retryCount = o.retryCount + 1, o.errorMessage = :errorMessage WHERE o.id = :id")
    void incrementRetryCount(
        @Param("id") UUID id,
        @Param("errorMessage") String errorMessage
    );

    long countByStatus(BillingOutboxEntity.OutboxStatus status);
}
