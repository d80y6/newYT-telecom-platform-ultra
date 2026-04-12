package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.PerformanceMetric;
import com.yemenptc.bss.coreservice.service.PerformanceManagementService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/performanceManagement/v5")
@RequiredArgsConstructor
public class PerformanceManagementController {

    private final PerformanceManagementService performanceManagementService;

    @GetMapping("/performanceMetric")
    public ResponseEntity<TmfResponse<PerformanceMetric>> listMetrics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PerformanceMetric> result = performanceManagementService.listMetrics(PageRequest.of(page, size));
        List<PerformanceMetric> metrics = result.getContent();
        return ResponseEntity.ok(TmfResponse.list(metrics, metrics.size(), 0, metrics.size(), "PerformanceMetric"));
    }

    @PostMapping("/performanceMetric")
    public ResponseEntity<TmfResponse<PerformanceMetric>> createMetric(@RequestBody PerformanceMetric request) {
        PerformanceMetric metric = performanceManagementService.recordMetric(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(metric, "PerformanceMetric"));
    }

    @GetMapping("/performanceMetric/{id}")
    public ResponseEntity<TmfResponse<PerformanceMetric>> getMetric(@PathVariable UUID id) {
        PerformanceMetric metric = performanceManagementService.getMetric(id);
        return ResponseEntity.ok(TmfResponse.success(metric, "PerformanceMetric"));
    }

    @PostMapping("/performanceMetric/bulk")
    public ResponseEntity<TmfResponse<?>> createBulkMetrics(@RequestBody List<PerformanceMetric> request) {
        for (PerformanceMetric metric : request) {
            performanceManagementService.recordMetric(metric);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.list(request, request.size(), 0, request.size(), "PerformanceMetric"));
    }

    @GetMapping("/performanceThreshold")
    public ResponseEntity<TmfResponse<?>> listThresholds() {
        return ResponseEntity.ok(TmfResponse.list(List.of(), 0, 0, 0, "PerformanceThreshold"));
    }

    @PostMapping("/performanceThreshold")
    public ResponseEntity<TmfResponse<?>> createThreshold(@RequestBody Map<String, Object> request) {
        Map<String, Object> threshold = Map.of(
            "id", UUID.randomUUID().toString(),
            "thresholdType", request.get("thresholdType"),
            "thresholdValue", request.get("thresholdValue")
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(threshold, "PerformanceThreshold"));
    }

    @GetMapping("/performanceReport")
    public ResponseEntity<TmfResponse<?>> generateReport(
            @RequestParam(required = false) String metricName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> report = Map.of(
            "metricName", metricName != null ? metricName : "ALL",
            "generatedAt", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(TmfResponse.success(report, "PerformanceReport"));
    }
}
