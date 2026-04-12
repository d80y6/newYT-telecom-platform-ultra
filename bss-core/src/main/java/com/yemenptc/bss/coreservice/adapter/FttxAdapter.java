package com.yemenptc.bss.coreservice.adapter;

import com.yemenptc.bss.coreservice.entity.NetworkResource;
import com.yemenptc.bss.coreservice.repository.NetworkResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class FttxAdapter implements NetworkAdapter, ExternalAdapter {

    private final AdapterRegistry adapterRegistry;
    private final NetworkResourceRepository networkResourceRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String SERVICE_STATE_KEY_PREFIX = "fttx:service:";
    private static final String PPPOE_STATE_KEY = "fttx:pppoe:";
    
    private final Map<String, ServiceState> serviceStates = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        adapterRegistry.register("FTTX-ADAPTER", this);
        log.info("FTTX/ADSL Adapter initialized and registered");
    }
    
    @Override
    public String getAdapterId() {
        return "FTTX-ADAPTER";
    }
    
    @Override
    public String getTechnologyType() {
        return "FTTH";
    }
    
    @Override
    public boolean healthCheck() {
        try {
            return networkResourceRepository.count() >= 0;
        } catch (Exception e) {
            log.error("FTTX adapter health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public ProvisioningResult provision(ProvisionRequest request) {
        log.info("FTTX provision request: serviceId={}, technology={}", 
                request.getServiceId(), request.getTechnology());
        
        if (serviceStates.containsKey(request.getServiceId())) {
            return ProvisioningResult.failure(
                    request.getServiceId(), 
                    request.getOrderId(),
                    "ALREADY_PROVISIONED",
                    "Service already provisioned"
            );
        }
        
        try {
            String assignedIp = assignIpAddress(request.getServiceIdentifier());
            Integer assignedVlan = assignVlan(request.getParameters());
            String macAddress = generateMacAddress();
            
            ServiceState state = new ServiceState();
            state.serviceId = request.getServiceId();
            state.orderId = request.getOrderId();
            state.customerId = request.getCustomerId();
            state.status = ServiceStatus.PENDING;
            state.ipAddress = assignedIp;
            state.vlanId = assignedVlan;
            state.macAddress = macAddress;
            state.technology = determineTechnology(request);
            state.createdAt = Instant.now();
            state.lastUpdated = Instant.now();
            state.sessionState = SessionState.DISCONNECTED;
            
            serviceStates.put(request.getServiceId(), state);
            persistStateToRedis(state);
            saveToNetworkResource(state);
            
            boolean pppoeSuccess = setupPppoeSession(state);
            if (!pppoeSuccess) {
                state.sessionState = SessionState.ERROR;
                state.status = ServiceStatus.ACTIVE;
                log.warn("PPPoE setup partial success for {}", state.serviceId);
            } else {
                state.sessionState = SessionState.CONNECTED;
                state.uptimeStarted = Instant.now();
            }
            
            state.status = ServiceStatus.ACTIVE;
            state.lastUpdated = Instant.now();
            persistStateToRedis(state);
            
            log.info("FTTX service provisioned: {} with IP {} VLAN {}", 
                    state.serviceId, state.ipAddress, state.vlanId);
            
            return ProvisioningResult.success(
                    state.serviceId,
                    state.orderId,
                    Map.of(
                            "ipAddress", state.ipAddress,
                            "vlanId", String.valueOf(state.vlanId),
                            "macAddress", state.macAddress,
                            "technology", state.technology,
                            "sessionState", state.sessionState.name()
                    )
            );
            
        } catch (Exception e) {
            log.error("FTTX provision failed for {}: {}", request.getServiceId(), e.getMessage());
            return ProvisioningResult.failure(
                    request.getServiceId(),
                    request.getOrderId(),
                    "PROVISION_FAILED",
                    e.getMessage()
            );
        }
    }
    
    @Override
    public ProvisioningResult deprovision(String serviceId) {
        log.info("FTTX deprovision request: serviceId={}", serviceId);
        
        ServiceState state = serviceStates.get(serviceId);
        if (state == null) {
            return ProvisioningResult.failure(serviceId, null, "NOT_FOUND", "Service not found");
        }
        
        try {
            terminatePppoeSession(state);
            releaseIpAddress(state.ipAddress);
            releaseVlan(state.vlanId);
            
            state.status = ServiceStatus.TERMINATED;
            state.sessionState = SessionState.DISCONNECTED;
            state.terminatedAt = Instant.now();
            state.lastUpdated = Instant.now();
            
            persistStateToRedis(state);
            updateNetworkResource(state);
            
            serviceStates.remove(serviceId);
            
            log.info("FTTX service deprovisioned: {}", serviceId);
            
            return ProvisioningResult.success(serviceId, state.orderId, Map.of());
            
        } catch (Exception e) {
            log.error("FTTX deprovision failed for {}: {}", serviceId, e.getMessage());
            return ProvisioningResult.failure(serviceId, state.orderId, "DEPROVISION_FAILED", e.getMessage());
        }
    }
    
    @Override
    public StatusResult getStatus(String serviceId) {
        ServiceState state = loadStateFromRedis(serviceId);
        if (state == null) {
            state = serviceStates.get(serviceId);
        }
        
        if (state == null) {
            return StatusResult.builder()
                    .success(false)
                    .serviceId(serviceId)
                    .errorMessage("Service not found")
                    .statusCheckedAt(Instant.now())
                    .build();
        }
        
        long uptimeSeconds = 0;
        if (state.uptimeStarted != null) {
            uptimeSeconds = Instant.now().getEpochSecond() - state.uptimeStarted.getEpochSecond();
        }
        
        return StatusResult.builder()
                .success(true)
                .serviceId(serviceId)
                .status(state.status.name())
                .technology(state.technology)
                .ipAddress(state.ipAddress)
                .macAddress(state.macAddress)
                .vlanId(state.vlanId)
                .sessionState(state.sessionState.name())
                .uptimeSeconds(uptimeSeconds)
                .bandwidthUp(new BigDecimal("100"))
                .bandwidthDown(new BigDecimal("500"))
                .dataUsageBytes(state.dataUsageBytes)
                .lastActivity(state.lastActivity)
                .statusCheckedAt(Instant.now())
                .build();
    }
    
    @Override
    public ProvisioningResult suspend(String serviceId) {
        log.info("FTTX suspend request: serviceId={}", serviceId);
        
        ServiceState state = serviceStates.get(serviceId);
        if (state == null) {
            return ProvisioningResult.failure(serviceId, null, "NOT_FOUND", "Service not found");
        }
        
        if (state.status == ServiceStatus.SUSPENDED) {
            return ProvisioningResult.failure(serviceId, state.orderId, "ALREADY_SUSPENDED", "Service already suspended");
        }
        
        try {
            suspendPppoeSession(state);
            
            state.status = ServiceStatus.SUSPENDED;
            state.sessionState = SessionState.SUSPENDED;
            state.lastUpdated = Instant.now();
            
            persistStateToRedis(state);
            updateNetworkResource(state);
            
            log.info("FTTX service suspended: {}", serviceId);
            
            return ProvisioningResult.success(serviceId, state.orderId, Map.of("status", "SUSPENDED"));
            
        } catch (Exception e) {
            log.error("FTTX suspend failed for {}: {}", serviceId, e.getMessage());
            return ProvisioningResult.failure(serviceId, state.orderId, "SUSPEND_FAILED", e.getMessage());
        }
    }
    
    @Override
    public ProvisioningResult resume(String serviceId) {
        log.info("FTTX resume request: serviceId={}", serviceId);
        
        ServiceState state = serviceStates.get(serviceId);
        if (state == null) {
            return ProvisioningResult.failure(serviceId, null, "NOT_FOUND", "Service not found");
        }
        
        if (state.status != ServiceStatus.SUSPENDED) {
            return ProvisioningResult.failure(serviceId, state.orderId, "NOT_SUSPENDED", "Service is not suspended");
        }
        
        try {
            resumePppoeSession(state);
            
            state.status = ServiceStatus.ACTIVE;
            state.sessionState = SessionState.CONNECTED;
            state.uptimeStarted = Instant.now();
            state.lastUpdated = Instant.now();
            
            persistStateToRedis(state);
            updateNetworkResource(state);
            
            log.info("FTTX service resumed: {}", serviceId);
            
            return ProvisioningResult.success(serviceId, state.orderId, 
                    Map.of("status", "ACTIVE", "sessionState", "CONNECTED"));
            
        } catch (Exception e) {
            log.error("FTTX resume failed for {}: {}", serviceId, e.getMessage());
            return ProvisioningResult.failure(serviceId, state.orderId, "RESUME_FAILED", e.getMessage());
        }
    }
    
    @Override
    public boolean isHealthy() {
        return healthCheck();
    }
    
    private String assignIpAddress(String serviceIdentifier) {
        int octet3 = ThreadLocalRandom.current().nextInt(1, 255);
        int octet4 = ThreadLocalRandom.current().nextInt(2, 254);
        return "10." + octet3 + "." + octet4 + ".1";
    }
    
    private Integer assignVlan(Map<String, String> parameters) {
        return ThreadLocalRandom.current().nextInt(100, 200);
    }
    
    private String generateMacAddress() {
        long msb = ThreadLocalRandom.current().nextLong(0, 281474976710656L);
        long lsb = ThreadLocalRandom.current().nextLong(0, 281474976710656L);
        return String.format("%012X", msb << 16 | (lsb & 0xFFFF));
    }
    
    private String determineTechnology(ProvisionRequest request) {
        if (request.getTechnology() != null) {
            return request.getTechnology().toUpperCase();
        }
        if (request.getServiceType() != null) {
            String type = request.getServiceType().toUpperCase();
            if (type.contains("FTTH") || type.contains("FIBER")) {
                return "FTTH";
            } else if (type.contains("ADSL") || type.contains("VDSL")) {
                return "ADSL";
            }
        }
        return "FTTH";
    }
    
    private boolean setupPppoeSession(ServiceState state) {
        String pppoeKey = PPPOE_STATE_KEY + state.serviceId;
        try {
            redisTemplate.opsForHash().put(pppoeKey, "username", state.serviceId + "@ptc.ye");
            redisTemplate.opsForHash().put(pppoeKey, "ipAddress", state.ipAddress);
            redisTemplate.opsForHash().put(pppoeKey, "vlan", String.valueOf(state.vlanId));
            redisTemplate.opsForHash().put(pppoeKey, "state", "CONNECTED");
            redisTemplate.expire(pppoeKey, java.time.Duration.ofDays(1));
            return true;
        } catch (Exception e) {
            log.error("PPPoE setup failed: {}", e.getMessage());
            return false;
        }
    }
    
    private void terminatePppoeSession(ServiceState state) {
        String pppoeKey = PPPOE_STATE_KEY + state.serviceId;
        try {
            redisTemplate.delete(pppoeKey);
        } catch (Exception e) {
            log.warn("Failed to cleanup PPPoE state: {}", e.getMessage());
        }
    }
    
    private void suspendPppoeSession(ServiceState state) {
        String pppoeKey = PPPOE_STATE_KEY + state.serviceId;
        try {
            redisTemplate.opsForHash().put(pppoeKey, "state", "SUSPENDED");
        } catch (Exception e) {
            log.warn("Failed to suspend PPPoE: {}", e.getMessage());
        }
    }
    
    private void resumePppoeSession(ServiceState state) {
        String pppoeKey = PPPOE_STATE_KEY + state.serviceId;
        try {
            redisTemplate.opsForHash().put(pppoeKey, "state", "CONNECTED");
        } catch (Exception e) {
            log.warn("Failed to resume PPPoE: {}", e.getMessage());
        }
    }
    
    private void releaseIpAddress(String ipAddress) {
        log.debug("Releasing IP address: {}", ipAddress);
    }
    
    private void releaseVlan(Integer vlanId) {
        log.debug("Releasing VLAN: {}", vlanId);
    }
    
    private void persistStateToRedis(ServiceState state) {
        String key = SERVICE_STATE_KEY_PREFIX + state.serviceId;
        try {
            redisTemplate.opsForHash().put(key, "serviceId", state.serviceId);
            redisTemplate.opsForHash().put(key, "orderId", state.orderId);
            redisTemplate.opsForHash().put(key, "customerId", state.customerId);
            redisTemplate.opsForHash().put(key, "status", state.status.name());
            redisTemplate.opsForHash().put(key, "ipAddress", state.ipAddress);
            redisTemplate.opsForHash().put(key, "vlanId", String.valueOf(state.vlanId));
            redisTemplate.opsForHash().put(key, "macAddress", state.macAddress);
            redisTemplate.opsForHash().put(key, "sessionState", state.sessionState.name());
            redisTemplate.opsForHash().put(key, "technology", state.technology);
            redisTemplate.opsForHash().put(key, "lastUpdated", state.lastUpdated.toString());
            redisTemplate.expire(key, java.time.Duration.ofDays(30));
        } catch (Exception e) {
            log.warn("Failed to persist state to Redis: {}", e.getMessage());
        }
    }
    
    private ServiceState loadStateFromRedis(String serviceId) {
        String key = SERVICE_STATE_KEY_PREFIX + serviceId;
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            if (entries.isEmpty()) {
                return null;
            }
            
            ServiceState state = new ServiceState();
            state.serviceId = (String) entries.get("serviceId");
            state.orderId = (String) entries.get("orderId");
            state.customerId = (String) entries.get("customerId");
            state.status = ServiceStatus.valueOf((String) entries.get("status"));
            state.ipAddress = (String) entries.get("ipAddress");
            state.vlanId = Integer.parseInt((String) entries.get("vlanId"));
            state.macAddress = (String) entries.get("macAddress");
            state.sessionState = SessionState.valueOf((String) entries.get("sessionState"));
            state.technology = (String) entries.get("technology");
            state.lastUpdated = Instant.parse((String) entries.get("lastUpdated"));
            
            return state;
        } catch (Exception e) {
            log.warn("Failed to load state from Redis: {}", e.getMessage());
            return null;
        }
    }
    
    private void saveToNetworkResource(ServiceState state) {
        try {
            Optional<NetworkResource> existing = networkResourceRepository
                    .findByServiceId(state.serviceId);
            
            NetworkResource resource;
            if (existing.isPresent()) {
                resource = existing.get();
            } else {
                resource = NetworkResource.builder()
                        .serviceId(state.serviceId)
                        .resourceId(UUID.randomUUID().toString())
                        .build();
            }
            
            resource.setName("FTTX-" + state.serviceId);
            resource.setResourceType("ACCESS_LINE");
            resource.setTechnology(state.technology);
            resource.setStatus(mapToResourceStatus(state.status));
            resource.setIpAddress(state.ipAddress);
            resource.setMacAddress(state.macAddress);
            resource.setCustomerId(state.customerId);
            
            networkResourceRepository.save(resource);
        } catch (Exception e) {
            log.warn("Failed to save to network resource: {}", e.getMessage());
        }
    }
    
    private void updateNetworkResource(ServiceState state) {
        saveToNetworkResource(state);
    }
    
    private NetworkResource.ResourceStatus mapToResourceStatus(ServiceStatus status) {
        return switch (status) {
            case ACTIVE -> NetworkResource.ResourceStatus.ACTIVE;
            case SUSPENDED -> NetworkResource.ResourceStatus.SUSPENDED;
            case PENDING -> NetworkResource.ResourceStatus.INACTIVE;
            case TERMINATED -> NetworkResource.ResourceStatus.TERMINATED;
        };
    }
    
    public enum ServiceStatus { PENDING, ACTIVE, SUSPENDED, TERMINATED }
    
    public enum SessionState { DISCONNECTED, CONNECTED, AUTHENTICATING, SUSPENDED, ERROR }
    
    private static class ServiceState {
        String serviceId;
        String orderId;
        String customerId;
        ServiceStatus status;
        String ipAddress;
        Integer vlanId;
        String macAddress;
        String technology;
        SessionState sessionState;
        Instant createdAt;
        Instant uptimeStarted;
        Instant lastUpdated;
        Instant terminatedAt;
        Instant lastActivity;
        long dataUsageBytes;
    }
}
