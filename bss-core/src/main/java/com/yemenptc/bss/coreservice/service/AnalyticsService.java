package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.AnalyticsMetric;
import com.yemenptc.bss.coreservice.repository.AnalyticsMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsMetricRepository analyticsMetricRepository;

    @Transactional
    public AnalyticsMetric recordMetric(AnalyticsMetric request) {
        log.info("Recording analytics metric: {}", request.getMetricName());

        AnalyticsMetric metric = AnalyticsMetric.builder()
            .metricName(request.getMetricName())
            .metricCategory(request.getMetricCategory())
            .dataSource(request.getDataSource())
            .accountId(request.getAccountId())
            .serviceId(request.getServiceId())
            .periodStart(request.getPeriodStart())
            .periodEnd(request.getPeriodEnd())
            .value(request.getValue())
            .previousValue(request.getPreviousValue())
            .unit(request.getUnit())
            .aggregationType(request.getAggregationType())
            .dimensions(request.getDimensions())
            .calculatedAt(LocalDateTime.now())
            .build();

        if (metric.getPreviousValue() != null && metric.getPreviousValue().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal change = metric.getValue().subtract(metric.getPreviousValue())
                .divide(metric.getPreviousValue(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            metric.setChangePercent(change);
        }

        AnalyticsMetric saved = analyticsMetricRepository.save(metric);
        log.info("Analytics metric recorded: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public AnalyticsMetric getMetric(UUID id) {
        return analyticsMetricRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Analytics metric not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<AnalyticsMetric> listMetrics(Pageable pageable) {
        return analyticsMetricRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AnalyticsMetric> getMetricsByCategory(String category, Pageable pageable) {
        return analyticsMetricRepository.findByMetricCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        
        List<AnalyticsMetric> recentMetrics = analyticsMetricRepository.findByPeriodStartBetween(weekAgo, now);
        
        BigDecimal totalRevenue = recentMetrics.stream()
            .filter(m -> "REVENUE".equals(m.getMetricCategory()))
            .map(AnalyticsMetric::getValue)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long activeServices = recentMetrics.stream()
            .filter(m -> "SERVICE_COUNT".equals(m.getMetricCategory()))
            .map(AnalyticsMetric::getValue)
            .filter(Objects::nonNull)
            .count();
        
        long totalCustomers = recentMetrics.stream()
            .filter(m -> "CUSTOMER_COUNT".equals(m.getMetricCategory()))
            .map(AnalyticsMetric::getValue)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(BigDecimal.ZERO)
            .longValue();
        
        return Map.of(
            "totalRevenue", totalRevenue,
            "activeServices", activeServices,
            "totalCustomers", totalCustomers,
            "period", "LAST_7_DAYS",
            "generatedAt", now.toString()
        );
    }

    @Transactional(readOnly = true)
    public List<AnalyticsMetric> getTimeSeriesData(String metricName, LocalDateTime start, LocalDateTime end) {
        List<AnalyticsMetric> metrics = analyticsMetricRepository.findByPeriodStartBetween(start, end);
        metrics.sort(Comparator.comparing(AnalyticsMetric::getPeriodStart));
        return metrics;
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateKpis() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1);
        
        List<AnalyticsMetric> monthMetrics = analyticsMetricRepository.findByPeriodStartBetween(monthStart, now);
        
        BigDecimal arpu = monthMetrics.stream()
            .filter(m -> "ARPPU".equals(m.getMetricName()))
            .map(AnalyticsMetric::getValue)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(BigDecimal.ZERO);
        
        BigDecimal churnRate = monthMetrics.stream()
            .filter(m -> "CHURN_RATE".equals(m.getMetricName()))
            .map(AnalyticsMetric::getValue)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(BigDecimal.ZERO);
        
        BigDecimal collectionRate = monthMetrics.stream()
            .filter(m -> "COLLECTION_RATE".equals(m.getMetricName()))
            .map(AnalyticsMetric::getValue)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(BigDecimal.valueOf(95));
        
        Map<String, BigDecimal> kpis = new HashMap<>();
        kpis.put("arpu", arpu);
        kpis.put("churnRate", churnRate);
        kpis.put("collectionRate", collectionRate);
        return kpis;
    }
}