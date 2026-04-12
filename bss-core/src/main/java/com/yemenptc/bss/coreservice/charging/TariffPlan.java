package com.yemenptc.bss.coreservice.charging;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffPlan {
    
    private String planId;
    private String planName;
    private PricingModel model;
    private BigDecimal baseRate;
    private BigDecimal peakRate;
    private BigDecimal offPeakRate;
    private BigDecimal perSecondCharge;
    private BigDecimal perMbCharge;
    private BigDecimal monthlyFee;
    private Integer freeVoiceMinutes;
    private Integer freeSms;
    private Long freeDataMb;
    private BigDecimal roamingMarkup;
    private String currency;

    public enum PricingModel {
        FLAT_RATE, TIERED, VOLUME_BASED, TIME_BASED, HYBRID
    }
}
