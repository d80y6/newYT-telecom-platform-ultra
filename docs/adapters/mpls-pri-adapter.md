# MPLS/PRI Adapter Specification

## 1. Overview

The MPLS/PRI Adapter provides integration between the new BSS/OSS platform and the existing in-house developed system for enterprise services including MPLS VPN and PRI (Primary Rate Interface) circuits.

### 1.1 System Profile

| Attribute | Value |
|-----------|-------|
| **Vendor** | In-house (Yemen PTC) |
| **System Name** | Enterprise Services Management System |
| **Function** | MPLS VPN, PRI Circuit Management |
| **Location** | On-premise Data Center |
| **Protocol** | REST API, SNMP, NETCONF |
| **SLA** | 99.9% availability |

### 1.2 Current Capabilities

- **MPLS VPN Management**
  - VRF (Virtual Routing and Forwarding) creation
  - Site management
  - Route target assignment
  - Bandwidth allocation
  - Quality of Service (QoS) configuration
- **PRI (Primary Rate Interface)**
  - E1/T1 circuit provisioning
  - Channel allocation
  - D-channel configuration
  - Number assignment
- **SLA Monitoring**
  - Link status monitoring
  - Bandwidth utilization
  - Latency and jitter monitoring
  - Packet loss tracking
- **Customer Portal**
  - Service status visibility
  - Billing information
  - Support ticket management

### 1.3 Integration Scope

| Capability | Strategy |
|------------|----------|
| Circuit Lifecycle | Migrate to new platform; sync to in-house |
| VRF Management | Keep in-house; integrate via API |
| SLA Monitoring | Migrate to new OSS layer |
| Customer Portal | Migrate to new BSS layer |
| Billing | Migrate to new platform |

---

## 2. Integration Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                      MPLS/PRI ENTERPRISE SERVICES ARCHITECTURE                        │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                        ENTERPRISE SERVICES SYSTEM                                │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   MPLS      │  │   PRI       │  │   SLA       │  │   Customer  │           │ │
│  │  │   Manager   │  │   Manager   │  │   Monitor   │  │   Portal    │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                             │ │
│  │  │   Network   │  │   Bandwidth │  │   Inventory │                             │ │
│  │  │   Config    │  │   Manager   │  │   Manager   │                             │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                             │ │
│  │                                                                                   │ │
│  │  ┌─────────────────────────────────────────────────────────────────────┐       │ │
│  │  │                        REST API LAYER                                │       │ │
│  │  └─────────────────────────────────────────────────────────────────────┘       │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                               │                                        │
│                                               ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                          MPLS/PRI ADAPTER                                        │ │
│  │                                                                                   │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │  │
│  │  │   MPLS          │  │   PRI           │  │   SLA           │            │  │
│  │  │   Adapter       │  │   Adapter       │  │   Adapter       │            │  │
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘            │  │
│  │           │                    │                    │                      │  │
│  │           │                    │                    │                      │  │
│  │  ┌────────┴────────────────────┴────────────────────┴────────┐            │  │
│  │  │              CANONICAL TRANSFORM LAYER                     │            │  │
│  │  │   • Circuit Model Mapping                                  │            │  │
│  │  │   • QoS Profile Translation                                │            │  │
│  │  │   • Status Mapping                                          │            │  │
│  │  └───────────────────────────────────────────────────────────┘            │  │
│  │                                                                            │  │
│  └────────────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────────────┘
                                               │
                                               ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              BSS/OSS PLATFORM                                         │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  Enterprise │  │  Product    │  │  Billing    │  │  Circuit    │                  │
│  │  Customer   │  │  Catalog    │  │  Engine     │  │  Management │                  │
│  │  Management │  │             │  │             │  │             │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  SLA        │  │  Order      │  │  Service    │  │  Network    │                  │
│  │  Management │  │  Management │  │  Assurance  │  │  Inventory  │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Interface Specifications

### 3.1 MPLS VPN Management

#### VRF Operations

| Operation | Endpoint | Method | Description |
|-----------|----------|--------|-------------|
| Create VRF | `/api/v1/mpls/vrf` | POST | Create VRF instance |
| Update VRF | `/api/v1/mpls/vrf/{vrfId}` | PUT | Modify VRF settings |
| Delete VRF | `/api/v1/mpls/vrf/{vrfId}` | DELETE | Delete VRF |
| Get VRF | `/api/v1/mpls/vrf/{vrfId}` | GET | Get VRF details |
| List VRFs | `/api/v1/mpls/vrf` | GET | List all VRFs |

#### Site Operations

| Operation | Endpoint | Method | Description |
|-----------|----------|--------|-------------|
| Create Site | `/api/v1/mpls/site` | POST | Add site to VPN |
| Update Site | `/api/v1/mpls/site/{siteId}` | PUT | Update site config |
| Delete Site | `/api/v1/mpls/site/{siteId}` | DELETE | Remove site |
| Get Site | `/api/v1/mpls/site/{siteId}` | GET | Get site details |
| List Sites | `/api/v1/mpls/site?vrf={vrfId}` | GET | List sites in VRF |

#### MPLS Adapter Implementation

```java
@Service
@Slf4j
public class MplsAdapter {
    
    private final WebClient webClient;
    private final KafkaTemplate<String, EnterpriseEvent> kafkaTemplate;
    
    @Value("${mpls-pri.api.base-url}")
    private String baseUrl;
    
    @Value("${mpls-pri.api.key}")
    private String apiKey;
    
    @Bean
    public WebClient mplsWebClient() {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("X-API-Key", apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    // ============ VRF Operations ============
    
    public VrfResult createVrf(VrfRequest request) {
        try {
            InhouseVrfRequest req = InhouseVrfRequest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .routeTarget(request.getRouteTarget())
                .routeDistinguisher(request.getRouteDistinguisher())
                .bandwidthMbps(request.getBandwidthMbps())
                .qosProfile(request.getQosProfile())
                .build();
            
            InhouseVrfResponse response = webClient.post()
                .uri("/api/v1/mpls/vrf")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhouseVrfResponse.class)
                .block();
            
            if (response != null && response.isSuccess()) {
                kafkaTemplate.send("enterprise.mpls.vrf.created",
                    EnterpriseEvent.builder()
                        .eventType("MPLS_VRF_CREATED")
                        .resourceId(response.getVrfId())
                        .resourceName(request.getName())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return VrfResult.builder()
                    .success(true)
                    .vrfId(response.getVrfId())
                    .routeTarget(response.getRouteTarget())
                    .routeDistinguisher(response.getRouteDistinguisher())
                    .build();
            }
            
            return VrfResult.builder()
                .success(false)
                .errorMessage(response.getMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to create VRF: {}", e.getMessage());
            return VrfResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ Site Operations ============
    
    public SiteResult createSite(SiteRequest request) {
        try {
            InhouseSiteRequest req = InhouseSiteRequest.builder()
                .vrfId(request.getVrfId())
                .siteName(request.getSiteName())
                .location(request.getLocation())
                .ceIpAddress(request.getCeIpAddress())
                .peIpAddress(request.getPeIpAddress())
                .vlanId(request.getVlanId())
                .bandwidthMbps(request.getBandwidthMbps())
                .qosPolicy(request.getQosPolicy())
                .build();
            
            InhouseSiteResponse response = webClient.post()
                .uri("/api/v1/mpls/site")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhouseSiteResponse.class)
                .block();
            
            if (response != null && response.isSuccess()) {
                kafkaTemplate.send("enterprise.mpls.site.created",
                    EnterpriseEvent.builder()
                        .eventType("MPLS_SITE_CREATED")
                        .resourceId(response.getSiteId())
                        .vrfId(request.getVrfId())
                        .siteName(request.getSiteName())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return SiteResult.builder()
                    .success(true)
                    .siteId(response.getSiteId())
                    .ceIpAddress(response.getCeIpAddress())
                    .vlanId(response.getVlanId())
                    .build();
            }
            
            return SiteResult.builder()
                .success(false)
                .errorMessage(response.getMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to create site: {}", e.getMessage());
            return SiteResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ QoS Management ============
    
    public QosResult configureQos(QosRequest request) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("siteId", request.getSiteId());
            req.put("policyName", request.getPolicyName());
            req.put("classes", request.getServiceClasses());
            req.put("totalBandwidth", request.getTotalBandwidth());
            
            InhouseQosResponse response = webClient.post()
                .uri("/api/v1/mpls/qos")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhouseQosResponse.class)
                .block();
            
            if (response != null && response.isSuccess()) {
                kafkaTemplate.send("enterprise.mpls.qos.configured",
                    EnterpriseEvent.builder()
                        .eventType("MPLS_QOS_CONFIGURED")
                        .resourceId(request.getSiteId())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return QosResult.success(request.getPolicyName());
            }
            
            return QosResult.failure(response.getMessage());
            
        } catch (Exception e) {
            log.error("Failed to configure QoS: {}", e.getMessage());
            return QosResult.failure(e.getMessage());
        }
    }
}
```

### 3.2 PRI Circuit Management

#### PRI Operations

| Operation | Endpoint | Method | Description |
|-----------|----------|--------|-------------|
| Create PRI | `/api/v1/pri/circuit` | POST | Create PRI circuit |
| Update PRI | `/api/v1/pri/circuit/{circuitId}` | PUT | Modify circuit |
| Delete PRI | `/api/v1/pri/circuit/{circuitId}` | DELETE | Delete circuit |
| Get PRI | `/api/v1/pri/circuit/{circuitId}` | GET | Get circuit details |
| List PRIs | `/api/v1/pri/circuit` | GET | List all circuits |

#### PRI Adapter Implementation

```java
@Service
@Slf4j
public class PriAdapter {
    
    private final WebClient webClient;
    private final KafkaTemplate<String, EnterpriseEvent> kafkaTemplate;
    private final NetworkElementAdapter networkAdapter;
    
    // ============ Circuit Operations ============
    
    public PriCircuitResult createPriCircuit(PriCircuitRequest request) {
        try {
            // 1. Reserve E1/T1 port
            PortReservation portReservation = networkAdapter.reserveE1Port(
                request.getExchange(),
                request.getEquipment()
            );
            
            // 2. Create circuit in in-house system
            InhousePriCircuitRequest req = InhousePriCircuitRequest.builder()
                .circuitName(request.getCircuitName())
                .customerId(request.getCustomerId())
                .exchange(request.getExchange())
                .equipmentId(request.getEquipment())
                .portNumber(portReservation.getPortNumber())
                .e1T1Number(request.getE1T1Number())
                .dChannel(request.getDChannel())
                .bandwidth(request.getBandwidthBps())
                .protocol(request.getProtocol()) // E1, T1, J1
                .build();
            
            InhousePriCircuitResponse response = webClient.post()
                .uri("/api/v1/pri/circuit")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhousePriCircuitResponse.class)
                .block();
            
            if (response != null && response.isSuccess()) {
                // 3. Configure network element
                networkAdapter.configureE1Port(
                    E1Config.builder()
                        .portNumber(portReservation.getPortNumber())
                        .e1T1Number(request.getE1T1Number())
                        .dChannel(request.getDChannel())
                        .timeslotMapping(request.getTimeslotMapping())
                        .protocol(request.getProtocol())
                        .build()
                );
                
                kafkaTemplate.send("enterprise.pri.circuit.created",
                    EnterpriseEvent.builder()
                        .eventType("PRI_CIRCUIT_CREATED")
                        .resourceId(response.getCircuitId())
                        .circuitName(request.getCircuitName())
                        .e1T1Number(request.getE1T1Number())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return PriCircuitResult.builder()
                    .success(true)
                    .circuitId(response.getCircuitId())
                    .portNumber(portReservation.getPortNumber())
                    .e1T1Number(response.getE1T1Number())
                    .dChannel(response.getDChannel())
                    .build();
            }
            
            return PriCircuitResult.builder()
                .success(false)
                .errorMessage(response.getMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to create PRI circuit: {}", e.getMessage());
            return PriCircuitResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ Number Management ============
    
    public NumberResult assignNumbers(String circuitId, List<String> numbers) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("circuitId", circuitId);
            req.put("numbers", numbers);
            
            InhouseNumberResponse response = webClient.post()
                .uri("/api/v1/pri/numbers/assign")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhouseNumberResponse.class)
                .block();
            
            if (response != null && response.isSuccess()) {
                return NumberResult.success(circuitId, numbers);
            }
            
            return NumberResult.failure(response.getMessage());
            
        } catch (Exception e) {
            log.error("Failed to assign numbers: {}", e.getMessage());
            return NumberResult.failure(e.getMessage());
        }
    }
    
    // ============ D-Channel Management ============
    
    public DChannelResult configureDChannel(DChannelRequest request) {
        try {
            networkAdapter.configureDChannel(
                DChannelConfig.builder()
                    .circuitId(request.getCircuitId())
                    .e1T1Number(request.getE1T1Number())
                    .timeslot(request.getTimeslot())  // Usually 16 for E1
                    .protocol(request.getProtocol())  // Q.921, Q.931
                    .build()
            );
            
            kafkaTemplate.send("enterprise.pri.dchannel.configured",
                EnterpriseEvent.builder()
                    .eventType("PRI_DCHANNEL_CONFIGURED")
                    .resourceId(request.getCircuitId())
                    .timestamp(Instant.now())
                    .build()
            );
            
            return DChannelResult.success(request.getCircuitId());
            
        } catch (Exception e) {
            log.error("Failed to configure D-channel: {}", e.getMessage());
            return DChannelResult.failure(e.getMessage());
        }
    }
}
```

### 3.3 SLA Monitoring

```java
@Service
@Slf4j
public class SlaMonitoringAdapter {
    
    private final WebClient webClient;
    private final KafkaTemplate<String, SlaEvent> kafkaTemplate;
    private final PrometheusClient prometheusClient;
    
    // ============ SLA Configuration ============
    
    public SlaConfigResult createSlaConfig(SlaConfigRequest request) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("serviceId", request.getServiceId());
            req.put("serviceType", request.getServiceType());
            req.put("thresholds", request.getThresholds());
            req.put("notificationRules", request.getNotificationRules());
            
            InhouseSlaResponse response = webClient.post()
                .uri("/api/v1/sla/config")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhouseSlaResponse.class)
                .block();
            
            if (response != null && response.isSuccess()) {
                // Configure Prometheus alerting
                prometheusClient.createAlertRule(
                    request.getServiceId(),
                    request.getThresholds()
                );
                
                return SlaConfigResult.success(request.getServiceId());
            }
            
            return SlaConfigResult.failure(response.getMessage());
            
        } catch (Exception e) {
            log.error("Failed to create SLA config: {}", e.getMessage());
            return SlaConfigResult.failure(e.getMessage());
        }
    }
    
    // ============ SLA Metrics Collection ============
    
    @Scheduled(fixedDelay = 60000) // Every minute
    public void collectSlaMetrics() {
        try {
            // Get service list
            List<String> serviceIds = getServiceList();
            
            for (String serviceId : serviceIds) {
                // Query metrics
                SlaMetrics metrics = collectServiceMetrics(serviceId);
                
                // Check thresholds
                checkSlaThresholds(serviceId, metrics);
                
                // Store metrics
                kafkaTemplate.send("enterprise.sla.metrics",
                    SlaMetricsEvent.builder()
                        .serviceId(serviceId)
                        .timestamp(Instant.now())
                        .latencyMs(metrics.getLatencyMs())
                        .jitterMs(metrics.getJitterMs())
                        .packetLossPercent(metrics.getPacketLossPercent())
                        .uptimePercent(metrics.getUptimePercent())
                        .build()
                );
            }
            
        } catch (Exception e) {
            log.error("Failed to collect SLA metrics: {}", e.getMessage());
        }
    }
    
    private SlaMetrics collectServiceMetrics(String serviceId) {
        // Collect from various sources
        double latency = prometheusClient.query(
            "avg(probe_http_duration_seconds{service='" + serviceId + "'})"
        );
        
        double jitter = prometheusClient.query(
            "avg(probe_http_jitter_seconds{service='" + serviceId + "'})"
        );
        
        double packetLoss = prometheusClient.query(
            "avg(probe_http_packet_loss_percent{service='" + serviceId + "'})"
        );
        
        double uptime = calculateUptime(serviceId);
        
        return SlaMetrics.builder()
            .latencyMs(latency * 1000)
            .jitterMs(jitter * 1000)
            .packetLossPercent(packetLoss)
            .uptimePercent(uptime)
            .build();
    }
    
    private void checkSlaThresholds(String serviceId, SlaMetrics metrics) {
        SlaConfig config = getSlaConfig(serviceId);
        
        if (metrics.getLatencyMs() > config.getMaxLatencyMs()) {
            kafkaTemplate.send("enterprise.sla.violation",
                SlaViolationEvent.builder()
                    .serviceId(serviceId)
                    .violationType("LATENCY")
                    .threshold(config.getMaxLatencyMs())
                    .actual(metrics.getLatencyMs())
                    .timestamp(Instant.now())
                    .build()
            );
        }
        
        if (metrics.getPacketLossPercent() > config.getMaxPacketLossPercent()) {
            kafkaTemplate.send("enterprise.sla.violation",
                SlaViolationEvent.builder()
                    .serviceId(serviceId)
                    .violationType("PACKET_LOSS")
                    .threshold(config.getMaxPacketLossPercent())
                    .actual(metrics.getPacketLossPercent())
                    .timestamp(Instant.now())
                    .build()
            );
        }
        
        if (metrics.getUptimePercent() < config.getMinUptimePercent()) {
            kafkaTemplate.send("enterprise.sla.violation",
                SlaViolationEvent.builder()
                    .serviceId(serviceId)
                    .violationType("UPTIME")
                    .threshold(config.getMinUptimePercent())
                    .actual(metrics.getUptimePercent())
                    .timestamp(Instant.now())
                    .build()
            );
        }
    }
    
    // ============ SLA Reports ============
    
    public SlaReport generateSlaReport(String serviceId, DateRange dateRange) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("serviceId", serviceId);
            params.put("startDate", dateRange.getStartDate().toString());
            params.put("endDate", dateRange.getEndDate().toString());
            
            InhouseSlaReportResponse response = webClient.get()
                .uri("/api/v1/sla/report?serviceId={serviceId}&startDate={startDate}&endDate={endDate}",
                    params)
                .retrieve()
                .bodyToMono(InhouseSlaReportResponse.class)
                .block();
            
            if (response != null && response.isSuccess()) {
                return SlaReport.builder()
                    .serviceId(serviceId)
                    .dateRange(dateRange)
                    .metricsSummary(response.getMetricsSummary())
                    .violations(response.getViolations())
                    .compliancePercent(response.getCompliancePercent())
                    .build();
            }
            
            throw new SlaReportException("Failed to generate SLA report");
            
        } catch (Exception e) {
            log.error("Failed to generate SLA report: {}", e.getMessage());
            throw new SlaReportException("SLA report generation failed", e);
        }
    }
}
```

---

## 4. Data Synchronization

### 4.1 Real-time Sync

| Event | Direction | Channel | Latency |
|-------|-----------|---------|---------|
| VRF Created | Inhouse → Platform | Kafka Event | <1s |
| Site Created | Inhouse → Platform | Kafka Event | <1s |
| Circuit Created | Inhouse → Platform | Kafka Event | <1s |
| Configuration Changed | Inhouse → Platform | Kafka Event | <1s |
| Status Changed | Inhouse → Platform | Kafka Event | <1s |
| SLA Violation | Inhouse → Platform | Kafka Event | <1s |

### 4.2 Batch Sync

| Data | Frequency | Method | Records |
|------|-----------|--------|---------|
| VRF List | Daily | API Call | ~500 |
| Site List | Daily | API Call | ~2K |
| Circuit List | Daily | API Call | ~1K |
| Configuration Audit | Weekly | API Call | ~5K |

### 4.3 Sync Implementation

```java
@Service
@Slf4j
public class MplsPriSyncService {
    
    private final MplsAdapter mplsAdapter;
    private final PriAdapter priAdapter;
    private final SlaMonitoringAdapter slaAdapter;
    
    @Scheduled(cron = "0 0 4 * * *") // 4 AM daily
    public void performDailySync() {
        log.info("Starting daily MPLS/PRI sync");
        
        try {
            // 1. Sync MPLS VPNs
            syncMplsVpns();
            
            // 2. Sync PRI Circuits
            syncPriCircuits();
            
            // 3. Sync SLA Configs
            syncSlaConfigs();
            
            log.info("Daily MPLS/PRI sync completed");
            
        } catch (Exception e) {
            log.error("Daily MPLS/PRI sync failed: {}", e.getMessage());
            alertTeam("MPLS/PRI sync failed", e);
        }
    }
    
    private void syncMplsVpns() {
        log.info("Syncing MPLS VPNs");
        
        // Get list from in-house system
        List<VrfInfo> mplsVrfs = mplsAdapter.listVrfs();
        
        // Sync to platform
        for (VrfInfo vrf : mplsVrfs) {
            syncVrfToPlatform(vrf);
            
            // Sync sites for each VRF
            List<SiteInfo> sites = mplsAdapter.listSites(vrf.getVrfId());
            for (SiteInfo site : sites) {
                syncSiteToPlatform(site);
            }
        }
    }
    
    private void syncPriCircuits() {
        log.info("Syncing PRI circuits");
        
        // Get list from in-house system
        List<PriCircuitInfo> priCircuits = priAdapter.listCircuits();
        
        // Sync to platform
        for (PriCircuitInfo circuit : priCircuits) {
            syncPriCircuitToPlatform(circuit);
        }
    }
    
    private void syncSlaConfigs() {
        log.info("Syncing SLA configurations");
        
        // Get list from in-house system
        List<SlaConfigInfo> slaConfigs = slaAdapter.listConfigs();
        
        // Sync to platform
        for (SlaConfigInfo config : slaConfigs) {
            syncSlaConfigToPlatform(config);
        }
    }
}
```

---

## 5. Error Handling

### 5.1 Retry Strategy

| Error Type | Retry Count | Backoff | Fallback |
|------------|-------------|---------|----------|
| API Timeout | 3 | Exponential (2s, 4s, 8s) | Queue for retry |
| API Error (5xx) | 3 | Exponential | Alert and queue |
| API Error (4xx) | 0 | N/A | Alert immediately |
| Configuration Error | 2 | Fixed (5s) | Rollback changes |
| Network Element Error | 3 | Exponential | Manual intervention |

### 5.2 Rollback Strategy

```java
@Service
@Slf4j
public class MplsRollbackService {
    
    private final MplsAdapter mplsAdapter;
    private final PriAdapter priAdapter;
    private final RollbackLogRepository rollbackLogRepo;
    
    public void executeRollback(String transactionId) {
        try {
            RollbackLog rollbackLog = rollbackLogRepo.findByTransactionId(transactionId);
            
            if (rollbackLog == null) {
                log.error("No rollback log found for transaction: {}", transactionId);
                return;
            }
            
            switch (rollbackLog.getResourceType()) {
                case "MPLS_VRF":
                    rollbackVrf(rollbackLog);
                    break;
                case "MPLS_SITE":
                    rollbackSite(rollbackLog);
                    break;
                case "PRI_CIRCUIT":
                    rollbackPriCircuit(rollbackLog);
                    break;
                case "SLA_CONFIG":
                    rollbackSlaConfig(rollbackLog);
                    break;
            }
            
            log.info("Rollback completed for transaction: {}", transactionId);
            
        } catch (Exception e) {
            log.error("Rollback failed for transaction: {}", transactionId, e);
            alertService.sendAlert("rollback_failed", e.getMessage());
        }
    }
    
    private void rollbackVrf(RollbackLog rollbackLog) {
        // Implement VRF rollback logic
        if ("CREATE".equals(rollbackLog.getOperation())) {
            mplsAdapter.deleteVrf(rollbackLog.getResourceId());
        } else if ("UPDATE".equals(rollbackLog.getOperation())) {
            mplsAdapter.updateVrf(rollbackLog.getPreviousState());
        }
    }
    
    private void rollbackSite(RollbackLog rollbackLog) {
        // Implement site rollback logic
        if ("CREATE".equals(rollbackLog.getOperation())) {
            mplsAdapter.deleteSite(rollbackLog.getResourceId());
        } else if ("UPDATE".equals(rollbackLog.getOperation())) {
            mplsAdapter.updateSite(rollbackLog.getPreviousState());
        }
    }
    
    private void rollbackPriCircuit(RollbackLog rollbackLog) {
        // Implement PRI circuit rollback logic
        if ("CREATE".equals(rollbackLog.getOperation())) {
            priAdapter.deleteCircuit(rollbackLog.getResourceId());
        } else if ("UPDATE".equals(rollbackLog.getOperation())) {
            priAdapter.updateCircuit(rollbackLog.getPreviousState());
        }
    }
}
```

---

## 6. Monitoring & Metrics

### 6.1 Key Metrics

| Metric | Type | Threshold | Alert |
|--------|------|-----------|-------|
| API Latency (p95) | Gauge | >500ms | Warning |
| API Success Rate | Gauge | <99% | Warning |
| Configuration Latency | Gauge | >30s | Warning |
| SLA Compliance | Gauge | <99% | Critical |
| Active VRFs | Gauge | - | Info |
| Active Circuits | Gauge | - | Info |

### 6.2 Health Checks

```
Health Check Endpoints:
- /health/mpls-pri/api - API connectivity
- /health/mpls-pri/mpls - MPLS service status
- /health/mpls-pri/pri - PRI service status
- /health/mpls-pri/sla - SLA monitoring status
```

---

## 7. Configuration

### 7.1 Application Properties

```yaml
mpls-pri:
  api:
    base-url: ${MPLS_PRI_API_URL:http://enterprise.internal:8080}
    key: ${MPLS_PRI_API_KEY:}
    timeout: 30000
    retry-attempts: 3
  
  mpls:
    max-vrfs-per-customer: 10
    max-sites-per-vrf: 50
    default-bandwidth-mbps: 10
    qos-profiles:
      - gold
      - silver
      - bronze
  
  pri:
    default-protocol: E1
    d-channel-timeslot: 16
    max-circuits-per-customer: 5
  
  sla:
    monitoring-interval-ms: 60000
    reporting-schedule: "0 0 8 * * *"  # 8 AM daily
    retention-days: 365
    alert-thresholds:
      latency-ms: 100
      jitter-ms: 20
      packet-loss-percent: 0.1
      uptime-percent: 99.9
  
  sync:
    enabled: true
    schedule: "0 0 4 * * *"  # 4 AM daily
  
  security:
    credential-store: hashicorp-vault
    secret-path: secret/mpls-pri

kafka:
  topics:
    enterprise-event: enterprise.event
    sla-metrics: enterprise.sla.metrics
    sla-violation: enterprise.sla.violation
```

---

## 8. Migration Strategy

### Phase 1: Connect & Monitor (Months 1-3)
- Deploy adapters
- In-house system remains primary
- New platform monitors and syncs data
- Build integration tests

### Phase 2: New Services via Platform (Months 4-6)
- New enterprise services created in new platform
- Sync to in-house for provisioning
- Monitor sync success rate

### Phase 3: SLA Monitoring Migration (Months 7-9)
- Migrate SLA monitoring to new platform
- Keep in-house for configuration management
- Consolidate SLA reporting

### Phase 4: Configuration Management (Months 10-12)
- Migrate configuration management to new platform
- In-house becomes reporting backend only

### Phase 5: Retirement (Months 13-18)
- Decommission in-house system
- Archive for compliance

---

## 9. Testing

### 9.1 Test Scenarios

| Scenario | Type | Expected |
|----------|------|----------|
| Create VRF | Integration | VRF created in both systems |
| Create Site | Integration | Site added to VRF |
| Create PRI Circuit | Integration | Circuit provisioned |
| Configure QoS | Integration | QoS policy applied |
| SLA Violation | Integration | Alert triggered |
| Rollback | Integration | Configuration reverted |

### 9.2 Load Testing

```
Target: 50 concurrent API calls
Expected: <500ms p95 latency, 99% success rate

Target: 1000 services to monitor
Expected: <1 minute collection time, 100% accuracy
```
