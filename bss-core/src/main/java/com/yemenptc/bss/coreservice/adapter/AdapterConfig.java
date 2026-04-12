package com.yemenptc.bss.coreservice.adapter;

import lombok.Data;
import lombok.Builder;
import java.time.Instant;
import java.util.Map;
import java.util.HashMap;

@Data
@Builder
public class AdapterConfig {
    private String adapterId;
    private String name;
    private String baseUrl;
    private String protocol;
    private Map<String, String> credentials;
    private int timeout;
    private int maxRetries;
    private boolean enabled;
    private Instant lastHealthCheck;
    private AdapterStatus status;

    public enum AdapterStatus { ACTIVE, INACTIVE, ERROR, MAINTENANCE }
}
