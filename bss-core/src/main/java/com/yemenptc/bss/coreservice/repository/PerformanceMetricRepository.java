package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.PerformanceMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, UUID> {

    Page<PerformanceMetric> findByResourceId(String resourceId, Pageable pageable);

    Page<PerformanceMetric> findByMetricName(String metricName, Pageable pageable);

    Page<PerformanceMetric> findBySeverity(String severity, Pageable pageable);

    List<PerformanceMetric> findByCollectionTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<PerformanceMetric> findByResourceIdAndCollectionTimestampBetween(
            String resourceId, LocalDateTime start, LocalDateTime end);

    long countBySeverity(String severity);
}