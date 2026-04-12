package com.yemenptc.bss.coreservice.orchestration;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StepResult {
    private String stepId;
    private String stepName;
    private StepStatus status;
    private String errorCode;
    private String errorMessage;
    private Instant startedAt;
    private Instant completedAt;
    private Map<String, Object> outputData;
    
    public enum StepStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, COMPENSATED
    }
}