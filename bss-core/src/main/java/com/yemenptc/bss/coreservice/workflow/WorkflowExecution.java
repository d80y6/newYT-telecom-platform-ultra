package com.yemenptc.bss.coreservice.workflow;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowExecution {
    
    private String executionId;
    private String processDefinitionId;
    private String processInstanceId;
    private String businessKey;
    private WorkflowType workflowType;
    private WorkflowStatus status;
    private Instant startTime;
    private Instant endTime;
    private String currentActivity;
    private String assignedUser;
    private Integer retryCount;
    private String errorMessage;

    public enum WorkflowType {
        ORDER_FULFILLMENT, SERVICE_ACTIVATION, SERVICE_SUSPENSION, SERVICE_RESTORATION,
        NUMBER_PORTING, DEVICE_PROVISIONING, FAULT_ESCALATION, BILLING_CYCLE
    }

    public enum WorkflowStatus {
        INITIATED, RUNNING, WAITING, COMPLETED, FAILED, CANCELLED, SUSPENDED
    }
}
