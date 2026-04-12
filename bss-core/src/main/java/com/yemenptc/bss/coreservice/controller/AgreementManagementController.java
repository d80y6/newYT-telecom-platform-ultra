package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Agreement;
import com.yemenptc.bss.coreservice.service.AgreementService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/agreementManagement/v5")
@RequiredArgsConstructor
public class AgreementManagementController {

    private final AgreementService agreementService;

    @GetMapping("/agreement")
    public ResponseEntity<TmfResponse<Agreement>> listAgreements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Agreement> result = agreementService.listAgreements(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
            result.getContent(), (int) result.getTotalElements(),
            page * size, size, "Agreement"));
    }

    @PostMapping("/agreement")
    public ResponseEntity<TmfResponse<Agreement>> createAgreement(@RequestBody Agreement request) {
        Agreement agreement = agreementService.createAgreement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TmfResponse.success(agreement, "Agreement"));
    }

    @GetMapping("/agreement/{id}")
    public ResponseEntity<TmfResponse<Agreement>> getAgreement(@PathVariable UUID id) {
        Agreement agreement = agreementService.getAgreement(id);
        return ResponseEntity.ok(TmfResponse.success(agreement, "Agreement"));
    }

    @PostMapping("/agreement/{id}/terminate")
    public ResponseEntity<TmfResponse<Agreement>> terminateAgreement(@PathVariable UUID id) {
        Agreement agreement = agreementService.terminateAgreement(id);
        return ResponseEntity.ok(TmfResponse.success(agreement, "Agreement"));
    }
}