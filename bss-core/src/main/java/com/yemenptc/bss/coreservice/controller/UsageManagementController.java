package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.UsageRecord;
import com.yemenptc.bss.coreservice.service.UsageManagementService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/usageManagement/v5")
@RequiredArgsConstructor
public class UsageManagementController {

    private final UsageManagementService usageManagementService;

    @GetMapping("/usage")
    public ResponseEntity<TmfResponse<UsageRecord>> listUsage(
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String usageType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<UsageRecord> result;
        if (accountId != null) {
            result = usageManagementService.getUsageByAccount(accountId, PageRequest.of(page, size));
        } else {
            result = usageManagementService.listUsageRecords(PageRequest.of(page, size));
        }
        
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(), page, size, "Usage"));
    }

    @PostMapping("/usage")
    public ResponseEntity<TmfResponse<UsageRecord>> recordUsage(@RequestBody UsageRecord request) {
        UsageRecord record = usageManagementService.recordUsage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(record, "Usage"));
    }

    @GetMapping("/usage/{id}")
    public ResponseEntity<TmfResponse<UsageRecord>> getUsage(@PathVariable UUID id) {
        UsageRecord record = usageManagementService.getUsage(id);
        return ResponseEntity.ok(TmfResponse.success(record, "Usage"));
    }

    @GetMapping("/usage/{id}/ratedUsage")
    public ResponseEntity<TmfResponse<UsageRecord>> getRatedUsage(@PathVariable UUID id) {
        UsageRecord record = usageManagementService.getUsage(id);
        return ResponseEntity.ok(TmfResponse.success(record, "RatedUsage"));
    }

    @PostMapping("/usage/{id}/rate")
    public ResponseEntity<TmfResponse<UsageRecord>> rateUsage(
            @PathVariable UUID id,
            @RequestBody Map<String, BigDecimal> request) {
        
        BigDecimal amount = request.get("amount");
        UsageRecord record = usageManagementService.rateUsage(id, amount);
        return ResponseEntity.ok(TmfResponse.success(record, "Usage"));
    }

    @GetMapping("/usageAccumulation")
    public ResponseEntity<TmfResponse<Map>> getUsageAccumulation(
            @RequestParam String accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null 
            ? LocalDateTime.parse(startDate) 
            : LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        LocalDateTime end = endDate != null 
            ? LocalDateTime.parse(endDate) 
            : LocalDateTime.now();
        
        var records = usageManagementService.getUsageByDateRange(accountId, start, end);
        
        long totalVoiceSeconds = records.stream()
            .filter(r -> r.getUsageType() == UsageRecord.UsageType.VOICE)
            .mapToLong(r -> r.getDurationSeconds() != null ? r.getDurationSeconds() : 0)
            .sum();
        
        long totalDataBytes = records.stream()
            .filter(r -> r.getUsageType() == UsageRecord.UsageType.DATA)
            .mapToLong(r -> r.getVolumeBytes() != null ? r.getVolumeBytes() : 0)
            .sum();
        
        int totalSmsCount = records.stream()
            .filter(r -> r.getUsageType() == UsageRecord.UsageType.SMS)
            .mapToInt(r -> r.getCount() != null ? r.getCount() : 0)
            .sum();
        
        Map<String, Object> accumulation = Map.of(
            "accountId", accountId,
            "totalVoiceSeconds", totalVoiceSeconds,
            "totalDataMB", totalDataBytes / (1024 * 1024),
            "totalSmsCount", totalSmsCount,
            "recordCount", records.size()
        );
        
        return ResponseEntity.ok(TmfResponse.success(accumulation, "UsageAccumulation"));
    }

    @PostMapping("/usage/threshold")
    public ResponseEntity<TmfResponse<Map>> setThreshold(@RequestBody Map<String, Object> request) {
        Map<String, Object> threshold = Map.of(
            "accountId", request.get("accountId"),
            "usageType", request.get("usageType"),
            "thresholdValue", request.get("thresholdValue"),
            "notificationEnabled", true
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(threshold, "Threshold"));
    }
}