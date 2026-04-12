package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Fault;
import com.yemenptc.bss.coreservice.service.FaultManagementService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/faultManagement/v5")
@RequiredArgsConstructor
public class FaultManagementController {

    private final FaultManagementService faultManagementService;

    @GetMapping("/fault")
    public ResponseEntity<TmfResponse<Fault>> listFaults(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Fault> result;
        if (status != null) {
            result = faultManagementService.findByStatus(Fault.FaultStatus.valueOf(status), PageRequest.of(page, size));
        } else if (severity != null) {
            result = faultManagementService.findBySeverity(Fault.AlarmSeverity.valueOf(severity), PageRequest.of(page, size));
        } else {
            result = faultManagementService.listFaults(PageRequest.of(page, size));
        }
        
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(), page, size, "Fault"));
    }

    @PostMapping("/fault")
    public ResponseEntity<TmfResponse<Fault>> createFault(@RequestBody Fault request) {
        Fault fault = faultManagementService.createFault(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(fault, "Fault"));
    }

    @GetMapping("/fault/{id}")
    public ResponseEntity<TmfResponse<Fault>> getFault(@PathVariable UUID id) {
        Fault fault = faultManagementService.getFault(id);
        return ResponseEntity.ok(TmfResponse.success(fault, "Fault"));
    }

    @PostMapping("/fault/{id}/acknowledge")
    public ResponseEntity<TmfResponse<Fault>> acknowledgeFault(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String acknowledgedBy = request.get("acknowledgedBy");
        Fault fault = faultManagementService.acknowledgeFault(id, acknowledgedBy);
        return ResponseEntity.ok(TmfResponse.success(fault, "Fault"));
    }

    @PostMapping("/fault/{id}/clear")
    public ResponseEntity<TmfResponse<Fault>> clearFault(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String clearedBy = request.get("clearedBy");
        String resolutionNotes = request.get("resolutionNotes");
        Fault fault = faultManagementService.clearFault(id, clearedBy, resolutionNotes);
        return ResponseEntity.ok(TmfResponse.success(fault, "Fault"));
    }

    @PostMapping("/fault/{id}/close")
    public ResponseEntity<TmfResponse<Fault>> closeFault(@PathVariable UUID id) {
        Fault fault = faultManagementService.closeFault(id);
        return ResponseEntity.ok(TmfResponse.success(fault, "Fault"));
    }

    @PostMapping("/fault/{id}/notes")
    public ResponseEntity<TmfResponse<Fault>> addNotes(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String notes = request.get("resolutionNotes");
        Fault fault = faultManagementService.addResolutionNotes(id, notes);
        return ResponseEntity.ok(TmfResponse.success(fault, "Fault"));
    }
}