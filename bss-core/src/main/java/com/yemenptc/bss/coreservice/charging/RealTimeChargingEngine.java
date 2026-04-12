package com.yemenptc.bss.coreservice.charging;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealTimeChargingEngine {

    public ChargingEvent rateEvent(ChargingEvent event, TariffPlan tariff) {
        long startMs = System.currentTimeMillis();
        
        BigDecimal charge = calculateCharge(event, tariff);
        
        event.setRatedAmount(charge);
        event.setStatus(ChargingEvent.ChargingStatus.RATED);
        
        long latencyMs = System.currentTimeMillis() - startMs;
        log.info("Rated event {} in {}ms - amount: {}", 
            event.getEventId(), latencyMs, charge);
        
        if (latencyMs > 100) {
            log.warn("Rating latency exceeded 100ms: {}ms", latencyMs);
        }
        
        return event;
    }

    public BigDecimal calculateCharge(ChargingEvent event, TariffPlan tariff) {
        return switch (event.getUsageType()) {
            case VOICE -> rateVoice(event, tariff);
            case SMS -> rateSms(event, tariff);
            case DATA -> rateData(event, tariff);
            case CONTENT -> rateContent(event, tariff);
            case EVENT -> calculateEventCharge(event, tariff);
        };
    }

    private BigDecimal rateVoice(ChargingEvent event, TariffPlan tariff) {
        BigDecimal baseRate = getTimeBasedRate(tariff);
        long seconds = event.getDurationSeconds() != null ? event.getDurationSeconds() : 0;
        
        BigDecimal charge = baseRate.multiply(BigDecimal.valueOf(seconds))
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        
        return applyFreeAllowance(charge, tariff.getFreeVoiceMinutes() != null ? tariff.getFreeVoiceMinutes() : 0, seconds);
    }

    private BigDecimal rateSms(ChargingEvent event, TariffPlan tariff) {
        // SMS is typically charged per message, default 1 message
        BigDecimal smsCount = BigDecimal.ONE;
        BigDecimal rate = tariff.getBaseRate() != null ? tariff.getBaseRate() : new BigDecimal("1.00");
        return rate.multiply(smsCount);
    }

    private BigDecimal rateData(ChargingEvent event, TariffPlan tariff) {
        long mbUsed = event.getVolumeBytes() != null ? event.getVolumeBytes() / (1024 * 1024) : 0;
        BigDecimal rate = tariff.getPerMbCharge() != null ? tariff.getPerMbCharge() : BigDecimal.ZERO;
        
        BigDecimal charge = rate.multiply(BigDecimal.valueOf(mbUsed));
        
        long freeMb = tariff.getFreeDataMb() != null ? tariff.getFreeDataMb() : 0;
        if (mbUsed > freeMb) {
            charge = rate.multiply(BigDecimal.valueOf(mbUsed - freeMb));
        }
        
        return charge;
    }

    private BigDecimal rateContent(ChargingEvent event, TariffPlan tariff) {
        // Content services typically use base rate, could be extended for content tiers
        BigDecimal contentTierMultiplier = BigDecimal.ONE; // Default multiplier
        BigDecimal rate = tariff.getBaseRate() != null ? tariff.getBaseRate() : new BigDecimal("5.00");
        return rate.multiply(contentTierMultiplier);
    }

    private BigDecimal calculateEventCharge(ChargingEvent event, TariffPlan tariff) {
        // Event-based charging (e.g., service activation, feature enablement)
        BigDecimal rate = tariff.getBaseRate() != null ? tariff.getBaseRate() : new BigDecimal("10.00");
        return rate;
    }

    private BigDecimal getTimeBasedRate(TariffPlan tariff) {
        LocalTime now = LocalTime.now();
        LocalTime peakStart = LocalTime.of(8, 0);
        LocalTime peakEnd = LocalTime.of(22, 0);
        
        if (now.isAfter(peakStart) && now.isBefore(peakEnd)) {
            return tariff.getPeakRate() != null ? tariff.getPeakRate() : tariff.getBaseRate();
        }
        return tariff.getOffPeakRate() != null ? tariff.getOffPeakRate() : tariff.getBaseRate();
    }

    private BigDecimal applyFreeAllowance(BigDecimal charge, long freeMinutes, long usedSeconds) {
        long freeSeconds = freeMinutes * 60;
        if (usedSeconds <= freeSeconds) {
            return BigDecimal.ZERO;
        }
        return charge;
    }

    public ChargingEvent createChargingEvent(String subscriberId, String accountId, 
            ChargingEvent.UsageType usageType) {
        return ChargingEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .subscriberId(subscriberId)
            .accountId(accountId)
            .usageType(usageType)
            .startTime(Instant.now())
            .status(ChargingEvent.ChargingStatus.STARTED)
            .build();
    }
}
