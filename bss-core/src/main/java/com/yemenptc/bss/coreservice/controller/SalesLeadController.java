package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.SalesLead;
import com.yemenptc.bss.coreservice.service.SalesLeadService;
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
@RequestMapping("/tmf-api/salesManagement/v5")
@RequiredArgsConstructor
@Tag(name = "Sales Management", description = "TMF699 Sales Management API")
public class SalesLeadController {

    private final SalesLeadService salesLeadService;

    @PostMapping("/lead")
    @Operation(summary = "Create sales lead", description = "Creates a new sales lead")
    public ResponseEntity<SalesLead> createLead(@RequestBody SalesLead request) {
        SalesLead lead = salesLeadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(lead);
    }

    @GetMapping("/lead")
    @Operation(summary = "List sales leads", description = "Retrieves all sales leads with pagination")
    public ResponseEntity<Page<SalesLead>> listLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SalesLead> leads = salesLeadService.listLeads(PageRequest.of(page, size));
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/lead/{id}")
    @Operation(summary = "Get sales lead", description = "Retrieves a sales lead by ID")
    public ResponseEntity<SalesLead> getLead(@PathVariable UUID id) {
        SalesLead lead = salesLeadService.getLead(id);
        return ResponseEntity.ok(lead);
    }

    @DeleteMapping("/lead/{id}")
    @Operation(summary = "Delete sales lead", description = "Deletes a sales lead")
    public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
        salesLeadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/lead/{id}/status")
    @Operation(summary = "Update lead status", description = "Updates lead status")
    public ResponseEntity<SalesLead> updateLeadStatus(
            @PathVariable UUID id, 
            @RequestBody SalesLead.LeadStatus status) {
        SalesLead lead = salesLeadService.updateLeadStatus(id, status);
        return ResponseEntity.ok(lead);
    }

    @PatchMapping("/lead/{id}/assign")
    @Operation(summary = "Assign lead", description = "Assigns a lead to a user")
    public ResponseEntity<SalesLead> assignLead(
            @PathVariable UUID id, 
            @RequestBody UUID assignedTo) {
        SalesLead lead = salesLeadService.assignLead(id, assignedTo);
        return ResponseEntity.ok(lead);
    }

    @GetMapping("/lead/status/{status}")
    @Operation(summary = "Get leads by status", description = "Retrieves leads by status")
    public ResponseEntity<List<SalesLead>> getLeadsByStatus(@PathVariable SalesLead.LeadStatus status) {
        List<SalesLead> leads = salesLeadService.getLeadsByStatus(status);
        return ResponseEntity.ok(leads);
    }
}
