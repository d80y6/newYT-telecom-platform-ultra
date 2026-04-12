package com.yemenptc.bss.coreservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdapterRegistry {

    private final Map<String, ExternalAdapter> adapters = new ConcurrentHashMap<>();

    public void register(String adapterId, ExternalAdapter adapter) {
        adapters.put(adapterId, adapter);
        log.info("Registered adapter: {}", adapterId);
    }

    public ExternalAdapter getAdapter(String adapterId) {
        ExternalAdapter adapter = adapters.get(adapterId);
        if (adapter == null) {
            throw new RuntimeException("Adapter not found: " + adapterId);
        }
        return adapter;
    }

    public List<String> getAdapterIds() {
        return List.copyOf(adapters.keySet());
    }

    public Map<String, AdapterHealth> getAllHealth() {
        Map<String, AdapterHealth> healthMap = new ConcurrentHashMap<>();
        adapters.forEach((id, adapter) -> {
            try {
                boolean healthy = adapter.healthCheck();
                healthMap.put(id, AdapterHealth.builder()
                    .adapterId(id)
                    .healthy(healthy)
                    .build());
            } catch (Exception e) {
                healthMap.put(id, AdapterHealth.builder()
                    .adapterId(id)
                    .healthy(false)
                    .errorMessage(e.getMessage())
                    .build());
            }
        });
        return healthMap;
    }

    @lombok.Data
    @lombok.Builder
    public static class AdapterHealth {
        private String adapterId;
        private boolean healthy;
        private String errorMessage;
    }
}
