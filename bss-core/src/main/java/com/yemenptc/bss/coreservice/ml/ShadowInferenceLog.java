package com.yemenptc.bss.coreservice.ml;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.math.BigDecimal;

@Data
@Builder
public class ShadowInferenceLog {
    private String modelId;
    private String entityType;
    private String entityId;
    private BigDecimal predictedScore;
    private String features;
    private Instant timestamp;
}
