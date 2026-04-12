package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tmf-api/productConfigurationManagement/v5")
@RequiredArgsConstructor
public class ProductConfigurationManagementController {

    @GetMapping("/productConfiguration")
    public ResponseEntity<TmfResponse> listConfigurations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(TmfResponse.list(new ArrayList<>(), 0, page * size, size, "ProductConfiguration"));
    }

    @PostMapping("/productConfiguration")
    public ResponseEntity<TmfResponse> createConfiguration(@RequestBody Map<String, Object> request) {
        Map<String, Object> config = new HashMap<>();
        config.put("id", UUID.randomUUID().toString());
        config.put("name", request.get("name"));
        return ResponseEntity.status(HttpStatus.CREATED).body(TmfResponse.success(config, "ProductConfiguration"));
    }

    @GetMapping("/productConfiguration/{id}")
    public ResponseEntity<TmfResponse> getConfiguration(@PathVariable String id) {
        Map<String, Object> config = new HashMap<>();
        config.put("id", id);
        config.put("name", "Sample Config");
        return ResponseEntity.ok(TmfResponse.success(config, "ProductConfiguration"));
    }

    @PutMapping("/productConfiguration/{id}")
    public ResponseEntity<TmfResponse> updateConfiguration(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> config = new HashMap<>();
        config.put("id", id);
        return ResponseEntity.ok(TmfResponse.success(config, "ProductConfiguration"));
    }

    @GetMapping("/bundlingRule")
    public ResponseEntity<TmfResponse> listBundlingRules() {
        return ResponseEntity.ok(TmfResponse.success(new ArrayList<>(), "BundlingRule"));
    }

    @PostMapping("/bundlingRule")
    public ResponseEntity<TmfResponse> createBundlingRule(@RequestBody Map<String, Object> request) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("id", UUID.randomUUID().toString());
        rule.put("bundleType", request.getOrDefault("bundleType", "SIMPLE_BUNDLE"));
        return ResponseEntity.status(HttpStatus.CREATED).body(TmfResponse.success(rule, "BundlingRule"));
    }
}