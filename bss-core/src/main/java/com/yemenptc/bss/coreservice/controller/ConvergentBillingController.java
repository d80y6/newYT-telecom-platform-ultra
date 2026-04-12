package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ConvergentBillingAccount;
import com.yemenptc.bss.coreservice.service.ConvergentBillingService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/tmf-api/convergentBilling/v5")
@RequiredArgsConstructor
public class ConvergentBillingController {

    private final ConvergentBillingService convergentBillingService;

    @PostMapping("/account")
    public ResponseEntity<TmfResponse<ConvergentBillingAccount>> createAccount(@RequestBody ConvergentBillingAccount request) {
        ConvergentBillingAccount account = convergentBillingService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(account, "ConvergentBillingAccount"));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<TmfResponse<ConvergentBillingAccount>> getAccount(@PathVariable String accountId) {
        ConvergentBillingAccount account = convergentBillingService.getAccount(accountId);
        return ResponseEntity.ok(TmfResponse.success(account, "ConvergentBillingAccount"));
    }

    @GetMapping("/account/{accountId}/summary")
    public ResponseEntity<TmfResponse<Map>> getAccountSummary(@PathVariable String accountId) {
        Map summary = convergentBillingService.getAccountSummary(accountId);
        return ResponseEntity.ok(TmfResponse.success(summary, "AccountSummary"));
    }

    @PostMapping("/account/{accountId}/recharge")
    public ResponseEntity<TmfResponse<Map>> recharge(
            @PathVariable String accountId,
            @RequestBody Map<String, BigDecimal> request) {
        
        BigDecimal amount = request.get("amount");
        Map result = convergentBillingService.recharge(accountId, amount);
        return ResponseEntity.ok(TmfResponse.success(result, "Recharge"));
    }

    @PostMapping("/account/{accountId}/charge")
    public ResponseEntity<TmfResponse<Map>> charge(
            @PathVariable String accountId,
            @RequestBody Map<String, Object> request) {
        
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String serviceType = (String) request.get("serviceType");
        
        Map result = convergentBillingService.charge(accountId, amount, serviceType);
        return ResponseEntity.ok(TmfResponse.success(result, "Charge"));
    }

    @PostMapping("/account/{accountId}/payment")
    public ResponseEntity<TmfResponse<Map>> processPayment(
            @PathVariable String accountId,
            @RequestBody Map<String, BigDecimal> request) {
        
        BigDecimal amount = request.get("amount");
        Map result = convergentBillingService.processPayment(accountId, amount);
        return ResponseEntity.ok(TmfResponse.success(result, "Payment"));
    }
}