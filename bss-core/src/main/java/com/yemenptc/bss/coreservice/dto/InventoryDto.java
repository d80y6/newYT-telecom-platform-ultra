package com.yemenptc.bss.coreservice.dto;

import lombok.*;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryDto {
    private String id;
    private String name;
    private String type;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class NetworkElementRequest {
    private String name;
    private String type;
    private String vendor;
    private String model;
    private String ipAddress;
    private String location;
    private String siteId;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class NetworkElementDto {
    private String id;
    private String name;
    private String type;
    private String vendor;
    private String model;
    private String ipAddress;
    private String status;
    private String location;
    private String siteId;
    private Instant createdAt;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class NetworkInventoryStats {
    private long totalElements;
    private long activeElements;
    private long inactiveElements;
    private long dlsamCount;
    private long oltCount;
    private long switchCount;
    private long routerCount;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class NumberPoolDto {
    private String id;
    private String number;
    private String numberType;
    private String status;
    private String exchange;
    private String assignedTo;
    private Instant reservationExpiry;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProvisioningTask {
    private String id;
    private String action;
    private String status;
    private String request;
    private String errorMessage;
    private int retryCount;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;
}
