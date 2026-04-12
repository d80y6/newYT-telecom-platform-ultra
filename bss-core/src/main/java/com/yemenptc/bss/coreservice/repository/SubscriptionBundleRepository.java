package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.SubscriptionBundle;
import com.yemenptc.bss.coreservice.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionBundleRepository extends JpaRepository<SubscriptionBundle, UUID> {
    
    Optional<SubscriptionBundle> findByBundleId(String bundleId);
    
    List<SubscriptionBundle> findBySubscriptionIdAndIsActiveTrue(String subscriptionId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM SubscriptionBundle b WHERE b.subscriptionId = :subscriptionId " +
           "AND b.serviceType = :serviceType AND b.isActive = true AND b.expiresAt > :now " +
           "AND b.remainingUnits > 0 ORDER BY b.expiresAt ASC")
    List<SubscriptionBundle> findActiveBundlesWithRemainingUnits(
            @Param("subscriptionId") String subscriptionId,
            @Param("serviceType") PricingRule.ServiceType serviceType,
            @Param("now") Instant now);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM SubscriptionBundle b WHERE b.bundleId = :bundleId")
    Optional<SubscriptionBundle> findByBundleIdWithLock(@Param("bundleId") String bundleId);
    
    @Query("SELECT b FROM SubscriptionBundle b WHERE b.subscriptionId = :subscriptionId " +
           "AND b.serviceType = :serviceType AND b.isActive = true")
    List<SubscriptionBundle> findBySubscriptionAndServiceType(
            @Param("subscriptionId") String subscriptionId,
            @Param("serviceType") PricingRule.ServiceType serviceType);
}
