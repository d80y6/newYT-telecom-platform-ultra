package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ChargingRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChargingRuleRepository extends JpaRepository<ChargingRule, UUID> {
    Page<ChargingRule> findByStatus(ChargingRule.RuleStatus status, Pageable pageable);
    Page<ChargingRule> findByChargingType(ChargingRule.ChargingType type, Pageable pageable);
}