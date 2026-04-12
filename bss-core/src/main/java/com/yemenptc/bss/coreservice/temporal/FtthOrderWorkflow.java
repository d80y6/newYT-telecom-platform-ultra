package com.yemenptc.bss.coreservice.temporal;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Map;

/**
 * Temporal workflow for FTTH order saga orchestration.
 * Implements compensation for each step.
 */
@WorkflowInterface
public interface FtthOrderWorkflow {

    @WorkflowMethod
    String executeFtthOrder(String orderId, Map<String, Object> config);
}
