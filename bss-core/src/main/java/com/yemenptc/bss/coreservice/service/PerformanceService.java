package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Metric;
import com.yemenptc.bss.coreservice.entity.SlaThreshold;
import com.yemenptc.bss.coreservice.repository.MetricRepository;
import com.yemenptc.bss.coreservice.repository.SlaThresholdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PerformanceService {

    private final MetricRepository metricRepository;
    private final SlaThresholdRepository slaThresholdRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NotificationService notificationService;

    @Transactional
    public Metric recordMetric(String resourceId, String metricName, Double value, String unit) {
        Metric metric = Metric.builder()
                .resourceId(resourceId != null ? UUID.fromString(resourceId) : null)
                .metricName(metricName)
                .metricValue(value)
                .metricUnit(unit)
                .timestamp(Instant.now())
                .build();

        metric = metricRepository.save(metric);

        kafkaTemplate.send("performance.metrics", resourceId, metric);

        checkSlaThresholds(metricName, value);

        return metric;
    }

    private void checkSlaThresholds(String metricName, Double value) {
        List<SlaThreshold> thresholds = slaThresholdRepository.findByMetricName(metricName);
        
        for (SlaThreshold threshold : thresholds) {
            if (evaluateThreshold(value, threshold)) {
                handleSlaViolation(threshold, metricName, value);
            }
        }
    }

    private boolean evaluateThreshold(Double value, SlaThreshold threshold) {
        return switch (threshold.getOperator()) {
            case GT -> value > threshold.getThresholdValue();
            case LT -> value < threshold.getThresholdValue();
            case GE -> value >= threshold.getThresholdValue();
            case LE -> value <= threshold.getThresholdValue();
            case EQ -> Math.abs(value - threshold.getThresholdValue()) < 0.001;
        };
    }

    private void handleSlaViolation(SlaThreshold threshold, String metricName, Double value) {
        log.warn("SLA violation: {} {} {} for metric {}", 
                metricName, threshold.getOperator(), threshold.getThresholdValue(), metricName);

        if (Boolean.TRUE.equals(threshold.getAutoCreditEnabled()) && threshold.getCreditAmount() != null) {
            log.info("Auto-credit of {} to be applied for SLA metric {}", threshold.getCreditAmount(), metricName);
        }
    }

    @Transactional(readOnly = true)
    public List<Metric> getMetricsByResource(String resourceId, Instant startTime, Instant endTime) {
        return metricRepository.findByResourceIdAndTimestampBetween(
                UUID.fromString(resourceId), startTime, endTime);
    }

    @Transactional(readOnly = true)
    public List<Metric> getMetricsByName(String metricName, Instant startTime, Instant endTime) {
        return metricRepository.findByMetricNameAndTimestampBetween(metricName, startTime, endTime);
    }
}
