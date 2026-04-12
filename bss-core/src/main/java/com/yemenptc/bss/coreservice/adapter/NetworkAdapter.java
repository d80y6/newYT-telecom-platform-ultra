package com.yemenptc.bss.coreservice.adapter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public interface NetworkAdapter {
    
    String getAdapterId();
    
    String getTechnologyType();
    
    ProvisioningResult provision(ProvisionRequest request);
    
    ProvisioningResult deprovision(String serviceId);
    
    StatusResult getStatus(String serviceId);
    
    ProvisioningResult suspend(String serviceId);
    
    ProvisioningResult resume(String serviceId);
    
    boolean isHealthy();
    
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    class ProvisionRequest {
        private String orderId;
        private String serviceId;
        private String customerId;
        private String accountId;
        private String serviceType;
        private String technology;
        private String serviceIdentifier;
        private String productOfferingId;
        private Map<String, String> parameters;
        private Instant requestedAt;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    class ProvisioningResult {
        private boolean success;
        private String serviceId;
        private String orderId;
        private String status;
        private String message;
        private Map<String, String> provisionedDetails;
        private Instant provisionedAt;
        private String errorCode;
        private Instant nextRetryAt;
        private int retryCount;
        
        public static ProvisioningResult success(String serviceId, String orderId, Map<String, String> details) {
            return ProvisioningResult.builder()
                    .success(true)
                    .serviceId(serviceId)
                    .orderId(orderId)
                    .status("ACTIVE")
                    .message("Service provisioned successfully")
                    .provisionedDetails(details)
                    .provisionedAt(Instant.now())
                    .build();
        }
        
        public static ProvisioningResult failure(String serviceId, String orderId, String errorCode, String message) {
            return ProvisioningResult.builder()
                    .success(false)
                    .serviceId(serviceId)
                    .orderId(orderId)
                    .status("FAILED")
                    .errorCode(errorCode)
                    .message(message)
                    .provisionedAt(Instant.now())
                    .retryCount(0)
                    .build();
        }
        
        public ProvisioningResult withRetry(int retryCount, Instant nextRetry) {
            this.retryCount = retryCount;
            this.nextRetryAt = nextRetry;
            return this;
        }
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    class StatusResult {
        private boolean success;
        private String serviceId;
        private String status;
        private String technology;
        private String ipAddress;
        private String macAddress;
        private Integer vlanId;
        private String sessionState;
        private Long uptimeSeconds;
        private BigDecimal bandwidthUp;
        private BigDecimal bandwidthDown;
        private Long dataUsageBytes;
        private Instant lastActivity;
        private Instant statusCheckedAt;
        private String errorMessage;
        
        public static StatusResult active(String serviceId, Map<String, Object> details) {
            return StatusResult.builder()
                    .success(true)
                    .serviceId(serviceId)
                    .status("ACTIVE")
                    .sessionState("CONNECTED")
                    .uptimeSeconds(0L)
                    .dataUsageBytes(0L)
                    .lastActivity(Instant.now())
                    .statusCheckedAt(Instant.now())
                    .build();
        }
    }
}
