package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.SlaTemplate;
import com.yemenptc.bss.coreservice.service.SlaService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/slaManagement/v5")
@RequiredArgsConstructor
public class SlaManagementController {

    private final SlaService slaService;

    @GetMapping("/slaTemplate")
    public ResponseEntity<TmfResponse<SlaTemplate>> listSlaTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SlaTemplate> result = slaService.listTemplates(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
            result.getContent(), (int) result.getTotalElements(),
            page * size, size, "SlaTemplate"));
    }

    @PostMapping("/slaTemplate")
    public ResponseEntity<TmfResponse<SlaTemplate>> createSlaTemplate(@RequestBody SlaTemplate request) {
        SlaTemplate template = slaService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TmfResponse.success(template, "SlaTemplate"));
    }

    @GetMapping("/slaTemplate/{id}")
    public ResponseEntity<TmfResponse<SlaTemplate>> getSlaTemplate(@PathVariable UUID id) {
        SlaTemplate template = slaService.getTemplate(id);
        return ResponseEntity.ok(TmfResponse.success(template, "SlaTemplate"));
    }

    @PutMapping("/slaTemplate/{id}/activate")
    public ResponseEntity<TmfResponse<SlaTemplate>> activateSlaTemplate(@PathVariable UUID id) {
        SlaTemplate template = slaService.activateTemplate(id);
        return ResponseEntity.ok(TmfResponse.success(template, "SlaTemplate"));
    }

    @PutMapping("/slaTemplate/{id}/deprecate")
    public ResponseEntity<TmfResponse<SlaTemplate>> deprecateSlaTemplate(@PathVariable UUID id) {
        SlaTemplate template = slaService.deprecateTemplate(id);
        return ResponseEntity.ok(TmfResponse.success(template, "SlaTemplate"));
    }

    @DeleteMapping("/slaTemplate/{id}")
    public ResponseEntity<Void> deleteSlaTemplate(@PathVariable UUID id) {
        slaService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}