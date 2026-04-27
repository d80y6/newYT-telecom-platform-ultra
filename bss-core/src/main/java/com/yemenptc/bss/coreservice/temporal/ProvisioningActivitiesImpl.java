package com.yemenptc.bss.coreservice.temporal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProvisioningActivitiesImpl implements ProvisioningActivities {

    @Override
    public void reserveMsisdn(String msisdn) {
        log.info("Reserving MSISDN: {}", msisdn);
    }

    @Override
    public void activateSim(String imsi) {
        log.info("Activating SIM: {}", imsi);
    }

    @Override
    public void provisionPlan(String msisdn, String planId) {
        log.info("Provisioning plan {} for MSISDN: {}", planId, msisdn);
    }

    @Override
    public void updateInventory(String resourceId, String status) {
        log.info("Updating inventory for resource {}: status={}", resourceId, status);
    }
}
