package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.entity.RatingRecord;
import com.yemenptc.bss.coreservice.service.RatingService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/usageManagement/v5")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/usage")
    public ResponseEntity<TmfResponse<UsageEvent>> createUsageEvent(@RequestBody UsageEvent request) {
        UsageEvent event = ratingService.createUsageEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(event, "UsageEvent"));
    }

    @GetMapping("/usage")
    public ResponseEntity<TmfResponse<UsageEvent>> listUsageEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UsageEvent> result = ratingService.listUsageEvents(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "UsageEvent"));
    }

    @PostMapping("/rating")
    public ResponseEntity<TmfResponse<RatingRecord>> rateUsage(@RequestBody UsageEvent request) {
        RatingRecord record = ratingService.rateUsage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(record, "RatingRecord"));
    }

    @PostMapping("/rating/batch")
    public ResponseEntity<TmfResponse<RatingRecord>> batchRate(@RequestBody List<UsageEvent> events) {
        RatingRecord record = ratingService.batchRate(events);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(record, "RatingRecord"));
    }

    @GetMapping("/rating/{id}")
    public ResponseEntity<TmfResponse<RatingRecord>> getRatingRecord(@PathVariable UUID id) {
        RatingRecord record = ratingService.getRatingRecord(id);
        return ResponseEntity.ok(TmfResponse.success(record, "RatingRecord"));
    }

    @GetMapping("/rating")
    public ResponseEntity<TmfResponse<RatingRecord>> listRatingRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RatingRecord> result = ratingService.listRatingRecords(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "RatingRecord"));
    }

    @GetMapping("/rating/subscription/{subscriptionId}")
    public ResponseEntity<TmfResponse<RatingRecord>> getRatingsBySubscription(@PathVariable UUID subscriptionId) {
        List<RatingRecord> records = ratingService.getRatingsBySubscription(subscriptionId);
        return ResponseEntity.ok(TmfResponse.list(
                records, records.size(), 0, records.size(), "RatingRecord"));
    }
}
