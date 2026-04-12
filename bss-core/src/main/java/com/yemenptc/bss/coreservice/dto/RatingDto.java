package com.yemenptc.bss.coreservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RatingDto {
    private String eventId;
    private String subscriptionId;
    private String serviceType;
    private BigDecimal chargeAmount;
    private String currency;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class UsageEvent {
    private String eventId;
    private String subscriptionId;
    private String serviceType;
    private String eventType;
    private String callingNumber;
    private String calledNumber;
    private Instant startTime;
    private Instant endTime;
    private Long durationSeconds;
    private Long dataVolumeBytes;
    private String sourceSystem;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class RatedEvent {
    private String id;
    private String eventId;
    private String subscriptionId;
    private String serviceType;
    private String eventType;
    private BigDecimal chargeAmount;
    private String currency;
    private BigDecimal ratedUnits;
    private String ratingResult;
    private String errorMessage;
    private Instant ratedAt;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class PrepaidRatingRequest {
    private String subscriptionId;
    private String serviceType;
    private Integer ratingGroup;
    private Long requestedUnits;
    private BigDecimal availableBalance;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class PrepaidRatingResult {
    private boolean success;
    private Long grantedUnits;
    private BigDecimal chargedAmount;
    private BigDecimal remainingBalance;
    private String ratingResult;
    private String message;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class RatePlanDto {
    private String id;
    private String name;
    private String description;
    private String serviceType;
    private String ratingType;
    private String status;
    private Instant validFrom;
    private Instant validTo;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class RatePlanCreateRequest {
    private String name;
    private String description;
    private String serviceType;
    private String ratingType;
    private Instant validFrom;
    private Instant validTo;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class TariffDto {
    private String id;
    private String ratePlanId;
    private String name;
    private Integer ratingGroup;
    private String unitOfMeasure;
    private BigDecimal ratePerUnit;
    private String currency;
    private BigDecimal peakRate;
    private BigDecimal offPeakRate;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class TariffCreateRequest {
    private String name;
    private Integer ratingGroup;
    private String unitOfMeasure;
    private BigDecimal ratePerUnit;
    private BigDecimal peakRate;
    private BigDecimal offPeakRate;
    private Integer peakStartHour;
    private Integer peakEndHour;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class BatchRatingRequest {
    private java.util.List<UsageEvent> events;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class BatchRatingResult {
    private int totalEvents;
    private int successfulEvents;
    private int failedEvents;
    private BigDecimal totalChargeAmount;
}
