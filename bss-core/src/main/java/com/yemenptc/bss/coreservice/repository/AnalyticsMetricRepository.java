package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.AnalyticsMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsMetricRepository extends JpaRepository<AnalyticsMetric, UUID> {

    Page<AnalyticsMetric> findByMetricName(String metricName, Pageable pageable);

    Page<AnalyticsMetric> findByMetricCategory(String category, Pageable pageable);

    Page<AnalyticsMetric> findByAccountId(String accountId, Pageable pageable);

    List<AnalyticsMetric> findByPeriodStartBetween(LocalDateTime start, LocalDateTime end);
}