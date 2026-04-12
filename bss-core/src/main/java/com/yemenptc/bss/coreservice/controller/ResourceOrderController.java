package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ResourceOrder;
import com.yemenptc.bss.coreservice.service.ResourceOrderService;
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
@RequestMapping("/tmf-api/resourceOrderingManagement/v4")
@RequiredArgsConstructor
public class ResourceOrderController {

    private final ResourceOrderService resourceOrderService;

    @GetMapping("/resourceOrder")
    public ResponseEntity<TmfResponse<ResourceOrder>> listResourceOrders(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String resourceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<ResourceOrder> result;
        if (state != null) {
            result = resourceOrderService.findByState(ResourceOrder.ResourceOrderState.valueOf(state), PageRequest.of(page, size));
        } else {
            result = resourceOrderService.listResourceOrders(PageRequest.of(page, size));
        }
        
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(), page, size, "ResourceOrder"));
    }

    @PostMapping("/resourceOrder")
    public ResponseEntity<TmfResponse<ResourceOrder>> createResourceOrder(@RequestBody ResourceOrder request) {
        ResourceOrder order = resourceOrderService.createResourceOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(order, "ResourceOrder"));
    }

    @GetMapping("/resourceOrder/{id}")
    public ResponseEntity<TmfResponse<ResourceOrder>> getResourceOrder(@PathVariable UUID id) {
        ResourceOrder order = resourceOrderService.getResourceOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ResourceOrder"));
    }

    @PatchMapping("/resourceOrder/{id}")
    public ResponseEntity<TmfResponse<ResourceOrder>> updateResourceOrder(
            @PathVariable UUID id,
            @RequestBody ResourceOrder request) {
        ResourceOrder order = resourceOrderService.getResourceOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ResourceOrder"));
    }

    @PostMapping("/resourceOrder/{id}/stateChange")
    public ResponseEntity<TmfResponse<ResourceOrder>> changeResourceOrderState(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String targetState = request.get("targetState");
        ResourceOrder.ResourceOrderState newState = ResourceOrder.ResourceOrderState.valueOf(targetState);
        
        ResourceOrder order = resourceOrderService.updateState(id, newState);
        return ResponseEntity.ok(TmfResponse.success(order, "ResourceOrder"));
    }

    @PostMapping("/resourceOrder/{id}/acknowledge")
    public ResponseEntity<TmfResponse<ResourceOrder>> acknowledgeOrder(@PathVariable UUID id) {
        ResourceOrder order = resourceOrderService.acknowledgeOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ResourceOrder"));
    }

    @PostMapping("/resourceOrder/{id}/start")
    public ResponseEntity<TmfResponse<ResourceOrder>> startOrder(@PathVariable UUID id) {
        ResourceOrder order = resourceOrderService.startOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ResourceOrder"));
    }

    @PostMapping("/resourceOrder/{id}/complete")
    public ResponseEntity<TmfResponse<ResourceOrder>> completeOrder(@PathVariable UUID id) {
        ResourceOrder order = resourceOrderService.completeOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ResourceOrder"));
    }

    @PostMapping("/resourceOrder/{id}/cancel")
    public ResponseEntity<TmfResponse<ResourceOrder>> cancelOrder(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        ResourceOrder order = resourceOrderService.cancelOrder(id, reason);
        return ResponseEntity.ok(TmfResponse.success(order, "ResourceOrder"));
    }
}