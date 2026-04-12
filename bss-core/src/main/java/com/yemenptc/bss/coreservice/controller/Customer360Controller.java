package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.AccountHierarchy;
import com.yemenptc.bss.coreservice.entity.Customer360;
import com.yemenptc.bss.coreservice.entity.CustomerRelationship;
import com.yemenptc.bss.coreservice.entity.CustomerSegment;
import com.yemenptc.bss.coreservice.service.Customer360Service;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/customerManagement/v4")
@RequiredArgsConstructor
public class Customer360Controller {

    private final Customer360Service customer360Service;

    @GetMapping("/customer")
    public ResponseEntity<TmfResponse<Customer360>> listCustomers360(
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Customer360> result = customer360Service.listCustomers360(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(), page, size, "Customer360"));
    }

    @PostMapping("/customer")
    public ResponseEntity<TmfResponse<Customer360>> createCustomer360(@RequestBody Map<String, Object> request) {
        UUID customerId = UUID.fromString((String) request.get("customerId"));
        Customer360 customer360 = customer360Service.createCustomer360(customerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(customer360, "Customer360"));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<TmfResponse<Customer360>> getCustomer360(@PathVariable UUID id) {
        Customer360 customer360 = customer360Service.getCustomer360(id);
        return ResponseEntity.ok(TmfResponse.success(customer360, "Customer360"));
    }

    @GetMapping("/customer/{id}/segment")
    public ResponseEntity<TmfResponse<CustomerSegment>> getCustomerSegments(@PathVariable UUID id) {
        Customer360 c360 = customer360Service.getCustomer360(id);
        var segments = c360.getSegments();
        return ResponseEntity.ok(TmfResponse.list(segments, segments.size(), 0, segments.size(), "CustomerSegment"));
    }

    @PostMapping("/customer/{id}/segment")
    public ResponseEntity<TmfResponse<CustomerSegment>> assignSegment(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        String segmentType = request.get("segmentType");
        String segmentValue = request.get("segmentValue");
        CustomerSegment segment = customer360Service.assignSegment(id, segmentType, segmentValue);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(segment, "CustomerSegment"));
    }

    @GetMapping("/customer/{id}/hierarchy")
    public ResponseEntity<TmfResponse<AccountHierarchy>> getAccountHierarchy(@PathVariable UUID id) {
        Customer360 c360 = customer360Service.getCustomer360(id);
        var hierarchy = c360.getAccountHierarchy();
        return ResponseEntity.ok(TmfResponse.list(hierarchy, hierarchy.size(), 0, hierarchy.size(), "AccountHierarchy"));
    }

    @PostMapping("/customer/{id}/hierarchy")
    public ResponseEntity<TmfResponse<AccountHierarchy>> addAccountRelationship(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        String relatedAccountId = request.get("relatedAccountId");
        String relationshipType = request.get("relationshipType");
        AccountHierarchy.RelationshipType relType = AccountHierarchy.RelationshipType.valueOf(relationshipType);
        AccountHierarchy hierarchy = customer360Service.addAccountRelationship(id, relatedAccountId, relType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(hierarchy, "AccountHierarchy"));
    }

    @PatchMapping("/customer/{id}/engagement")
    public ResponseEntity<TmfResponse<Customer360>> updateEngagement(
            @PathVariable UUID id,
            @RequestBody Map<String, Integer> request) {
        Integer score = request.get("engagementScore");
        Customer360 c360 = customer360Service.updateEngagementScore(id, score);
        return ResponseEntity.ok(TmfResponse.success(c360, "Customer360"));
    }

    // TMF629 Additional Endpoints

    @GetMapping("/customer/{id}/dashboard")
    public ResponseEntity<TmfResponse<Map<String, Object>>> getCustomerDashboard(@PathVariable UUID id) {
        Customer360 c360 = customer360Service.getCustomer360(id);
        // Build dashboard summary - simplified for now
        Map<String, Object> dashboard = Map.of(
                "customerId", id,
                "profileCompleteScore", c360.getProfileCompleteScore(),
                "engagementScore", c360.getEngagementScore(),
                "totalRevenue", c360.getTotalRevenue(),
                "monthlyAvgRevenue", c360.getMonthlyAvgRevenue(),
                "creditScore", c360.getCreditScore(),
                "riskLevel", c360.getRiskLevel()
        );
        return ResponseEntity.ok(TmfResponse.success(dashboard, "CustomerDashboard"));
    }

    @GetMapping("/customer/{id}/creditProfile")
    public ResponseEntity<TmfResponse<Map<String, Object>>> getCreditProfile(@PathVariable UUID id) {
        Customer360 c360 = customer360Service.getCustomer360(id);
        Map<String, Object> creditProfile = Map.of(
                "creditScore", c360.getCreditScore(),
                "creditLimit", c360.getCreditLimit(),
                "availableCredit", c360.getAvailableCredit(),
                "creditStatus", c360.getCreditStatus(),
                "riskLevel", c360.getRiskLevel(),
                "lastReviewDate", c360.getLastCreditReviewDate(),
                "onTimePaymentRate", c360.getOnTimePaymentRate(),
                "latePaymentCount", c360.getLatePaymentCount(),
                "defaultCount", c360.getDefaultCount(),
                "daysPastDue", c360.getDaysPastDue()
        );
        return ResponseEntity.ok(TmfResponse.success(creditProfile, "CreditProfile"));
    }

    @PatchMapping("/customer/{id}/creditProfile")
    public ResponseEntity<TmfResponse<Map<String, Object>>> updateCreditProfile(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        Integer creditScore = (Integer) request.get("creditScore");
        BigDecimal creditLimit = (BigDecimal) request.get("creditLimit");
        String creditStatus = (String) request.get("creditStatus");
        String riskLevel = (String) request.get("riskLevel");

        Customer360 updated = customer360Service.updateCreditProfile(id, creditScore, creditLimit, creditStatus, riskLevel);

        Map<String, Object> creditProfile = Map.of(
                "creditScore", updated.getCreditScore(),
                "creditLimit", updated.getCreditLimit(),
                "availableCredit", updated.getAvailableCredit(),
                "creditStatus", updated.getCreditStatus(),
                "riskLevel", updated.getRiskLevel(),
                "lastReviewDate", updated.getLastCreditReviewDate(),
                "onTimePaymentRate", updated.getOnTimePaymentRate(),
                "latePaymentCount", updated.getLatePaymentCount(),
                "defaultCount", updated.getDefaultCount(),
                "daysPastDue", updated.getDaysPastDue()
        );
        return ResponseEntity.ok(TmfResponse.success(creditProfile, "CreditProfile"));
    }

    @GetMapping("/customer/{id}/relationships")
    public ResponseEntity<TmfResponse<CustomerRelationship>> getCustomerRelationships(@PathVariable UUID id) {
        Customer360 c360 = customer360Service.getCustomer360(id);
        List<CustomerRelationship> relationships = c360.getCustomerRelationships();
        return ResponseEntity.ok(TmfResponse.<CustomerRelationship>list(relationships, relationships.size(), 0, relationships.size(), "CustomerRelationship"));
    }

    @PostMapping("/customer/{id}/relationships")
    public ResponseEntity<TmfResponse<CustomerRelationship>> addCustomerRelationship(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        String relatedCustomerId = request.get("relatedCustomerId");
        String relationshipType = request.get("relationshipType");
        CustomerRelationship relationship = customer360Service.addCustomerRelationship(id, relatedCustomerId, relationshipType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(relationship, "CustomerRelationship"));
    }

    @GetMapping("/customer/{id}/usage")
    public ResponseEntity<TmfResponse<Map<String, Object>>> getCustomerUsageStats(
            @PathVariable UUID id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String serviceType) {
        // This would integrate with UsageManagementService - simplified for now
        Map<String, Object> usageStats = Map.of(
                "customerId", id,
                "period", period != null ? period : "MONTH",
                "serviceType", serviceType,
                "voiceMinutes", 0,
                "dataUsageGB", 0,
                "smsCount", 0,
                "trends", List.of()
        );
        return ResponseEntity.ok(TmfResponse.success(usageStats, "UsageStatistics"));
    }

    @GetMapping("/customer/{id}/billing")
    public ResponseEntity<TmfResponse<Map<String, Object>>> getCustomerBillingSummary(@PathVariable UUID id) {
        // This would integrate with billing service - simplified for now
        Map<String, Object> billingSummary = Map.of(
                "customerId", id,
                "currentBalance", BigDecimal.ZERO,
                "overdueAmount", BigDecimal.ZERO,
                "lastPaymentDate", null,
                "lastPaymentAmount", BigDecimal.ZERO,
                "averageMonthlyBill", BigDecimal.ZERO,
                "paymentHistory", List.of()
        );
        return ResponseEntity.ok(TmfResponse.success(billingSummary, "BillingSummary"));
    }

    @GetMapping("/customer/{id}/preferences")
    public ResponseEntity<TmfResponse<Map<String, Object>>> getCustomerPreferences(@PathVariable UUID id) {
        Customer360 c360 = customer360Service.getCustomer360(id);
        Map<String, Object> preferences = Map.of(
                "language", c360.getLanguagePreference(),
                "preferredContactMethod", c360.getPreferredContactMethod(),
                "marketingConsent", c360.getMarketingConsent(),
                "communicationPreferences", c360.getCommunicationPreferences(),
                "privacySettings", c360.getPrivacySettings()
        );
        return ResponseEntity.ok(TmfResponse.success(preferences, "CustomerPreferences"));
    }

    @PatchMapping("/customer/{id}/preferences")
    public ResponseEntity<TmfResponse<Map<String, Object>>> updateCustomerPreferences(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        // This would update preferences - simplified for now
        Map<String, Object> preferences = Map.of(
                "language", request.get("language"),
                "preferredContactMethod", request.get("preferredContactMethod"),
                "marketingConsent", request.get("marketingConsent"),
                "communicationPreferences", request.get("communicationPreferences"),
                "privacySettings", request.get("privacySettings")
        );
        return ResponseEntity.ok(TmfResponse.success(preferences, "CustomerPreferences"));
    }

    @GetMapping("/customer/search")
    public ResponseEntity<TmfResponse<Customer360>> searchCustomers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minLifetimeValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Customer360> result;
        if (query != null) {
            result = customer360Service.listCustomers360(PageRequest.of(page, size));
        } else {
            result = customer360Service.listCustomers360(PageRequest.of(page, size));
        }
        return ResponseEntity.ok(TmfResponse.<Customer360>list(
                result.getContent(), (int) result.getTotalElements(), page, size, "Customer360"));
    }

    @GetMapping("/customer/statistics")
    public ResponseEntity<TmfResponse<Map<String, Object>>> getCustomerStatistics(
            @RequestParam(required = false) String period) {
        // This would provide customer statistics - simplified for now
        Map<String, Object> statistics = Map.of(
                "totalCustomers", 0,
                "activeCustomers", 0,
                "newCustomers", 0,
                "churnedCustomers", 0,
                "bySegment", Map.of(),
                "byType", Map.of(),
                "averageLifetimeValue", BigDecimal.ZERO
        );
        return ResponseEntity.ok(TmfResponse.success(statistics, "CustomerStatistics"));
    }
}