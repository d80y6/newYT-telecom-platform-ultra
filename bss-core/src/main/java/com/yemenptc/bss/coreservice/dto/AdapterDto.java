package com.yemenptc.bss.coreservice.dto;

import lombok.*;
import java.time.Instant;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AdapterDto {
    private String adapterId;
    private String name;
    private String serviceType;
    private String status;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AdapterMetadata {
    private String adapterId;
    private String name;
    private String description;
    private String serviceType;
    private String protocolType;
    private int priority;
    private boolean enabled;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class HealthStatus {
    private String adapterId;
    private String status;
    private String message;
    private long latencyMs;
    private Instant lastChecked;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AdapterMetrics {
    private String adapterId;
    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private double successRate;
    private long avgLatencyMs;
    private long p95LatencyMs;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ErrorResponse {
    private String code;
    private String message;
    private String details;
    private int status;
    private Instant timestamp;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class PageResponse<T> {
    private java.util.List<T> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class SubscriptionDto {
    private String id;
    private String subscriptionNumber;
    private String customerId;
    private String accountId;
    private String serviceType;
    private String status;
    private String productOfferingId;
    private String serviceIdentifier;
    private String planName;
    private BigDecimal monthlyFee;
    private Instant startDate;
    private Instant endDate;
    private Instant createdAt;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class SubscriptionCreateRequest {
    private String customerId;
    private String accountId;
    private String serviceType;
    private String productOfferingId;
    private String serviceIdentifier;
}
