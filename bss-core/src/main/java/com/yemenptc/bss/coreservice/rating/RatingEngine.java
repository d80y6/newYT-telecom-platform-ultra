package com.yemenptc.bss.coreservice.rating;

import com.yemenptc.bss.coreservice.entity.*;
import com.yemenptc.bss.coreservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingEngine {

    private final PricingRuleRepository pricingRuleRepository;
    private final SubscriptionBundleRepository subscriptionBundleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PricePlanRepository pricePlanRepository;

    @Value("${billing.tax-rate:0.05}")
    private BigDecimal taxRate;

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Transactional
    public RatingResult rateUsageEvent(UsageEvent event) {
        String planId = resolvePlanId(event.getSubscriptionId());
        PricingRule.ServiceType serviceType = resolveServiceType(event.getServiceType());
        
        List<PricingRule> rules = pricingRuleRepository.findEffectiveRules(
                planId, serviceType, Instant.now());
        
        if (rules.isEmpty()) {
            log.warn("No pricing rules found for plan {} service {}", planId, serviceType);
            rules = getDefaultRules(serviceType);
        }
        
        BigDecimal totalCharge = BigDecimal.ZERO;
        BigDecimal totalUnitsUsed = BigDecimal.ZERO;
        List<BundleDeduction> bundleDeductions = new ArrayList<>();
        List<String> appliedRules = new ArrayList<>();
        
        List<SubscriptionBundle> activeBundles = subscriptionBundleRepository
                .findActiveBundlesWithRemainingUnits(
                        event.getSubscriptionId().toString(), 
                        serviceType, 
                        Instant.now());
        
        BigDecimal remainingUsage = event.getUsageValue();
        
        for (SubscriptionBundle bundle : activeBundles) {
            if (remainingUsage.compareTo(BigDecimal.ZERO) <= 0) break;
            
            if (bundle.getRemainingUnits().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal deducted = bundle.deductUnits(remainingUsage);
                remainingUsage = remainingUsage.subtract(deducted);
                totalUnitsUsed = totalUnitsUsed.add(deducted);
                
                bundleDeductions.add(BundleDeduction.builder()
                        .bundleId(bundle.getBundleId())
                        .deductedUnits(deducted)
                        .remainingInBundle(bundle.getRemainingUnits())
                        .build());
            }
        }
        
        if (remainingUsage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal usageToCharge = remainingUsage;
            
            for (PricingRule rule : rules) {
                BigDecimal charge = calculateChargeForRule(rule, usageToCharge, event.getEventTime());
                totalCharge = totalCharge.add(charge);
                appliedRules.add(rule.getRuleId());
            }
        }
        
        BigDecimal discountAmount = applyDiscounts(totalCharge, rules);
        totalCharge = totalCharge.subtract(discountAmount);
        
        BigDecimal taxAmount = totalCharge.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWithTax = totalCharge.add(taxAmount);
        
        totalCharge = enforceMinMaxCharge(totalCharge, rules);
        
        return RatingResult.builder()
                .eventId(event.getEventId())
                .subscriptionId(event.getSubscriptionId())
                .serviceType(event.getServiceType())
                .eventType(event.getEventType())
                .usageValue(event.getUsageValue())
                .chargedUnits(totalUnitsUsed)
                .chargedAmount(totalCharge)
                .taxAmount(taxAmount)
                .totalAmount(totalWithTax)
                .currency("YER")
                .appliedRules(appliedRules)
                .bundleDeductions(bundleDeductions)
                .ratingStatus(RatingRecord.RatingStatus.RATED)
                .ratedAt(Instant.now())
                .build();
    }

    private BigDecimal calculateChargeForRule(PricingRule rule, BigDecimal usage, Instant eventTime) {
        switch (rule.getRatingType()) {
            case TIME_BASED:
                return calculateTimeBasedCharge(rule, usage);
            case VOLUME_BASED:
                return calculateVolumeBasedCharge(rule, usage);
            case FLAT_RATE:
                return rule.getFlatFee() != null ? rule.getFlatFee() : BigDecimal.ZERO;
            case DESTINATION:
                return calculateDestinationCharge(rule, usage, eventTime);
            case TIERED:
                return calculateTieredCharge(rule, usage);
            default:
                return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateTimeBasedCharge(PricingRule rule, BigDecimal usageInSeconds) {
        BigDecimal rate = getEffectiveRate(rule, LocalTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        return usageInSeconds.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateVolumeBasedCharge(PricingRule rule, BigDecimal volume) {
        BigDecimal ratePerMb = rule.getRatePerUnit();
        
        if ("GB".equalsIgnoreCase(rule.getBillingUnit())) {
            BigDecimal mbVolume = volume.multiply(new BigDecimal("1024"));
            return mbVolume.multiply(ratePerMb).setScale(2, RoundingMode.HALF_UP);
        }
        
        return volume.multiply(ratePerMb).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDestinationCharge(PricingRule rule, BigDecimal usage, Instant eventTime) {
        BigDecimal baseCharge = calculateTimeBasedCharge(rule, usage);
        
        if (rule.getDestinationPrefix() != null && 
            rule.getDestinationPrefix().startsWith("+")) {
            BigDecimal surcharge = rule.getFlatFee() != null ? rule.getFlatFee() : BigDecimal.ZERO;
            return baseCharge.add(surcharge);
        }
        
        return baseCharge;
    }

    private BigDecimal calculateTieredCharge(PricingRule rule, BigDecimal volume) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal remaining = volume;
        
        BigDecimal[] tierLimits = {
                new BigDecimal("100"),   
                new BigDecimal("500"),   
                new BigDecimal("1000")   
        };
        
        BigDecimal[] tierRates = {
                rule.getRatePerUnit().multiply(new BigDecimal("0.5")),
                rule.getRatePerUnit().multiply(new BigDecimal("0.75")),
                rule.getRatePerUnit()
        };
        
        for (int i = 0; i < tierLimits.length; i++) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            
            BigDecimal tierVolume = remaining.min(tierLimits[i]);
            total = total.add(tierVolume.multiply(tierRates[i]));
            remaining = remaining.subtract(tierVolume);
        }
        
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal maxRate = rule.getMaxCharge() != null ? 
                    rule.getMaxCharge().divide(volume, 4, RoundingMode.HALF_UP) :
                    rule.getRatePerUnit().multiply(new BigDecimal("1.5"));
            total = total.add(remaining.multiply(maxRate));
        }
        
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getEffectiveRate(PricingRule rule, LocalTime time) {
        if (rule.getPeakStartHour() == null || rule.getPeakEndHour() == null) {
            return rule.getRatePerUnit();
        }
        
        LocalTime peakStart = LocalTime.of(rule.getPeakStartHour(), 0);
        LocalTime peakEnd = LocalTime.of(rule.getPeakEndHour(), 0);
        
        boolean isPeakHour;
        if (peakStart.isBefore(peakEnd)) {
            isPeakHour = !time.isBefore(peakStart) && time.isBefore(peakEnd);
        } else {
            isPeakHour = !time.isBefore(peakStart) || time.isBefore(peakEnd);
        }
        
        if (isPeakHour && rule.getPeakRate() != null) {
            return rule.getPeakRate();
        } else if (!isPeakHour && rule.getOffPeakRate() != null) {
            return rule.getOffPeakRate();
        }
        
        return rule.getRatePerUnit();
    }

    private BigDecimal applyDiscounts(BigDecimal amount, List<PricingRule> rules) {
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (PricingRule rule : rules) {
            if (rule.getDiscountPercentage() != null && 
                rule.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = amount.multiply(rule.getDiscountPercentage())
                        .divide(HUNDRED, 2, RoundingMode.HALF_UP);
                totalDiscount = totalDiscount.add(discount);
            }
        }
        
        return totalDiscount.min(amount);
    }

    private BigDecimal enforceMinMaxCharge(BigDecimal charge, List<PricingRule> rules) {
        BigDecimal minCharge = BigDecimal.ZERO;
        BigDecimal maxCharge = null;
        
        for (PricingRule rule : rules) {
            if (rule.getMinCharge() != null && rule.getMinCharge().compareTo(minCharge) > 0) {
                minCharge = rule.getMinCharge();
            }
            if (rule.getMaxCharge() != null) {
                if (maxCharge == null || rule.getMaxCharge().compareTo(maxCharge) < 0) {
                    maxCharge = rule.getMaxCharge();
                }
            }
        }
        
        if (charge.compareTo(minCharge) < 0) {
            return minCharge;
        }
        
        if (maxCharge != null && charge.compareTo(maxCharge) > 0) {
            return maxCharge;
        }
        
        return charge;
    }

    private String resolvePlanId(UUID subscriptionId) {
        if (subscriptionId == null) {
            return "DEFAULT";
        }
        
        return subscriptionRepository.findById(subscriptionId.toString())
                .map(sub -> {
                    if (sub.getPlanName() != null) {
                        return pricePlanRepository.findByPlanId(sub.getPlanName())
                                .map(PricePlan::getPlanId)
                                .orElse("DEFAULT");
                    }
                    return "DEFAULT";
                })
                .orElse("DEFAULT");
    }

    private PricingRule.ServiceType resolveServiceType(String serviceType) {
        if (serviceType == null) {
            return PricingRule.ServiceType.USAGE;
        }
        
        try {
            return PricingRule.ServiceType.valueOf(serviceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            switch (serviceType.toLowerCase()) {
                case "voice": case "call": case "telephony":
                    return PricingRule.ServiceType.VOICE;
                case "sms": case "mms": case "message":
                    return PricingRule.ServiceType.SMS;
                case "data": case "internet": case "broadband":
                    return PricingRule.ServiceType.DATA;
                case "roaming":
                    return PricingRule.ServiceType.ROAMING;
                case "international":
                    return PricingRule.ServiceType.INTERNATIONAL;
                case "subscription": case "recurring":
                    return PricingRule.ServiceType.SUBSCRIPTION;
                default:
                    return PricingRule.ServiceType.USAGE;
            }
        }
    }

    private List<PricingRule> getDefaultRules(PricingRule.ServiceType serviceType) {
        List<PricingRule> defaultRules = new ArrayList<>();
        
        PricingRule rule = PricingRule.builder()
                .ruleId("DEFAULT-" + serviceType.name())
                .planId("DEFAULT")
                .serviceType(serviceType)
                .ratingType(PricingRule.RatingType.FLAT_RATE)
                .usageUnit(getDefaultUnit(serviceType))
                .ratePerUnit(BigDecimal.ONE)
                .flatFee(new BigDecimal("10.00"))
                .isActive(true)
                .build();
        
        defaultRules.add(rule);
        return defaultRules;
    }

    private String getDefaultUnit(PricingRule.ServiceType serviceType) {
        switch (serviceType) {
            case VOICE: return "SEC";
            case SMS: return "SMS";
            case DATA: return "MB";
            case ROAMING: return "MB";
            case INTERNATIONAL: return "MIN";
            default: return "UNIT";
        }
    }

    @lombok.Builder
    @lombok.Data
    public static class RatingResult {
        private String eventId;
        private UUID subscriptionId;
        private String serviceType;
        private String eventType;
        private BigDecimal usageValue;
        private BigDecimal chargedUnits;
        private BigDecimal chargedAmount;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private String currency;
        private List<String> appliedRules;
        private List<BundleDeduction> bundleDeductions;
        private RatingRecord.RatingStatus ratingStatus;
        private Instant ratedAt;
    }

    @lombok.Builder
    @lombok.Data
    public static class BundleDeduction {
        private String bundleId;
        private BigDecimal deductedUnits;
        private BigDecimal remainingInBundle;
    }
}
