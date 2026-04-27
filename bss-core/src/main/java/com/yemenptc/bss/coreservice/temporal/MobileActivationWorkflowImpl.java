package com.yemenptc.bss.coreservice.temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class MobileActivationWorkflowImpl implements MobileActivationWorkflow {

    private final ProvisioningActivities activities = Workflow.newActivityStub(
            ProvisioningActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(1))
                            .setMaximumAttempts(3)
                            .build())
                    .build());

    @Override
    public void activateMobile(String orderId, String msisdn, String planId) {
        activities.reserveMsisdn(msisdn);
        activities.provisionPlan(msisdn, planId);
        activities.updateInventory(msisdn, "ACTIVE");
    }
}
