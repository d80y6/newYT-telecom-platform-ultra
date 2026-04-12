package com.yemenptc.bss.coreservice.dto;

import lombok.*;
import java.time.Instant;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProvisioningDto {
    private String serviceId;
    private String serviceType;
    private String status;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ActivationRequest {
    private String serviceId;
    private String serviceType;
    private String customerId;
    private String accountId;
    private String productOfferingId;
    private String serviceIdentifier;
    private Map<String, Object> configuration;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class DeactivationRequest {
    private String serviceId;
    private String serviceType;
    private String serviceIdentifier;
    private String reason;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class SuspensionRequest {
    private String serviceId;
    private String serviceType;
    private String serviceIdentifier;
    private String suspensionType;
    private String reason;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ResumeRequest {
    private String serviceId;
    private String serviceType;
    private String serviceIdentifier;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ModificationRequest {
    private String serviceId;
    private String serviceType;
    private String serviceIdentifier;
    private Map<String, Object> changes;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ProvisioningResult {
    private boolean success;
    private String taskId;
    private String serviceIdentifier;
    private String message;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ServiceStatus {
    private String serviceId;
    private String serviceType;
    private String status;
    private String serviceIdentifier;
    private Instant lastUpdated;
}
