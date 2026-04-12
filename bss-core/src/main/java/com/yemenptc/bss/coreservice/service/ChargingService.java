package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ChargingRule;
import com.yemenptc.bss.coreservice.repository.ChargingRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingService {

    private final ChargingRuleRepository chargingRuleRepository;

    @Transactional
    public ChargingRule createRule(ChargingRule request) {
        log.info("Creating charging rule: {}", request.getRuleName());
        
        ChargingRule rule = ChargingRule.builder()
            .ruleName(request.getRuleName())
            .ruleType(request.getRuleType())
            .chargingType(request.getChargingType())
            .rateAmount(request.getRateAmount())
            .rateUnit(request.getRateUnit())
            .currency(request.getCurrency() != null ? request.getCurrency() : "YER")
            .priority(request.getPriority())
            .status(ChargingRule.RuleStatus.ACTIVE)
            .serviceType(request.getServiceType())
            .timeFrom(request.getTimeFrom())
            .timeTo(request.getTimeTo())
            .dayOfWeek(request.getDayOfWeek())
            .build();
        
        return chargingRuleRepository.save(rule);
    }

    @Transactional(readOnly = true)
    public ChargingRule getRule(UUID id) {
        return chargingRuleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Charging rule not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ChargingRule> listRules(Pageable pageable) {
        return chargingRuleRepository.findAll(pageable);
    }

    @Transactional
    public ChargingRule updateRule(UUID id, ChargingRule request) {
        ChargingRule rule = getRule(id);
        
        if (request.getRuleName() != null) rule.setRuleName(request.getRuleName());
        if (request.getRuleType() != null) rule.setRuleType(request.getRuleType());
        if (request.getChargingType() != null) rule.setChargingType(request.getChargingType());
        if (request.getRateAmount() != null) rule.setRateAmount(request.getRateAmount());
        if (request.getRateUnit() != null) rule.setRateUnit(request.getRateUnit());
        if (request.getServiceType() != null) rule.setServiceType(request.getServiceType());
        
        return chargingRuleRepository.save(rule);
    }

    @Transactional
    public void deactivateRule(UUID id) {
        ChargingRule rule = getRule(id);
        rule.setStatus(ChargingRule.RuleStatus.INACTIVE);
        chargingRuleRepository.save(rule);
        log.info("Charging rule deactivated: {}", id);
    }
}