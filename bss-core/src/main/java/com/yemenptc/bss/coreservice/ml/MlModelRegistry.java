package com.yemenptc.bss.coreservice.ml;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class MlModelRegistry {
    private String modelId;
    private String version;
    private String algorithmType;
    private Instant createdAt;
    private Map<String, Double> performanceMetrics; // e.g., accuracy, precision, recall
    private boolean isActive;
}
