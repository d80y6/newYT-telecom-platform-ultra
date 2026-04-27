package com.yemenptc.bss.coreservice.temporal;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MobileActivationWorkflow {
    @WorkflowMethod
    void activateMobile(String orderId, String msisdn, String planId);
}
