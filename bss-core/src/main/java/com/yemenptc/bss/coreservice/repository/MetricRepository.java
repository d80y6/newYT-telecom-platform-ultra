package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MetricRepository extends JpaRepository<Metric, UUID> {
    
    List<Metric> findByResourceIdAndTimestampBetween(UUID resourceId, Instant startTime, Instant endTime);
    
    List<Metric> findByMetricNameAndTimestampBetween(String metricName, Instant startTime, Instant endTime);
    
    List<Metric> findByResourceId(UUID resourceId);
    
    List<Metric> findByMetricName(String metricName);
}
