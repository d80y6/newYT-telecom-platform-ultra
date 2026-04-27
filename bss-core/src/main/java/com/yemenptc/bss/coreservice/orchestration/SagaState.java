package com.yemenptc.bss.coreservice.orchestration;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaState {
    public String workflowId;
    private String status;
    private String currentStep;
    private Map<String, Object> context = new HashMap<>();
}
