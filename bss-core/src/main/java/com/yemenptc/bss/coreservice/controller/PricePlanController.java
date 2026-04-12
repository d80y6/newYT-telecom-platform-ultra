package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.PricePlan;
import com.yemenptc.bss.coreservice.service.PricePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/priceRecurringSpecification/v5")
@RequiredArgsConstructor
@Tag(name = "Price Recurring Specification", description = "TMF655 Price Recurring Specification API")
public class PricePlanController {

    private final PricePlanService pricePlanService;

    @PostMapping("/pricePlan")
    @Operation(summary = "Create price plan", description = "Creates a new price plan")
    public ResponseEntity<PricePlan> createPricePlan(@RequestBody PricePlan request) {
        PricePlan plan = pricePlanService.createPricePlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    @GetMapping("/pricePlan")
    @Operation(summary = "List price plans", description = "Retrieves all price plans with pagination")
    public ResponseEntity<Page<PricePlan>> listPricePlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PricePlan> plans = pricePlanService.listPricePlans(PageRequest.of(page, size));
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/pricePlan/{id}")
    @Operation(summary = "Get price plan", description = "Retrieves a price plan by ID")
    public ResponseEntity<PricePlan> getPricePlan(@PathVariable UUID id) {
        PricePlan plan = pricePlanService.getPricePlan(id);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/pricePlan/{id}")
    @Operation(summary = "Update price plan", description = "Updates an existing price plan")
    public ResponseEntity<PricePlan> updatePricePlan(
            @PathVariable UUID id, 
            @RequestBody PricePlan request) {
        PricePlan plan = pricePlanService.updatePricePlan(id, request);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/pricePlan/{id}")
    @Operation(summary = "Delete price plan", description = "Deletes a price plan")
    public ResponseEntity<Void> deletePricePlan(@PathVariable UUID id) {
        pricePlanService.deletePricePlan(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/pricePlan/{id}/deactivate")
    @Operation(summary = "Deactivate price plan", description = "Deactivates a price plan")
    public ResponseEntity<PricePlan> deactivatePricePlan(@PathVariable UUID id) {
        PricePlan plan = pricePlanService.deactivatePricePlan(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/pricePlan/active")
    @Operation(summary = "Get active price plans", description = "Retrieves all active price plans")
    public ResponseEntity<List<PricePlan>> getActivePricePlans() {
        List<PricePlan> plans = pricePlanService.getActivePricePlans();
        return ResponseEntity.ok(plans);
    }
}
