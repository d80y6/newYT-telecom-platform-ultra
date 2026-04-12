package com.yemenptc.bss.coreservice.dto;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderDto {
    private String id;
    private String orderNumber;
    private String customerId;
    private String orderType;
    private String status;
    private String priority;
    private String channel;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class OrderCreateRequest {
    private String customerId;
    private String orderType;
    private String priority;
    private String channel;
    private String notes;
    private List<OrderItemRequest> items;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class OrderItemRequest {
    private String itemType;
    private String productOfferingId;
    private String action;
    private Map<String, Object> parameters;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class OrderItemDto {
    private String id;
    private String orderId;
    private String itemType;
    private String productOfferingId;
    private String subscriptionId;
    private String status;
    private String action;
    private String errorMessage;
    private Instant createdAt;
    private Instant completedAt;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class OrderResult {
    private boolean success;
    private String orderId;
    private String orderNumber;
    private String message;
}
