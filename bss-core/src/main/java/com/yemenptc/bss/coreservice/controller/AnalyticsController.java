package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.AnalyticsMetric;
import com.yemenptc.bss.coreservice.service.AnalyticsService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/analytics/v5")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/metric")
    public ResponseEntity<TmfResponse<AnalyticsMetric>> listMetrics(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<AnalyticsMetric> result;
        if (category != null) {
            result = analyticsService.getMetricsByCategory(category, PageRequest.of(page, size));
        } else {
            result = analyticsService.listMetrics(PageRequest.of(page, size));
        }
        
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(), page, size, "AnalyticsMetric"));
    }

    @PostMapping("/metric")
    public ResponseEntity<TmfResponse<AnalyticsMetric>> createMetric(@RequestBody AnalyticsMetric request) {
        AnalyticsMetric metric = analyticsService.recordMetric(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(metric, "AnalyticsMetric"));
    }

    @GetMapping("/metric/{id}")
    public ResponseEntity<TmfResponse<AnalyticsMetric>> getMetric(@PathVariable UUID id) {
        AnalyticsMetric metric = analyticsService.getMetric(id);
        return ResponseEntity.ok(TmfResponse.success(metric, "AnalyticsMetric"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<TmfResponse<Map>> getDashboard() {
        Map summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(TmfResponse.success(summary, "Dashboard"));
    }

    @GetMapping("/kpi")
    public ResponseEntity<TmfResponse<Map>> getKpis() {
        Map kpis = analyticsService.calculateKpis();
        return ResponseEntity.ok(TmfResponse.success(kpis, "KPI"));
    }

    @GetMapping("/timeSeries")
    public ResponseEntity<TmfResponse<AnalyticsMetric>> getTimeSeries(
            @RequestParam String metricName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<AnalyticsMetric> metrics = analyticsService.getTimeSeriesData(metricName, startTime, endTime);
        return ResponseEntity.ok(TmfResponse.list(metrics, metrics.size(), 0, metrics.size(), "AnalyticsMetric"));
    }
}