package com.yemenptc.bss.coreservice.workflow;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowOrchestrator {

    private final Map<String, WorkflowExecution> executions = new ConcurrentHashMap<>();

    public WorkflowExecution startOrderFulfillment(String orderId, Map<String, Object> variables) {
        String executionId = UUID.randomUUID().toString();
        
        WorkflowExecution execution = WorkflowExecution.builder()
            .executionId(executionId)
            .processDefinitionId("order-fulfillment-v1")
            .businessKey(orderId)
            .workflowType(WorkflowExecution.WorkflowType.ORDER_FULFILLMENT)
            .status(WorkflowExecution.WorkflowStatus.INITIATED)
            .startTime(Instant.now())
            .currentActivity("ORDER_RECEIVED")
            .retryCount(0)
            .build();
        
        executions.put(executionId, execution);
        
        executeWorkflow(execution, variables);
        
        return execution;
    }

    public WorkflowExecution startServiceActivation(String subscriptionId, Map<String, Object> variables) {
        String executionId = UUID.randomUUID().toString();
        
        WorkflowExecution execution = WorkflowExecution.builder()
            .executionId(executionId)
            .processDefinitionId("service-activation-v1")
            .businessKey(subscriptionId)
            .workflowType(WorkflowExecution.WorkflowType.SERVICE_ACTIVATION)
            .status(WorkflowExecution.WorkflowStatus.INITIATED)
            .startTime(Instant.now())
            .currentActivity("VALIDATION")
            .retryCount(0)
            .build();
        
        executions.put(executionId, execution);
        
        executeWorkflow(execution, variables);
        
        return execution;
    }

    private void executeWorkflow(WorkflowExecution execution, Map<String, Object> variables) {
        execution.setStatus(WorkflowExecution.WorkflowStatus.RUNNING);
        
        String activity = execution.getCurrentActivity();
        
        while (!isTerminalState(execution)) {
            try {
                String nextActivity = getNextActivity(execution);
                execution.setCurrentActivity(nextActivity);
                
                processActivity(execution, nextActivity, variables);
                
                if (execution.getStatus() == WorkflowExecution.WorkflowStatus.FAILED) {
                    handleFailure(execution);
                }
                
            } catch (Exception e) {
                log.error("Workflow {} failed at activity {}", 
                    execution.getExecutionId(), execution.getCurrentActivity(), e);
                execution.setStatus(WorkflowExecution.WorkflowStatus.FAILED);
                execution.setErrorMessage(e.getMessage());
                break;
            }
        }
        
        if (execution.getStatus() == WorkflowExecution.WorkflowStatus.RUNNING) {
            execution.setStatus(WorkflowExecution.WorkflowStatus.COMPLETED);
            execution.setEndTime(Instant.now());
        }
        
        log.info("Workflow {} completed with status: {}", 
            execution.getExecutionId(), execution.getStatus());
    }

    private String getNextActivity(WorkflowExecution execution) {
        return switch (execution.getCurrentActivity()) {
            case "ORDER_RECEIVED" -> "VALIDATE_ORDER";
            case "VALIDATE_ORDER" -> "CHECK_INVENTORY";
            case "CHECK_INVENTORY" -> "RESERVE_RESOURCE";
            case "RESERVE_RESOURCE" -> "CONFIGURE_NETWORK";
            case "CONFIGURE_NETWORK" -> "ACTIVATE_SERVICE";
            case "ACTIVATE_SERVICE" -> "NOTIFY_CUSTOMER";
            case "NOTIFY_CUSTOMER" -> "COMPLETED";
            default -> "COMPLETED";
        };
    }

    private void processActivity(WorkflowExecution execution, String activity, 
            Map<String, Object> variables) {
        
        switch (activity) {
            case "VALIDATE_ORDER" -> {
                log.info("Validating order: {}", execution.getBusinessKey());
            }
            case "CHECK_INVENTORY" -> {
                log.info("Checking inventory for order: {}", execution.getBusinessKey());
            }
            case "RESERVE_RESOURCE" -> {
                log.info("Reserving resources for order: {}", execution.getBusinessKey());
            }
            case "CONFIGURE_NETWORK" -> {
                log.info("Configuring network for order: {}", execution.getBusinessKey());
            }
            case "ACTIVATE_SERVICE" -> {
                log.info("Activating service for order: {}", execution.getBusinessKey());
            }
            case "NOTIFY_CUSTOMER" -> {
                log.info("Notifying customer for order: {}", execution.getBusinessKey());
            }
            case "COMPLETED" -> {
                execution.setStatus(WorkflowExecution.WorkflowStatus.COMPLETED);
                execution.setEndTime(Instant.now());
            }
        }
    }

    private boolean isTerminalState(WorkflowExecution execution) {
        return execution.getStatus() == WorkflowExecution.WorkflowStatus.COMPLETED ||
               execution.getStatus() == WorkflowExecution.WorkflowStatus.FAILED ||
               execution.getStatus() == WorkflowExecution.WorkflowStatus.CANCELLED;
    }

    private void handleFailure(WorkflowExecution execution) {
        if (execution.getRetryCount() < 3) {
            execution.setRetryCount(execution.getRetryCount() + 1);
            execution.setStatus(WorkflowExecution.WorkflowStatus.RUNNING);
            log.info("Retrying workflow {} (attempt {})", 
                execution.getExecutionId(), execution.getRetryCount());
        }
    }

    public Optional<WorkflowExecution> getExecution(String executionId) {
        return Optional.ofNullable(executions.get(executionId));
    }

    public List<WorkflowExecution> getExecutionsByStatus(WorkflowExecution.WorkflowStatus status) {
        return executions.values().stream()
            .filter(e -> e.getStatus() == status)
            .toList();
    }

    public List<WorkflowExecution> getExecutionsByOrder(String orderId) {
        return executions.values().stream()
            .filter(e -> orderId.equals(e.getBusinessKey()))
            .toList();
    }
}
