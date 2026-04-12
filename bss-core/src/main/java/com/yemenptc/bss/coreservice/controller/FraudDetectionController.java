package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.FraudAlert;
import com.yemenptc.bss.coreservice.service.FraudDetectionService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/fraudManagement/v5")
@RequiredArgsConstructor
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @GetMapping("/alert")
    public ResponseEntity<TmfResponse<FraudAlert>> listAlerts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<FraudAlert> result;
        if (status != null) {
            result = fraudDetectionService.findByStatus(FraudAlert.AlertStatus.valueOf(status), PageRequest.of(page, size));
        } else if (severity != null) {
            result = fraudDetectionService.findBySeverity(FraudAlert.FraudSeverity.valueOf(severity), PageRequest.of(page, size));
        } else {
            result = fraudDetectionService.listAlerts(PageRequest.of(page, size));
        }
        
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(), page, size, "FraudAlert"));
    }

    @PostMapping("/alert")
    public ResponseEntity<TmfResponse<FraudAlert>> createAlert(@RequestBody FraudAlert request) {
        FraudAlert alert = fraudDetectionService.createAlert(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(alert, "FraudAlert"));
    }

    @GetMapping("/alert/{id}")
    public ResponseEntity<TmfResponse<FraudAlert>> getAlert(@PathVariable UUID id) {
        FraudAlert alert = fraudDetectionService.getAlert(id);
        return ResponseEntity.ok(TmfResponse.success(alert, "FraudAlert"));
    }

    @PostMapping("/alert/{id}/review")
    public ResponseEntity<TmfResponse<FraudAlert>> reviewAlert(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String reviewedBy = request.get("reviewedBy");
        String resolution = request.get("resolution");
        String notes = request.get("notes");
        
        FraudAlert alert = fraudDetectionService.reviewAlert(id, reviewedBy, resolution, notes);
        return ResponseEntity.ok(TmfResponse.success(alert, "FraudAlert"));
    }

    @PostMapping("/alert/{id}/confirm")
    public ResponseEntity<TmfResponse<FraudAlert>> confirmAlert(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String actionTaken = request.get("actionTaken");
        FraudAlert alert = fraudDetectionService.confirmAlert(id, actionTaken);
        return ResponseEntity.ok(TmfResponse.success(alert, "FraudAlert"));
    }

    @PostMapping("/alert/{id}/escalate")
    public ResponseEntity<TmfResponse<FraudAlert>> escalateAlert(@PathVariable UUID id) {
        FraudAlert alert = fraudDetectionService.escalateAlert(id);
        return ResponseEntity.ok(TmfResponse.success(alert, "FraudAlert"));
    }

    @PostMapping("/alert/{id}/resolve")
    public ResponseEntity<TmfResponse<FraudAlert>> resolveAlert(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String resolution = request.get("resolution");
        String notes = request.get("notes");
        FraudAlert alert = fraudDetectionService.resolveAlert(id, resolution, notes);
        return ResponseEntity.ok(TmfResponse.success(alert, "FraudAlert"));
    }

    @PostMapping("/alert/{id}/falsePositive")
    public ResponseEntity<TmfResponse<FraudAlert>> markFalsePositive(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String notes = request.get("notes");
        FraudAlert alert = fraudDetectionService.markFalsePositive(id, notes);
        return ResponseEntity.ok(TmfResponse.success(alert, "FraudAlert"));
    }

    @PostMapping("/check/usageSpike")
    public ResponseEntity<TmfResponse<Map>> checkUsageSpike(@RequestBody Map<String, Object> request) {
        String accountId = (String) request.get("accountId");
        BigDecimal currentUsage = new BigDecimal(request.get("currentUsage").toString());
        BigDecimal threshold = new BigDecimal(request.get("threshold").toString());
        
        boolean triggered = fraudDetectionService.checkUsageSpike(accountId, currentUsage, threshold);
        
        return ResponseEntity.ok(TmfResponse.success(Map.of(
            "triggered", triggered,
            "accountId", accountId
        ), "CheckResult"));
    }
}