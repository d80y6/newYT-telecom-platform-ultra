package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.PerformanceMetric;
import com.yemenptc.bss.coreservice.repository.PerformanceMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PerformanceManagementService {

    private final PerformanceMetricRepository performanceMetricRepository;

    @Transactional
    public PerformanceMetric recordMetric(PerformanceMetric request) {
        log.info("Recording performance metric: {}", request.getMetricName());

        PerformanceMetric metric = PerformanceMetric.builder()
            .metricName(request.getMetricName())
            .resourceId(request.getResourceId())
            .resourceType(request.getResourceType())
            .resourceName(request.getResourceName())
            .serviceId(request.getServiceId())
            .neId(request.getNeId())
            .collectorId(request.getCollectorId())
            .collectionTimestamp(request.getCollectionTimestamp() != null ? 
                request.getCollectionTimestamp() : LocalDateTime.now())
            .metricValue(request.getMetricValue())
            .unit(request.getUnit())
            .metricType(request.getMetricType() != null ? 
                request.getMetricType() : PerformanceMetric.MetricType.GAUGE)
            .thresholdId(request.getThresholdId())
            .severity(request.getSeverity())
            .locationId(request.getLocationId())
            .region(request.getRegion())
            .build();

        if (metric.getSeverity() == null) {
            metric.setSeverity(determineSeverity(metric.getMetricName(), metric.getMetricValue()));
        }

        PerformanceMetric saved = performanceMetricRepository.save(metric);
        log.info("Performance metric recorded: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public PerformanceMetric getMetric(UUID id) {
        return performanceMetricRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Performance metric not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PerformanceMetric> listMetrics(Pageable pageable) {
        return performanceMetricRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<PerformanceMetric> getMetricsByResource(String resourceId, Pageable pageable) {
        return performanceMetricRepository.findByResourceId(resourceId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PerformanceMetric> getMetricsBySeverity(String severity, Pageable pageable) {
        return performanceMetricRepository.findBySeverity(severity, pageable);
    }

    @Transactional(readOnly = true)
    public List<PerformanceMetric> getMetricsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return performanceMetricRepository.findByCollectionTimestampBetween(start, end);
    }

    @Transactional(readOnly = true)
    public List<PerformanceMetric> getMetricsForResource(String resourceId, LocalDateTime start, LocalDateTime end) {
        return performanceMetricRepository.findByResourceIdAndCollectionTimestampBetween(resourceId, start, end);
    }

    @Transactional(readOnly = true)
    public long countBySeverity(String severity) {
        return performanceMetricRepository.countBySeverity(severity);
    }

    private String determineSeverity(String metricName, BigDecimal value) {
        if (value == null) return "NORMAL";
        
        String lowerName = metricName.toLowerCase();
        
        if (lowerName.contains("cpu") || lowerName.contains("memory") || lowerName.contains("disk")) {
            if (value.doubleValue() > 90) return "CRITICAL";
            if (value.doubleValue() > 75) return "MAJOR";
            if (value.doubleValue() > 60) return "MINOR";
        }
        
        if (lowerName.contains("latency") || lowerName.contains("delay")) {
            if (value.doubleValue() > 1000) return "CRITICAL";
            if (value.doubleValue() > 500) return "MAJOR";
            if (value.doubleValue() > 200) return "MINOR";
        }
        
        if (lowerName.contains("packet") || lowerName.contains("error") || lowerName.contains("fail")) {
            if (value.doubleValue() > 10) return "CRITICAL";
            if (value.doubleValue() > 5) return "MAJOR";
            if (value.doubleValue() > 1) return "MINOR";
        }
        
        return "NORMAL";
    }
}