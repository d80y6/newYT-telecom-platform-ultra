package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.PricePlan;
import com.yemenptc.bss.coreservice.repository.PricePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PricePlanService {

    private final PricePlanRepository pricePlanRepository;

    @Transactional
    public PricePlan createPricePlan(PricePlan request) {
        log.info("Creating price plan: {}", request.getName());

        PricePlan plan = PricePlan.builder()
            .planId(request.getPlanId() != null ? request.getPlanId() : "PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .name(request.getName())
            .description(request.getDescription())
            .billingPeriod(request.getBillingPeriod())
            .basePrice(request.getBasePrice())
            .currency(request.getCurrency() != null ? request.getCurrency() : "YER")
            .isActive(true)
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .build();

        PricePlan saved = pricePlanRepository.save(plan);
        log.info("Price plan created: {}", saved.getPlanId());
        return saved;
    }

    @Transactional(readOnly = true)
    public PricePlan getPricePlan(UUID id) {
        return pricePlanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Price plan not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PricePlan> listPricePlans(Pageable pageable) {
        return pricePlanRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<PricePlan> getActivePricePlans() {
        return pricePlanRepository.findByIsActive(true);
    }

    @Transactional
    public PricePlan updatePricePlan(UUID id, PricePlan request) {
        PricePlan plan = getPricePlan(id);
        if (request.getName() != null) plan.setName(request.getName());
        if (request.getDescription() != null) plan.setDescription(request.getDescription());
        if (request.getBasePrice() != null) plan.setBasePrice(request.getBasePrice());
        if (request.getBillingPeriod() != null) plan.setBillingPeriod(request.getBillingPeriod());
        return pricePlanRepository.save(plan);
    }

    @Transactional
    public PricePlan deactivatePricePlan(UUID id) {
        PricePlan plan = getPricePlan(id);
        plan.setIsActive(false);
        log.info("Price plan deactivated: {}", plan.getPlanId());
        return pricePlanRepository.save(plan);
    }

    @Transactional
    public void deletePricePlan(UUID id) {
        PricePlan plan = getPricePlan(id);
        pricePlanRepository.delete(plan);
        log.info("Price plan deleted: {}", plan.getPlanId());
    }
}
