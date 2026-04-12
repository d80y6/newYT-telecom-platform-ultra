package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.service.CustomerPortalService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tmf-api/customerPortal/v5")
@RequiredArgsConstructor
public class CustomerPortalController {

    private final CustomerPortalService portalService;

    @PostMapping("/login")
    public ResponseEntity<TmfResponse<Map>> login(@RequestBody Map<String, String> request) {
        String customerId = request.get("customerId");
        String ipAddress = request.get("ipAddress");
        String userAgent = request.get("userAgent");
        
        Map result = portalService.login(customerId, ipAddress, userAgent);
        return ResponseEntity.ok(TmfResponse.success(result, "Session"));
    }

    @PostMapping("/logout")
    public ResponseEntity<TmfResponse<Map>> logout(@RequestBody Map<String, String> request) {
        String sessionToken = request.get("sessionToken");
        portalService.logout(sessionToken);
        return ResponseEntity.ok(TmfResponse.success(Map.of("status", "logged_out"), "Session"));
    }

    @GetMapping("/dashboard/{customerId}")
    public ResponseEntity<TmfResponse<Map>> getDashboard(@PathVariable String customerId) {
        Map dashboard = portalService.getCustomerDashboard(customerId);
        return ResponseEntity.ok(TmfResponse.success(dashboard, "Dashboard"));
    }

    @PostMapping("/plan/change")
    public ResponseEntity<TmfResponse<Map>> requestPlanChange(@RequestBody Map<String, String> request) {
        String customerId = request.get("customerId");
        String newPlanId = request.get("newPlanId");
        
        Map result = portalService.requestPlanChange(customerId, newPlanId);
        return ResponseEntity.ok(TmfResponse.success(result, "Action"));
    }

    @PostMapping("/service/suspend")
    public ResponseEntity<TmfResponse<Map>> requestServiceSuspend(@RequestBody Map<String, String> request) {
        String customerId = request.get("customerId");
        String reason = request.get("reason");
        
        Map result = portalService.requestServiceSuspend(customerId, reason);
        return ResponseEntity.ok(TmfResponse.success(result, "Action"));
    }

    @GetMapping("/usage/{customerId}")
    public ResponseEntity<TmfResponse<Map>> getUsageDetails(@PathVariable String customerId) {
        Map usage = portalService.getUsageDetails(customerId);
        return ResponseEntity.ok(TmfResponse.success(usage, "Usage"));
    }

    @GetMapping("/bill/{customerId}")
    public ResponseEntity<TmfResponse<Map>> getBillDetails(@PathVariable String customerId) {
        Map bill = portalService.getBillDetails(customerId);
        return ResponseEntity.ok(TmfResponse.success(bill, "Bill"));
    }
}