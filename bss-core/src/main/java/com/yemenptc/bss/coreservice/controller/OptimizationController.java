package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.service.*;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tmf-api/optimization/v5")
@RequiredArgsConstructor
public class OptimizationController {

    private final PerformanceOptimizationService optimizationService;
    private final ZeroTouchAutomationService automationService;
    private final OmnichannelService omnichannelService;

    @GetMapping("/performance")
    public ResponseEntity<TmfResponse<Map>> getSystemPerformance() {
        Map result = optimizationService.analyzeSystemPerformance();
        return ResponseEntity.ok(TmfResponse.success(result, "Performance"));
    }

    @GetMapping("/database")
    public ResponseEntity<TmfResponse<Map>> getDatabaseMetrics() {
        Map result = optimizationService.getDatabaseOptimizationMetrics();
        return ResponseEntity.ok(TmfResponse.success(result, "DatabaseMetrics"));
    }

    @GetMapping("/cache")
    public ResponseEntity<TmfResponse<Map>> getCacheMetrics() {
        Map result = optimizationService.getCachePerformanceMetrics();
        return ResponseEntity.ok(TmfResponse.success(result, "CacheMetrics"));
    }

    @PostMapping("/database/optimize")
    public ResponseEntity<TmfResponse<Map>> optimizeDatabase(@RequestBody Map<String, String> request) {
        String type = request.get("type");
        Map result = optimizationService.optimizeDatabase(type);
        return ResponseEntity.ok(TmfResponse.success(result, "Optimization"));
    }

    @PostMapping("/cache/optimize")
    public ResponseEntity<TmfResponse<Map>> optimizeCache(@RequestBody Map<String, String> request) {
        String action = request.get("action");
        Map result = optimizationService.optimizeCache(action);
        return ResponseEntity.ok(TmfResponse.success(result, "Optimization"));
    }

    @GetMapping("/autoscaling")
    public ResponseEntity<TmfResponse<Map>> getAutoScalingMetrics() {
        Map result = optimizationService.getAutoScalingMetrics();
        return ResponseEntity.ok(TmfResponse.success(result, "AutoScaling"));
    }

    @GetMapping("/automation/metrics")
    public ResponseEntity<TmfResponse<Map>> getAutomationMetrics() {
        Map result = automationService.getAutomationMetrics();
        return ResponseEntity.ok(TmfResponse.success(result, "AutomationMetrics"));
    }

    @GetMapping("/omnichannel/preferences/{customerId}")
    public ResponseEntity<TmfResponse<Map>> getChannelPreferences(@PathVariable String customerId) {
        Map result = omnichannelService.getChannelPreferences(customerId);
        return ResponseEntity.ok(TmfResponse.success(result, "ChannelPreferences"));
    }

    @PostMapping("/omnichannel/notify")
    public ResponseEntity<TmfResponse<Map>> sendNotification(
            @RequestBody Map<String, Object> request) {
        String customerId = (String) request.get("customerId");
        Map result = omnichannelService.sendUnifiedNotification(customerId, request);
        return ResponseEntity.ok(TmfResponse.success(result, "Notification"));
    }
}