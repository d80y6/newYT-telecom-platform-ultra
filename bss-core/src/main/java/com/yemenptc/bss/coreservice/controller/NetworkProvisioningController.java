package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.NetworkProvisioningOrder;
import com.yemenptc.bss.coreservice.service.NetworkProvisioningService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/serviceProvisioningManagement/v5")
@RequiredArgsConstructor
public class NetworkProvisioningController {

    private final NetworkProvisioningService provisioningService;

    @GetMapping("/provisioningOrder")
    public ResponseEntity<TmfResponse<NetworkProvisioningOrder>> listProvisioningOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<NetworkProvisioningOrder> result;
        if (status != null) {
            result = provisioningService.findByStatus(
                NetworkProvisioningOrder.ProvisionOrderStatus.valueOf(status), PageRequest.of(page, size));
        } else {
            result = provisioningService.listProvisioningOrders(PageRequest.of(page, size));
        }
        
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(), page, size, "ProvisioningOrder"));
    }

    @PostMapping("/provisioningOrder")
    public ResponseEntity<TmfResponse<NetworkProvisioningOrder>> createProvisioningOrder(@RequestBody NetworkProvisioningOrder request) {
        NetworkProvisioningOrder order = provisioningService.createProvisioningOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(order, "ProvisioningOrder"));
    }

    @GetMapping("/provisioningOrder/{id}")
    public ResponseEntity<TmfResponse<NetworkProvisioningOrder>> getProvisioningOrder(@PathVariable UUID id) {
        NetworkProvisioningOrder order = provisioningService.getProvisioningOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ProvisioningOrder"));
    }

    @PostMapping("/provisioningOrder/{id}/schedule")
    public ResponseEntity<TmfResponse<NetworkProvisioningOrder>> scheduleOrder(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        
        LocalDateTime installationDate = LocalDateTime.parse((String) request.get("installationDate"));
        String technicianId = (String) request.get("technicianId");
        
        NetworkProvisioningOrder order = provisioningService.scheduleOrder(id, installationDate, technicianId);
        return ResponseEntity.ok(TmfResponse.success(order, "ProvisioningOrder"));
    }

    @PostMapping("/provisioningOrder/{id}/assign")
    public ResponseEntity<TmfResponse<NetworkProvisioningOrder>> assignTechnician(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String technicianId = request.get("technicianId");
        String technicianName = request.get("technicianName");
        
        NetworkProvisioningOrder order = provisioningService.assignTechnician(id, technicianId, technicianName);
        return ResponseEntity.ok(TmfResponse.success(order, "ProvisioningOrder"));
    }

    @PostMapping("/provisioningOrder/{id}/start")
    public ResponseEntity<TmfResponse<NetworkProvisioningOrder>> startWork(@PathVariable UUID id) {
        NetworkProvisioningOrder order = provisioningService.startWork(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ProvisioningOrder"));
    }

    @PostMapping("/provisioningOrder/{id}/complete")
    public ResponseEntity<TmfResponse<NetworkProvisioningOrder>> completeOrder(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String completionNotes = request.get("completionNotes");
        NetworkProvisioningOrder order = provisioningService.completeOrder(id, completionNotes);
        return ResponseEntity.ok(TmfResponse.success(order, "ProvisioningOrder"));
    }

    @PostMapping("/provisioningOrder/{id}/fail")
    public ResponseEntity<TmfResponse<NetworkProvisioningOrder>> failOrder(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String reason = request.get("reason");
        NetworkProvisioningOrder order = provisioningService.failOrder(id, reason);
        return ResponseEntity.ok(TmfResponse.success(order, "ProvisioningOrder"));
    }
}