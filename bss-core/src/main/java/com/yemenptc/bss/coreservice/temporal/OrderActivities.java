package com.yemenptc.bss.coreservice.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.Map;

/**
 * Temporal activities for order saga orchestration.
 */
@ActivityInterface
public interface OrderActivities {

    @ActivityMethod
    boolean validateParty(String partyId);

    @ActivityMethod
    boolean performCreditCheck(String partyId);

    @ActivityMethod
    boolean reserveResources(String orderId, Map<String, Object> config);

    @ActivityMethod
    boolean activateCpe(String orderId, Map<String, Object> config);

    @ActivityMethod
    void enableBilling(String orderId);

    @ActivityMethod
    void releaseResources(String orderId);

    @ActivityMethod
    void deactivateCpe(String orderId);

    @ActivityMethod
    void disableBilling(String orderId);
}
