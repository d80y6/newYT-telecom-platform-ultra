package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.NetworkElement;
import com.yemenptc.bss.coreservice.entity.NetworkResource;
import com.yemenptc.bss.coreservice.entity.NumberPool;
import com.yemenptc.bss.coreservice.service.InventoryService;
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
@RequestMapping("/tmf-api/resourceInventoryManagement/v4")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // Network Element endpoints
    @PostMapping("/resource")
    public ResponseEntity<TmfResponse<NetworkElement>> createElement(@RequestBody NetworkElement request) {
        NetworkElement element = inventoryService.createElement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(element, "Resource"));
    }

    @GetMapping("/resource/{id}")
    public ResponseEntity<TmfResponse<NetworkElement>> getElement(@PathVariable String id) {
        NetworkElement element = inventoryService.getElement(id);
        return ResponseEntity.ok(TmfResponse.success(element, "Resource"));
    }

    @GetMapping("/resource")
    public ResponseEntity<TmfResponse<NetworkElement>> listElements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NetworkElement> result = inventoryService.listElements(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Resource"));
    }

    @GetMapping("/resource/type/{type}")
    public ResponseEntity<TmfResponse<NetworkElement>> getElementsByType(@PathVariable String type) {
        List<NetworkElement> elements = inventoryService.getElementsByType(type);
        return ResponseEntity.ok(TmfResponse.list(
                elements, elements.size(), 0, elements.size(), "Resource"));
    }

    @GetMapping("/resource/site/{siteId}")
    public ResponseEntity<TmfResponse<NetworkElement>> getElementsBySite(@PathVariable String siteId) {
        List<NetworkElement> elements = inventoryService.getElementsBySite(siteId);
        return ResponseEntity.ok(TmfResponse.list(
                elements, elements.size(), 0, elements.size(), "Resource"));
    }

    // Network Resource endpoints
    @PostMapping("/resourceInventory")
    public ResponseEntity<TmfResponse<NetworkResource>> createResource(@RequestBody NetworkResource request) {
        NetworkResource resource = inventoryService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(resource, "ResourceInventory"));
    }

    @GetMapping("/resourceInventory/{id}")
    public ResponseEntity<TmfResponse<NetworkResource>> getResource(@PathVariable UUID id) {
        NetworkResource resource = inventoryService.getResource(id);
        return ResponseEntity.ok(TmfResponse.success(resource, "ResourceInventory"));
    }

    @GetMapping("/resourceInventory")
    public ResponseEntity<TmfResponse<NetworkResource>> listResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NetworkResource> result = inventoryService.listResources(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "ResourceInventory"));
    }

    // Number Pool endpoints
    @PostMapping("/numberPool")
    public ResponseEntity<TmfResponse<NumberPool>> createNumberPool(@RequestBody NumberPool request) {
        NumberPool pool = inventoryService.createNumberPool(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(pool, "NumberPool"));
    }

    @GetMapping("/numberPool/{id}")
    public ResponseEntity<TmfResponse<NumberPool>> getNumberPool(@PathVariable String id) {
        NumberPool pool = inventoryService.getNumberPool(id);
        return ResponseEntity.ok(TmfResponse.success(pool, "NumberPool"));
    }

    @GetMapping("/numberPool")
    public ResponseEntity<TmfResponse<NumberPool>> listNumberPools(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NumberPool> result = inventoryService.listNumberPools(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "NumberPool"));
    }

    @GetMapping("/numberPool/available")
    public ResponseEntity<TmfResponse<NumberPool>> getAvailableNumbers(
            @RequestParam NumberPool.NumberType type,
            @RequestParam String exchange) {
        List<NumberPool> pools = inventoryService.getAvailableNumbers(type, exchange);
        return ResponseEntity.ok(TmfResponse.list(
                pools, pools.size(), 0, pools.size(), "NumberPool"));
    }

    @PostMapping("/numberPool/{id}/reserve")
    public ResponseEntity<TmfResponse<NumberPool>> reserveNumber(@PathVariable String id) {
        NumberPool pool = inventoryService.reserveNumber(id);
        return ResponseEntity.ok(TmfResponse.success(pool, "NumberPool"));
    }

    @PostMapping("/numberPool/{id}/assign")
    public ResponseEntity<TmfResponse<NumberPool>> assignNumber(
            @PathVariable String id, @RequestParam String assignedTo) {
        NumberPool pool = inventoryService.assignNumber(id, assignedTo);
        return ResponseEntity.ok(TmfResponse.success(pool, "NumberPool"));
    }

    @PostMapping("/numberPool/{id}/release")
    public ResponseEntity<TmfResponse<NumberPool>> releaseNumber(@PathVariable String id) {
        NumberPool pool = inventoryService.releaseNumber(id);
        return ResponseEntity.ok(TmfResponse.success(pool, "NumberPool"));
    }
}
