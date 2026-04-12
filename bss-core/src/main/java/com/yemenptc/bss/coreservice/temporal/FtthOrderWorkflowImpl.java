package com.yemenptc.bss.coreservice.temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Temporal workflow implementation for FTTH order saga.
 * Steps: Party Validation → Credit Check → Resource Reservation → CPE Activation → Billing Enablement
 * Compensation: Reverse order of completed steps.
 */
@Slf4j
public class FtthOrderWorkflowImpl implements FtthOrderWorkflow {

    private final OrderActivities activities = Workflow.newActivityStub(
            OrderActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(
                            io.temporal.common.RetryOptions.newBuilder()
                                    .setMaximumAttempts(3)
                                    .setInitialInterval(Duration.ofSeconds(1))
                                    .build())
                    .build());

    @Override
    public String executeFtthOrder(String orderId, Map<String, Object> config) {
        List<String> completedSteps = new ArrayList<>();
        String partyId = (String) config.get("partyId");

        try {
            // Step 1: Validate Party
            if (!activities.validateParty(partyId)) {
                return "FAILED: Party validation failed";
            }
            completedSteps.add("PARTY_VALIDATED");

            // Step 2: Credit Check
            if (!activities.performCreditCheck(partyId)) {
                return "FAILED: Credit check failed";
            }
            completedSteps.add("CREDIT_CHECKED");

            // Step 3: Reserve Resources
            if (!activities.reserveResources(orderId, config)) {
                compensate(completedSteps, orderId);
                return "FAILED: Resource reservation failed";
            }
            completedSteps.add("RESOURCES_RESERVED");

            // Step 4: Activate CPE
            if (!activities.activateCpe(orderId, config)) {
                compensate(completedSteps, orderId);
                return "FAILED: CPE activation failed";
            }
            completedSteps.add("CPE_ACTIVATED");

            // Step 5: Enable Billing
            activities.enableBilling(orderId);
            completedSteps.add("BILLING_ENABLED");

            return "COMPLETED";

        } catch (Exception e) {
            log.error("FTTH order saga failed for order: {}", orderId, e);
            compensate(completedSteps, orderId);
            return "FAILED: " + e.getMessage();
        }
    }

    private void compensate(List<String> completedSteps, String orderId) {
        log.warn("Compensating saga for order: {}", orderId);

        for (int i = completedSteps.size() - 1; i >= 0; i--) {
            switch (completedSteps.get(i)) {
                case "BILLING_ENABLED" -> activities.disableBilling(orderId);
                case "CPE_ACTIVATED" -> activities.deactivateCpe(orderId);
                case "RESOURCES_RESERVED" -> activities.releaseResources(orderId);
            }
        }
    }
}
