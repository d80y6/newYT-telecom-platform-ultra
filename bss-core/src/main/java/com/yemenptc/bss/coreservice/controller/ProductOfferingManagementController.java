package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tmf-api/productOfferingManagement/v5")
@RequiredArgsConstructor
public class ProductOfferingManagementController {

    @GetMapping("/productOffering")
    public ResponseEntity<TmfResponse> listOfferings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(TmfResponse.list(new ArrayList<>(), 0, page * size, size, "ProductOffering"));
    }

    @PostMapping("/productOffering")
    public ResponseEntity<TmfResponse> createOffering(@RequestBody Map<String, Object> request) {
        Map<String, Object> offering = new HashMap<>();
        offering.put("id", UUID.randomUUID().toString());
        offering.put("name", request.get("name"));
        offering.put("status", "ACTIVE");
        return ResponseEntity.status(HttpStatus.CREATED).body(TmfResponse.success(offering, "ProductOffering"));
    }

    @GetMapping("/productOffering/{id}")
    public ResponseEntity<TmfResponse> getOffering(@PathVariable String id) {
        Map<String, Object> offering = new HashMap<>();
        offering.put("id", id);
        offering.put("name", "Sample Offering");
        offering.put("status", "ACTIVE");
        return ResponseEntity.ok(TmfResponse.success(offering, "ProductOffering"));
    }

    @PutMapping("/productOffering/{id}")
    public ResponseEntity<TmfResponse> updateOffering(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> offering = new HashMap<>();
        offering.put("id", id);
        offering.put("name", request.get("name"));
        return ResponseEntity.ok(TmfResponse.success(offering, "ProductOffering"));
    }

    @GetMapping("/productOfferingPrice")
    public ResponseEntity<TmfResponse> listPrices() {
        return ResponseEntity.ok(TmfResponse.success(new ArrayList<>(), "ProductOfferingPrice"));
    }
}