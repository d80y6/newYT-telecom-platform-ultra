package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ChargingRule;
import com.yemenptc.bss.coreservice.service.ChargingService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/productCharging/v5")
@RequiredArgsConstructor
public class ProductChargingController {

    private final ChargingService chargingService;

    @GetMapping("/chargingRule")
    public ResponseEntity<TmfResponse<ChargingRule>> listChargingRules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChargingRule> result = chargingService.listRules(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
            result.getContent(), (int) result.getTotalElements(),
            page * size, size, "ChargingRule"));
    }

    @PostMapping("/chargingRule")
    public ResponseEntity<TmfResponse<ChargingRule>> createChargingRule(@RequestBody ChargingRule request) {
        ChargingRule rule = chargingService.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TmfResponse.success(rule, "ChargingRule"));
    }

    @GetMapping("/chargingRule/{id}")
    public ResponseEntity<TmfResponse<ChargingRule>> getChargingRule(@PathVariable UUID id) {
        ChargingRule rule = chargingService.getRule(id);
        return ResponseEntity.ok(TmfResponse.success(rule, "ChargingRule"));
    }

    @PutMapping("/chargingRule/{id}")
    public ResponseEntity<TmfResponse<ChargingRule>> updateChargingRule(
            @PathVariable UUID id,
            @RequestBody ChargingRule request) {
        ChargingRule rule = chargingService.updateRule(id, request);
        return ResponseEntity.ok(TmfResponse.success(rule, "ChargingRule"));
    }

    @DeleteMapping("/chargingRule/{id}")
    public ResponseEntity<Void> deactivateChargingRule(@PathVariable UUID id) {
        chargingService.deactivateRule(id);
        return ResponseEntity.noContent().build();
    }
}