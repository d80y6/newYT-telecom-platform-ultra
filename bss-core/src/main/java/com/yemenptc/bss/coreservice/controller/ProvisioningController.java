package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import com.yemenptc.bss.coreservice.service.ProvisioningService;
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
@RequestMapping("/tmf-api/serviceOrderingManagement/v4/serviceOrder")
@RequiredArgsConstructor
public class ProvisioningController {

    private final ProvisioningService provisioningService;

    @PostMapping
    public ResponseEntity<TmfResponse<ServiceOrder>> create(@RequestBody ServiceOrder request) {
        ServiceOrder order = provisioningService.createServiceOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(order, "ServiceOrder"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TmfResponse<ServiceOrder>> getById(@PathVariable UUID id) {
        ServiceOrder order = provisioningService.getServiceOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }

    @GetMapping
    public ResponseEntity<TmfResponse<ServiceOrder>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ServiceOrder> result = provisioningService.listServiceOrders(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "ServiceOrder"));
    }

    @GetMapping("/party/{partyId}")
    public ResponseEntity<TmfResponse<ServiceOrder>> getByParty(@PathVariable UUID partyId) {
        List<ServiceOrder> orders = provisioningService.getOrdersByParty(partyId);
        return ResponseEntity.ok(TmfResponse.list(
                orders, orders.size(), 0, orders.size(), "ServiceOrder"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TmfResponse<ServiceOrder>> updateStatus(
            @PathVariable UUID id, @RequestBody ServiceOrder request) {
        ServiceOrder order = provisioningService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TmfResponse<ServiceOrder>> complete(@PathVariable UUID id) {
        ServiceOrder order = provisioningService.completeOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }

    @PostMapping("/{id}/fail")
    public ResponseEntity<TmfResponse<ServiceOrder>> fail(
            @PathVariable UUID id, @RequestBody String errorMessage) {
        ServiceOrder order = provisioningService.failOrder(id, errorMessage);
        return ResponseEntity.ok(TmfResponse.success(order, "ServiceOrder"));
    }
}
