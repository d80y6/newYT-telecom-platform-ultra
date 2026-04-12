package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.PricePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricePlanRepository extends JpaRepository<PricePlan, UUID> {
    
    Optional<PricePlan> findByPlanId(String planId);
    
    List<PricePlan> findByIsActive(Boolean isActive);
    
    List<PricePlan> findByBillingPeriod(String billingPeriod);
}
