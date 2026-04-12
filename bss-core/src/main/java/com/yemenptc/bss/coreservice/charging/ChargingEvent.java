package com.yemenptc.bss.coreservice.charging;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingEvent {
    
    private String eventId;
    private String sessionId;
    private String subscriberId;
    private String accountId;
    private UsageType usageType;
    private Instant startTime;
    private Instant endTime;
    private Long durationSeconds;
    private Long volumeBytes;
    private BigDecimal ratedAmount;
    private ChargingStatus status;
    private String destination;
    private String origin;
    private String networkElement;
    private String serviceId;
    private String offerId;

    public enum UsageType {
        VOICE, SMS, DATA, CONTENT, EVENT
    }

    public enum ChargingStatus {
        STARTED, CONTINUING, ENDED, RATED, FAILED
    }
}
