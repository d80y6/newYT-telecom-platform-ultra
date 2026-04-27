package com.yemenptc.bss.coreservice.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ProvisioningActivities {
    @ActivityMethod
    void reserveMsisdn(String msisdn);
    
    @ActivityMethod
    void activateSim(String imsi);
    
    @ActivityMethod
    void provisionPlan(String msisdn, String planId);
    
    @ActivityMethod
    void updateInventory(String resourceId, String status);
}
