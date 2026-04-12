package com.yemenptc.bss.coreservice.rating;

import com.yemenptc.bss.coreservice.entity.PricingRule;
import com.yemenptc.bss.coreservice.entity.SubscriptionBundle;
import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.repository.PricingRuleRepository;
import com.yemenptc.bss.coreservice.repository.SubscriptionBundleRepository;
import com.yemenptc.bss.coreservice.repository.SubscriptionRepository;
import com.yemenptc.bss.coreservice.repository.PricePlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingEngineTest {

    @Mock
    private PricingRuleRepository pricingRuleRepository;

    @Mock
    private SubscriptionBundleRepository subscriptionBundleRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PricePlanRepository pricePlanRepository;

    @InjectMocks
    private RatingEngine ratingEngine;

    private UsageEvent testUsageEvent;
    private PricingRule testPricingRule;

    @BeforeEach
    void setUp() {
        testUsageEvent = UsageEvent.builder()
                .eventId("TEST-001")
                .subscriptionId(UUID.randomUUID())
                .serviceType("DATA")
                .eventType("DATA_USAGE")
                .usageValue(new BigDecimal("100"))
                .usageUnit("MB")
                .eventTime(Instant.now())
                .sourceSystem("TEST")
                .build();

        testPricingRule = PricingRule.builder()
                .ruleId("RULE-001")
                .planId("PLAN-001")
                .serviceType(PricingRule.ServiceType.DATA)
                .ratingType(PricingRule.RatingType.VOLUME_BASED)
                .usageUnit("MB")
                .ratePerUnit(new BigDecimal("0.50"))
                .isActive(true)
                .effectiveFrom(Instant.now().minusSeconds(3600))
                .build();
    }

    @Test
    void testRateUsageEvent_WithValidRule_ReturnsNonZeroCharge() {
        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Collections.singletonList(testPricingRule));
        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        assertNotNull(result);
        assertEquals("TEST-001", result.getEventId());
        assertNotNull(result.getChargedAmount());
        assertTrue(result.getChargedAmount().compareTo(BigDecimal.ZERO) > 0,
                "Charged amount should be greater than zero");
        assertEquals(RatingRecord.RatingStatus.RATED, result.getRatingStatus());
        assertNotNull(result.getRatedAt());
    }

    @Test
    void testRateUsageEvent_VolumeBasedCalculation() {
        testUsageEvent.setUsageValue(new BigDecimal("100"));
        testPricingRule.setRatePerUnit(new BigDecimal("0.10"));

        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Collections.singletonList(testPricingRule));
        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        BigDecimal expectedCharge = new BigDecimal("10.00");
        assertEquals(0, expectedCharge.compareTo(result.getChargedAmount()),
                "Expected charge of 10.00 for 100 MB at 0.10/MB");
    }

    @Test
    void testRateUsageEvent_WithBundleDeduction() {
        testUsageEvent.setUsageValue(new BigDecimal("50"));

        SubscriptionBundle bundle = SubscriptionBundle.builder()
                .bundleId("BUNDLE-001")
                .subscriptionId(testUsageEvent.getSubscriptionId().toString())
                .serviceType(PricingRule.ServiceType.DATA)
                .totalUnits(new BigDecimal("100"))
                .remainingUnits(new BigDecimal("100"))
                .isActive(true)
                .build();

        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.singletonList(bundle));
        when(subscriptionBundleRepository.findByBundleId(any()))
                .thenReturn(Optional.of(bundle));
        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        assertNotNull(result.getBundleDeductions());
        assertEquals(1, result.getBundleDeductions().size());
        assertEquals("BUNDLE-001", result.getBundleDeductions().get(0).getBundleId());
    }

    @Test
    void testRateUsageEvent_NoRules_ReturnsDefaultCharge() {
        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        assertNotNull(result);
        assertNotNull(result.getChargedAmount());
        assertEquals(RatingRecord.RatingStatus.RATED, result.getRatingStatus());
    }

    @Test
    void testRateUsageEvent_WithPeakOffPeakRate() {
        testUsageEvent.setUsageValue(new BigDecimal("60"));
        testPricingRule.setPeakStartHour(9);
        testPricingRule.setPeakEndHour(18);
        testPricingRule.setPeakRate(new BigDecimal("0.20"));
        testPricingRule.setOffPeakRate(new BigDecimal("0.10"));

        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Collections.singletonList(testPricingRule));
        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        assertNotNull(result.getChargedAmount());
        assertTrue(result.getChargedAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testRateUsageEvent_WithDiscount() {
        testPricingRule.setDiscountPercentage(new BigDecimal("10"));

        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Collections.singletonList(testPricingRule));
        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        assertNotNull(result.getChargedAmount());
    }

    @Test
    void testRateUsageEvent_WithMinCharge() {
        testPricingRule.setRatePerUnit(new BigDecimal("0.001"));
        testPricingRule.setMinCharge(new BigDecimal("5.00"));

        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Collections.singletonList(testPricingRule));
        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        assertTrue(result.getChargedAmount().compareTo(new BigDecimal("5.00")) >= 0,
                "Charge should not be less than minimum charge");
    }

    @Test
    void testRateUsageEvent_TaxCalculation() {
        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Collections.singletonList(testPricingRule));
        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        assertNotNull(result.getTaxAmount());
        assertNotNull(result.getTotalAmount());
        assertEquals(0, result.getTotalAmount().compareTo(
                result.getChargedAmount().add(result.getTaxAmount())));
    }

    @Test
    void testRateUsageEvent_MultipleRulesApplied() {
        PricingRule rule1 = PricingRule.builder()
                .ruleId("RULE-001")
                .planId("PLAN-001")
                .serviceType(PricingRule.ServiceType.VOICE)
                .ratingType(PricingRule.RatingType.TIME_BASED)
                .usageUnit("SEC")
                .ratePerUnit(new BigDecimal("0.01"))
                .isActive(true)
                .build();

        PricingRule rule2 = PricingRule.builder()
                .ruleId("RULE-002")
                .planId("PLAN-001")
                .serviceType(PricingRule.ServiceType.VOICE)
                .ratingType(PricingRule.RatingType.FLAT_RATE)
                .flatFee(new BigDecimal("5.00"))
                .isActive(true)
                .build();

        testUsageEvent.setServiceType("VOICE");
        testUsageEvent.setUsageValue(new BigDecimal("60"));

        when(pricingRuleRepository.findEffectiveRules(any(), any(), any()))
                .thenReturn(Arrays.asList(rule1, rule2));
        when(subscriptionBundleRepository.findActiveBundlesWithRemainingUnits(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        RatingEngine.RatingResult result = ratingEngine.rateUsageEvent(testUsageEvent);

        assertNotNull(result.getAppliedRules());
        assertEquals(2, result.getAppliedRules().size());
        assertTrue(result.getAppliedRules().contains("RULE-001"));
        assertTrue(result.getAppliedRules().contains("RULE-002"));
    }
}
