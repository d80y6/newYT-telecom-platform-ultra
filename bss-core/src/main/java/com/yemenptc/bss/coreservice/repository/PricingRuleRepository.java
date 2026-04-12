package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, UUID> {
    
    Optional<PricingRule> findByRuleId(String ruleId);
    
    List<PricingRule> findByPlanIdAndIsActiveTrue(String planId);
    
    List<PricingRule> findByServiceTypeAndIsActiveTrue(PricingRule.ServiceType serviceType);
    
    @Query("SELECT p FROM PricingRule p WHERE p.planId = :planId AND p.serviceType = :serviceType " +
           "AND p.isActive = true AND (p.effectiveFrom IS NULL OR p.effectiveFrom <= :now) " +
           "AND (p.effectiveTo IS NULL OR p.effectiveTo > :now) ORDER BY p.priority ASC")
    List<PricingRule> findEffectiveRules(
            @Param("planId") String planId, 
            @Param("serviceType") PricingRule.ServiceType serviceType,
            @Param("now") Instant now);
    
    @Query("SELECT p FROM PricingRule p WHERE p.planId = :planId AND p.serviceType = :serviceType " +
           "AND p.ratingType = :ratingType AND p.isActive = true")
    Optional<PricingRule> findByPlanAndServiceAndRatingType(
            @Param("planId") String planId,
            @Param("serviceType") PricingRule.ServiceType serviceType,
            @Param("ratingType") PricingRule.RatingType ratingType);
}
