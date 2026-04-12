package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tmf-api/campaignManagement/v5")
@RequiredArgsConstructor
public class CampaignManagementController {

    @GetMapping("/campaign")
    public ResponseEntity<TmfResponse> listCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(TmfResponse.list(new ArrayList<>(), 0, page * size, size, "Campaign"));
    }

    @PostMapping("/campaign")
    public ResponseEntity<TmfResponse> createCampaign(@RequestBody Map<String, Object> request) {
        Map<String, Object> campaign = new HashMap<>();
        campaign.put("id", UUID.randomUUID().toString());
        campaign.put("name", request.get("name"));
        campaign.put("campaignType", request.getOrDefault("campaignType", "PROMOTIONAL"));
        campaign.put("status", "DRAFT");
        return ResponseEntity.status(HttpStatus.CREATED).body(TmfResponse.success(campaign, "Campaign"));
    }

    @GetMapping("/campaign/{id}")
    public ResponseEntity<TmfResponse> getCampaign(@PathVariable String id) {
        Map<String, Object> campaign = new HashMap<>();
        campaign.put("id", id);
        campaign.put("name", "Sample Campaign");
        campaign.put("campaignType", "PROMOTIONAL");
        campaign.put("status", "ACTIVE");
        return ResponseEntity.ok(TmfResponse.success(campaign, "Campaign"));
    }

    @PatchMapping("/campaign/{id}")
    public ResponseEntity<TmfResponse> updateCampaign(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> campaign = new HashMap<>();
        campaign.put("id", id);
        return ResponseEntity.ok(TmfResponse.success(campaign, "Campaign"));
    }

    @GetMapping("/campaign/{id}/offer")
    public ResponseEntity<TmfResponse> getCampaignOffers(@PathVariable String id) {
        return ResponseEntity.ok(TmfResponse.success(new ArrayList<>(), "CampaignOffer"));
    }

    @PostMapping("/campaign/{id}/trigger")
    public ResponseEntity<TmfResponse> triggerCampaign(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        result.put("campaignId", id);
        result.put("triggeredAt", new Date().toString());
        return ResponseEntity.ok(TmfResponse.success(result, "CampaignTrigger"));
    }

    @GetMapping("/lead")
    public ResponseEntity<TmfResponse> listLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(TmfResponse.list(new ArrayList<>(), 0, page * size, size, "Lead"));
    }

    @PostMapping("/lead")
    public ResponseEntity<TmfResponse> createLead(@RequestBody Map<String, Object> request) {
        Map<String, Object> lead = new HashMap<>();
        lead.put("id", UUID.randomUUID().toString());
        lead.put("name", request.get("name"));
        lead.put("status", "NEW");
        return ResponseEntity.status(HttpStatus.CREATED).body(TmfResponse.success(lead, "Lead"));
    }
}