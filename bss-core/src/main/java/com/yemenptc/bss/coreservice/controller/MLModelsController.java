package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ChurnPrediction;
import com.yemenptc.bss.coreservice.service.ChurnPredictionService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tmf-api/mlModels/v5")
@RequiredArgsConstructor
public class MLModelsController {

    private final ChurnPredictionService churnPredictionService;

    @PostMapping("/churnPrediction/predict")
    public ResponseEntity<TmfResponse<ChurnPrediction>> predictChurn(@RequestBody Map<String, String> request) {
        String customerId = request.get("customerId");
        ChurnPrediction prediction = churnPredictionService.predictChurn(customerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(prediction, "ChurnPrediction"));
    }

    @GetMapping("/churnPrediction/{customerId}")
    public ResponseEntity<TmfResponse<ChurnPrediction>> getPrediction(@PathVariable String customerId) {
        ChurnPrediction prediction = churnPredictionService.getPrediction(customerId);
        return ResponseEntity.ok(TmfResponse.success(prediction, "ChurnPrediction"));
    }

    @GetMapping("/churnPrediction/highRisk/list")
    public ResponseEntity<TmfResponse<ChurnPrediction>> getHighRiskCustomers(
            @RequestParam(defaultValue = "10") int limit) {
        List<ChurnPrediction> predictions = churnPredictionService.getHighRiskCustomers(limit);
        return ResponseEntity.ok(TmfResponse.list(predictions, predictions.size(), 0, predictions.size(), "ChurnPrediction"));
    }

    @GetMapping("/churnPrediction/analytics")
    public ResponseEntity<TmfResponse<Map>> getChurnAnalytics() {
        Map analytics = churnPredictionService.getChurnAnalytics();
        return ResponseEntity.ok(TmfResponse.success(analytics, "ChurnAnalytics"));
    }

    @PostMapping("/churnPrediction/{customerId}/action")
    public ResponseEntity<TmfResponse<ChurnPrediction>> triggerRetentionAction(
            @PathVariable String customerId,
            @RequestBody Map<String, String> request) {
        
        String actionType = request.get("actionType");
        ChurnPrediction prediction = churnPredictionService.triggerRetentionAction(customerId, actionType);
        return ResponseEntity.ok(TmfResponse.success(prediction, "ChurnPrediction"));
    }
}