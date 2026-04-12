package com.yemenptc.bss.coreservice.temporal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of Temporal order activities.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderActivitiesImpl implements OrderActivities {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean validateParty(String partyId) {
        log.info("Validating party: {}", partyId);
        return true;
    }

    @Override
    public boolean performCreditCheck(String partyId) {
        log.info("Credit check for party: {}", partyId);
        return true;
    }

    @Override
    public boolean reserveResources(String orderId, Map<String, Object> config) {
        log.info("Reserving resources for order: {}", orderId);
        String lockKey = "order:lock:" + orderId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "reserved");
        return Boolean.TRUE.equals(locked);
    }

    @Override
    public boolean activateCpe(String orderId, Map<String, Object> config) {
        log.info("Activating CPE for order: {}", orderId);
        return true;
    }

    @Override
    public void enableBilling(String orderId) {
        log.info("Enabling billing for order: {}", orderId);
    }

    @Override
    public void releaseResources(String orderId) {
        log.info("Releasing resources for order: {}", orderId);
        redisTemplate.delete("order:lock:" + orderId);
    }

    @Override
    public void deactivateCpe(String orderId) {
        log.info("Deactivating CPE for order: {}", orderId);
    }

    @Override
    public void disableBilling(String orderId) {
        log.info("Disabling billing for order: {}", orderId);
    }
}
