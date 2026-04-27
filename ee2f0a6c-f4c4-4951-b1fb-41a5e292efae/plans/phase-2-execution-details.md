# Phase 2: ML Model Tuning and Validation Execution Plan

## Objective
Transition from heuristic-based churn prediction and fraud detection to production-grade ML-driven predictive modeling.

## Execution Steps

### 1. Advanced Feature Engineering (Churn)
- **Target:** `ChurnPredictionService.java`
- **Action:**
    - Refactor `calculateChurnRisk` to pull real-time data from `TroubleTicketRepository`.
    - Implement a rolling-window analysis (30/60/90 days) for usage and payment behavior.
    - Introduce a `ChurnFeatureExtractor` utility to standardize inputs for both rule-based heuristics and future model inference.

### 2. ML-Driven Fraud Detection (Shadow Mode)
- **Target:** `FraudDetectionService.java`
- **Action:**
    - Enhance `runShadowModelCheck` to log real-time features (`transactionValue`, `velocity`, `geoVariance`) to a `ShadowInferenceLog`.
    - Implement an `EvaluationService` to compare shadow model decisions against business rule outputs (e.g., if shadow model predicts "Fraud" but rules "Allow", flag for manual audit).

### 3. Model Registry & Retraining Workflow
- **Target:** `MlModelRegistry.java` (New)
- **Action:**
    - Create a registry that tracks current model metadata, version, and performance metrics.
    - Implement a scheduled task in `ModelRetrainingService` that exports anonymized training sets (Usage/Churn history) for offline training, keeping the hot path performant.

## Verification
- **Backtesting:** Run the improved churn features against the last 90 days of historic data.
- **Latency Check:** Measure time overhead of fetching trouble tickets in the churn scoring path (Target: <50ms addition).

---
*Implementation beginning immediately.*
