package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Invoice;
import com.yemenptc.bss.coreservice.entity.Payment;
import com.yemenptc.bss.coreservice.service.BillingService;
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
@RequestMapping("/tmf-api/customerBillManagement/v5")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    // Invoice endpoints
    @PostMapping("/invoice")
    public ResponseEntity<TmfResponse<Invoice>> createInvoice(@RequestBody Invoice request) {
        Invoice invoice = billingService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(invoice, "Invoice"));
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<TmfResponse<Invoice>> getInvoice(@PathVariable UUID id) {
        Invoice invoice = billingService.getInvoice(id);
        return ResponseEntity.ok(TmfResponse.success(invoice, "Invoice"));
    }

    @GetMapping("/invoice")
    public ResponseEntity<TmfResponse<Invoice>> listInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Invoice> result = billingService.listInvoices(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Invoice"));
    }

    @PostMapping("/invoice/{id}/finalize")
    public ResponseEntity<TmfResponse<Invoice>> finalizeInvoice(@PathVariable UUID id) {
        Invoice invoice = billingService.finalizeInvoice(id);
        return ResponseEntity.ok(TmfResponse.success(invoice, "Invoice"));
    }

    @GetMapping("/invoice/account/{accountId}")
    public ResponseEntity<TmfResponse<Invoice>> getInvoicesByAccount(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Invoice> result = billingService.getInvoicesByAccount(accountId, PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Invoice"));
    }

    // Payment endpoints
    @PostMapping("/payment")
    public ResponseEntity<TmfResponse<Payment>> createPayment(@RequestBody Payment request) {
        Payment payment = billingService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(payment, "Payment"));
    }

    @PostMapping("/payment/{id}/process")
    public ResponseEntity<TmfResponse<Payment>> processPayment(@PathVariable UUID id) {
        Payment payment = billingService.processPayment(id);
        return ResponseEntity.ok(TmfResponse.success(payment, "Payment"));
    }

    @GetMapping("/payment/{id}")
    public ResponseEntity<TmfResponse<Payment>> getPayment(@PathVariable UUID id) {
        Payment payment = billingService.getPayment(id);
        return ResponseEntity.ok(TmfResponse.success(payment, "Payment"));
    }

    @GetMapping("/payment")
    public ResponseEntity<TmfResponse<Payment>> listPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Payment> result = billingService.listPayments(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Payment"));
    }

    @GetMapping("/payment/account/{accountId}")
    public ResponseEntity<TmfResponse<Payment>> getPaymentsByAccount(@PathVariable UUID accountId) {
        List<Payment> payments = billingService.getPaymentsByAccount(accountId);
        return ResponseEntity.ok(TmfResponse.list(
                payments, payments.size(), 0, payments.size(), "Payment"));
    }
}
