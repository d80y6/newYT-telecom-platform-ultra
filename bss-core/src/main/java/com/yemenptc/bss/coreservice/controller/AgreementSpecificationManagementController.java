package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tmf-api/agreementSpecificationManagement/v5")
@RequiredArgsConstructor
public class AgreementSpecificationManagementController {

    @GetMapping("/agreementSpecification")
    public ResponseEntity<TmfResponse> listAgreementSpecs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(TmfResponse.list(new ArrayList<>(), 0, page * size, size, "AgreementSpecification"));
    }

    @PostMapping("/agreementSpecification")
    public ResponseEntity<TmfResponse> createAgreementSpec(@RequestBody Map<String, Object> request) {
        Map<String, Object> spec = new HashMap<>();
        spec.put("id", UUID.randomUUID().toString());
        spec.put("name", request.get("name"));
        spec.put("description", request.get("description"));
        return ResponseEntity.status(HttpStatus.CREATED).body(TmfResponse.success(spec, "AgreementSpecification"));
    }

    @GetMapping("/agreementSpecification/{id}")
    public ResponseEntity<TmfResponse> getAgreementSpec(@PathVariable String id) {
        Map<String, Object> spec = new HashMap<>();
        spec.put("id", id);
        spec.put("name", "Standard Contract");
        return ResponseEntity.ok(TmfResponse.success(spec, "AgreementSpecification"));
    }

    @GetMapping("/template")
    public ResponseEntity<TmfResponse> listTemplates() {
        return ResponseEntity.ok(TmfResponse.success(new ArrayList<>(), "AgreementTemplate"));
    }
}