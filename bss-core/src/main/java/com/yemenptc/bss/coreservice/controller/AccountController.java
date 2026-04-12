package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Account;
import com.yemenptc.bss.coreservice.service.AccountService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/customerBillManagement/v5/billingAccount")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<TmfResponse<Account>> create(@RequestBody Account request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(account, "BillingAccount"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TmfResponse<Account>> getById(@PathVariable UUID id) {
        Account account = accountService.getAccount(id);
        return ResponseEntity.ok(TmfResponse.success(account, "BillingAccount"));
    }

    @GetMapping
    public ResponseEntity<TmfResponse<Account>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Account> result = accountService.listAccounts(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "BillingAccount"));
    }

    @PostMapping("/{id}/add-funds")
    public ResponseEntity<TmfResponse<Account>> addFunds(
            @PathVariable UUID id, @RequestParam BigDecimal amount) {
        Account account = accountService.addFunds(id, amount);
        return ResponseEntity.ok(TmfResponse.success(account, "BillingAccount"));
    }

    @PostMapping("/{id}/deduct-funds")
    public ResponseEntity<TmfResponse<Account>> deductFunds(
            @PathVariable UUID id, @RequestParam BigDecimal amount) {
        Account account = accountService.deductFunds(id, amount);
        return ResponseEntity.ok(TmfResponse.success(account, "BillingAccount"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
