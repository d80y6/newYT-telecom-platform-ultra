package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import com.yemenptc.bss.coreservice.service.ServiceOrderService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/serviceOrderingManagement/v4/serviceOrder")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @PostMapping
    public ResponseEntity<TmfResponse<ServiceOrder>> create(@RequestBody ServiceOrder request) {
        ServiceOrder order = serviceOrderService.createServiceOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(order, "ServiceOrder"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TmfResponse<ServiceOrder>> getById(@PathVariable UUID id) {
        ServiceOrder order = serviceOrderService.getServiceOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }

    @GetMapping
    public ResponseEntity<TmfResponse<ServiceOrder>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ServiceOrder> result = serviceOrderService.listServiceOrders(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "ServiceOrder"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TmfResponse<ServiceOrder>> update(
            @PathVariable UUID id, @RequestBody ServiceOrder request) {
        ServiceOrder order = serviceOrderService.updateServiceOrder(id, request);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }

    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<TmfResponse<ServiceOrder>> acknowledge(@PathVariable UUID id) {
        ServiceOrder order = serviceOrderService.acknowledgeOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<TmfResponse<ServiceOrder>> start(@PathVariable UUID id) {
        ServiceOrder order = serviceOrderService.startOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TmfResponse<ServiceOrder>> complete(@PathVariable UUID id) {
        ServiceOrder order = serviceOrderService.completeOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TmfResponse<ServiceOrder>> cancel(
            @PathVariable UUID id, @RequestParam String reason) {
        ServiceOrder order = serviceOrderService.cancelOrder(id, reason);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }
}