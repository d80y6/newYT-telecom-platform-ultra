# Network Elements Adapter Specification

## 1. Overview

The Network Elements Adapter provides integration between the new BSS/OSS platform and various network elements including switches, HLR/HSS, DSLAMs, OLTs, and other network equipment.

### 1.1 Supported Network Elements

| Element | Vendor | Protocol | Purpose |
|---------|--------|----------|---------|
| **PSTN Switches** | EWSD, AXE-10 | TL1, SNMP | Fixed line provisioning |
| **HLR/HSS** | Huawei, Ericsson | MAP, Diameter | Mobile subscriber management |
| **MSC/GGSN** | Multiple | RADIUS, Diameter | 4G/LTE core network |
| **DSLAMs** | Huawei, Nokia | SNMP, TR-069 | DSL line provisioning |
| **OLTs** | Huawei, ZTE | OMCI, TR-069 | FTTH provisioning |
| **PCRF** | Multiple | Diameter (Gx/Gxa) | Policy control |
| **Routers** | Cisco, Juniper | NETCONF, CLI | Enterprise services |
| **Firewalls** | Fortinet, Palo Alto | API | Security services |

### 1.2 Integration Scope

| Capability | Strategy |
|------------|----------|
| Line Provisioning | Migrate to new platform |
| Configuration Management | Keep existing; integrate via adapters |
| Performance Monitoring | Migrate to new platform |
| Alarm Management | Migrate to new platform |
| Backup/Restore | Keep existing; integrate via API |

---

## 2. Integration Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                         NETWORK ELEMENTS ARCHITECTURE                                 │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                         NETWORK ELEMENTS                                         │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │  PSTN       │  │   HLR/HSS   │  │   DSLAM     │  │   OLT       │           │ │
│  │  │  Switches   │  │   (Mobile)  │  │   (DSL)     │  │   (FTTH)    │           │ │
│  │  │  EWSD/AXE   │  │   Huawei/   │  │   Huawei/   │  │   Huawei/   │           │ │
│  │  │             │  │   Ericsson  │  │   Nokia     │  │   ZTE       │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   MSC/GGSN  │  │   PCRF      │  │   Routers   │  │   Firewalls │           │ │
│  │  │   (4G/LTE)  │  │   (Policy)  │  │   Cisco/    │  │   Fortinet/ │           │ │
│  │  │             │  │             │  │   Juniper   │  │   Palo Alto │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  │  ┌─────────────────────────────────────────────────────────────────────┐       │ │
│  │  │                        PROTOCOL LAYER                                │       │ │
│  │  │   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐      │       │ │
│  │  │   │   TL1   │ │   SNMP  │ │NETCONF/ │ │Diameter │ │ RADIUS  │      │       │ │
│  │  │   │         │ │         │ │  YANG   │ │         │ │         │      │       │ │
│  │  │   └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘      │       │ │
│  │  └─────────────────────────────────────────────────────────────────────┘       │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────────────────┘
                                               │
                                               ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                         NETWORK ELEMENTS ADAPTER LAYER                                │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                          ADAPTER REGISTRY                                        │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   TL1       │  │   SNMP      │  │   NETCONF   │  │   Diameter  │           │ │
│  │  │   Adapter   │  │   Adapter   │  │   Adapter   │  │   Adapter   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   RADIUS    │  │   Huawei    │  │   Ericsson  │  │   Cisco     │           │ │
│  │  │   Adapter   │  │   Adapter   │  │   Adapter   │  │   Adapter   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────────────────┘
                                               │
                                               ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              BSS/OSS PLATFORM                                         │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  Service    │  │  Network    │  │  Fault      │  │  Perfor-    │                  │
│  │  Fulfilment │  │  Inventory  │  │  Management │  │  mance      │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Adapter Specifications

### 3.1 PSTN Switch Adapter (TL1/SNMP)

#### Supported Switches

| Switch | Vendor | Protocol | Interface |
|--------|--------|----------|-----------|
| EWSD | Siemens | TL1, SNMP | Command Line, SNMP Traps |
| AXE-10 | Ericsson | TL1, SNMP | Command Line, SNMP Traps |
| Softswitch | Huawei | H.248, SIP | SIP Protocol |

#### TL1 Adapter Implementation

```java
@Service
@Slf4j
public class Tl1Adapter {
    
    private final Tl1Client tl1Client;
    private final KafkaTemplate<String, NetworkEvent> kafkaTemplate;
    private final Tl1CommandParser parser;
    
    @Value("${network-elements.tl1.host}")
    private String tl1Host;
    
    @Value("${network-elements.tl1.port}")
    private int tl1Port;
    
    @PostConstruct
    public void initialize() {
        tl1Client = new Tl1Client(tl1Host, tl1Port);
        tl1Client.connect();
    }
    
    // ============ Line Provisioning ============
    
    public ProvisioningResult provisionLine(LineProvisioningRequest request) {
        try {
            // 1. Build TL1 command
            String command = buildProvisionCommand(request);
            
            // 2. Send command
            Tl1Response response = tl1Client.sendCommand(command);
            
            // 3. Parse response
            ProvisioningResult result = parser.parseProvisionResponse(response);
            
            // 4. Publish event
            if (result.isSuccess()) {
                kafkaTemplate.send("network.line.provisioned",
                    LineProvisionedEvent.builder()
                        .lineNumber(request.getLineNumber())
                        .exchange(request.getExchange())
                        .status("PROVISIONED")
                        .timestamp(Instant.now())
                        .build()
                );
            }
            
            return result;
            
        } catch (Tl1Exception e) {
            log.error("TL1 provisioning failed: {}", e.getMessage());
            return ProvisioningResult.failure(e.getMessage());
        }
    }
    
    private String buildProvisionCommand(LineProvisioningRequest request) {
        return String.format(
            "ENT-SUB::%s:%s::LINE=%s,EXCH=%s,PLAN=%s;",
            request.getTargetId(),
            generateTag(),
            request.getLineNumber(),
            request.getExchange(),
            request.getPlanCode()
        );
    }
    
    // ============ Line Query ============
    
    public LineStatus queryLineStatus(String lineNumber) {
        try {
            String command = String.format(
                "RTRV-SUB::%s:%s;",
                lineNumber,
                generateTag()
            );
            
            Tl1Response response = tl1Client.sendCommand(command);
            
            return parser.parseLineStatus(response);
            
        } catch (Tl1Exception e) {
            log.error("TL1 query failed: {}", e.getMessage());
            throw new NetworkElementException("Line query failed", e);
        }
    }
    
    // ============ Line Release ============
    
    public ProvisioningResult releaseLine(String lineNumber) {
        try {
            String command = String.format(
                "CANC-SUB::%s:%s;",
                lineNumber,
                generateTag()
            );
            
            Tl1Response response = tl1Client.sendCommand(command);
            
            ProvisioningResult result = parser.parseReleaseResponse(response);
            
            if (result.isSuccess()) {
                kafkaTemplate.send("network.line.released",
                    LineReleasedEvent.builder()
                        .lineNumber(lineNumber)
                        .status("RELEASED")
                        .timestamp(Instant.now())
                        .build()
                );
            }
            
            return result;
            
        } catch (Tl1Exception e) {
            log.error("TL1 release failed: {}", e.getMessage());
            return ProvisioningResult.failure(e.getMessage());
        }
    }
    
    // ============ Alarm Handling ============
    
    @EventListener
    public void handleTl1Alarm(Tl1AlarmEvent alarm) {
        log.info("Received TL1 alarm: {}", alarm.getDescription());
        
        kafkaTemplate.send("network.alarm.detected",
            NetworkAlarmEvent.builder()
                .alarmId(generateAlarmId())
                .sourceElement(alarm.getSourceId())
                .alarmType(alarm.getAlarmType())
                .severity(mapSeverity(alarm.getSeverity()))
                .description(alarm.getDescription())
                .timestamp(Instant.now())
                .build()
        );
    }
}
```

### 3.2 HLR/HSS Adapter (MAP/Diameter)

#### Supported HLR/HSS

| Vendor | Interface | Protocol |
|--------|-----------|----------|
| Huawei | HLR | MAP (GSM/UMTS) |
| Huawei | HSS | Diameter (Cx, Sh, Zh) |
| Ericsson | HLR | MAP (GSM/UMTS) |
| Ericsson | HSS | Diameter (Cx, Sh, Zh) |

#### HLR/HSS Operations

| Operation | Description | Interface |
|-----------|-------------|-----------|
| Insert Subscriber Data | Create new subscriber | MAP InsertSubscriberData |
| Delete Subscriber Data | Remove subscriber | MAP DeleteSubscriberData |
| Update Location | Update subscriber location | MAP UpdateLocation |
| Cancel Location | Cancel subscriber location | MAP CancelLocation |
| Authentication | Authenticate subscriber | MAP SendAuthenticationInfo |
| Purge MS | Purge mobile station | MAP PurgeMS |

#### HLR/HSS Adapter Implementation

```java
@Service
@Slf4j
public class HlrHssAdapter {
    
    private final MapClient mapClient;
    private final DiameterClient diameterClient;
    private final KafkaTemplate<String, MobileEvent> kafkaTemplate;
    
    @Value("${network-elements.hlr.vendor}")
    private String hlrVendor;
    
    @Value("${network-elements.hlr.host}")
    private String hlrHost;
    
    @Value("${network-elements.hss.host}")
    private String hssHost;
    
    @PostConstruct
    public void initialize() {
        if ("huawei".equalsIgnoreCase(hlrVendor)) {
            mapClient = new HuaweiMapClient(hlrHost);
            diameterClient = new HuaweiDiameterClient(hssHost);
        } else if ("ericsson".equalsIgnoreCase(hlrVendor)) {
            mapClient = new EricssonMapClient(hlrHost);
            diameterClient = new EricssonDiameterClient(hssHost);
        } else {
            throw new IllegalArgumentException("Unsupported HLR vendor: " + hlrVendor);
        }
    }
    
    // ============ Subscriber Creation ============
    
    public MobileProvisioningResult createSubscriber(MobileProvisioningRequest request) {
        try {
            // 1. Build MAP InsertSubscriberData request
            MapSubscriberData subscriberData = MapSubscriberData.builder()
                .msisdn(request.getMsisdn())
                .imsi(request.getImsi())
                .profile(request.getProfile())
                .services(request.getServices())
                .build();
            
            // 2. Send to HLR
            MapResponse mapResponse = mapClient.insertSubscriberData(subscriberData);
            
            if (mapResponse.isSuccess()) {
                // 3. Also create in HSS if 4G/5G
                if (request.isLteSubscriber()) {
                    createHssSubscriber(request);
                }
                
                // 4. Publish event
                kafkaTemplate.send("mobile.subscriber.created",
                    MobileSubscriberEvent.builder()
                        .msisdn(request.getMsisdn())
                        .imsi(request.getImsi())
                        .hlrId(hlrVendor)
                        .status("ACTIVE")
                        .timestamp(Instant.now())
                        .build()
                );
                
                return MobileProvisioningResult.builder()
                    .success(true)
                    .msisdn(request.getMsisdn())
                    .imsi(request.getImsi())
                    .build();
            }
            
            return MobileProvisioningResult.builder()
                .success(false)
                .errorMessage(mapResponse.getErrorMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to create subscriber: {}", e.getMessage());
            return MobileProvisioningResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    private void createHssSubscriber(MobileProvisioningRequest request) {
        try {
            // Build Diameter User-Data-Request
            DiameterRequest udr = DiameterRequest.builder()
                .applicationId(16777216) // Cx
                .commandCode(306) // UDR
                .userName(request.getImsi())
                .serverName("bss-adapter.ytel.com.ye")
                .dataReference(0) // RepositoryData
                .build();
            
            // Send to HSS
            DiameterResponse response = diameterClient.sendRequest(udr);
            
            if (!response.isSuccess()) {
                throw new MobileProvisioningException("HSS creation failed");
            }
            
        } catch (Exception e) {
            log.error("Failed to create HSS subscriber: {}", e.getMessage());
            throw new MobileProvisioningException("HSS creation failed", e);
        }
    }
    
    // ============ Subscriber Query ============
    
    public MobileSubscriberInfo querySubscriber(String msisdn) {
        try {
            // Query HLR
            MapSubscriberInfo hlrInfo = mapClient.querySubscriber(msisdn);
            
            // Query HSS if available
            DiameterSubscriberInfo hssInfo = null;
            try {
                hssInfo = queryHssSubscriber(msisdn);
            } catch (Exception e) {
                log.warn("HSS query failed: {}", e.getMessage());
            }
            
            return mergeSubscriberInfo(hlrInfo, hssInfo);
            
        } catch (Exception e) {
            log.error("Failed to query subscriber: {}", e.getMessage());
            throw new NetworkElementException("Subscriber query failed", e);
        }
    }
    
    // ============ Service Modification ============
    
    public MobileProvisioningResult modifyServices(MobileModifyRequest request) {
        try {
            MapSubscriberData updates = MapSubscriberData.builder()
                .msisdn(request.getMsisdn())
                .services(request.getNewServices())
                .build();
            
            MapResponse response = mapClient.updateSubscriberData(updates);
            
            if (response.isSuccess()) {
                kafkaTemplate.send("mobile.subscriber.modified",
                    MobileSubscriberEvent.builder()
                        .msisdn(request.getMsisdn())
                        .services(request.getNewServices())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return MobileProvisioningResult.success(request.getMsisdn());
            }
            
            return MobileProvisioningResult.failure(response.getErrorMessage());
            
        } catch (Exception e) {
            log.error("Failed to modify services: {}", e.getMessage());
            return MobileProvisioningResult.failure(e.getMessage());
        }
    }
    
    // ============ Subscriber Termination ============
    
    public MobileProvisioningResult terminateSubscriber(String msisdn) {
        try {
            MapResponse response = mapClient.deleteSubscriberData(msisdn);
            
            if (response.isSuccess()) {
                kafkaTemplate.send("mobile.subscriber.terminated",
                    MobileSubscriberEvent.builder()
                        .msisdn(msisdn)
                        .status("TERMINATED")
                        .timestamp(Instant.now())
                        .build()
                );
                
                return MobileProvisioningResult.success(msisdn);
            }
            
            return MobileProvisioningResult.failure(response.getErrorMessage());
            
        } catch (Exception e) {
            log.error("Failed to terminate subscriber: {}", e.getMessage());
            return MobileProvisioningResult.failure(e.getMessage());
        }
    }
}
```

### 3.3 DSLAM Adapter (SNMP/TR-069)

#### Supported DSLAMs

| Vendor | Model | Protocol | Interface |
|--------|-------|----------|-----------|
| Huawei | MA5600/MA5603 | SNMP, TR-069 | CLI, HTTP |
| Nokia | ISAM | SNMP, TR-069 | CLI, HTTP |
| ZTE | ZXDSL | SNMP, TR-069 | CLI, HTTP |

#### DSLAM Operations

| Operation | Description | SNMP OID |
|-----------|-------------|----------|
| Get Port Status | Query DSLAM port status | ifOperStatus |
| Set Port Profile | Configure speed profile | xDSL Line Profile |
| Enable/Disable Port | Activate/deactivate port | ifAdminStatus |
| Get Performance | Get line performance | xDSL Performance |

#### DSLAM Adapter Implementation

```java
@Service
@Slf4j
public class DslamAdapter {
    
    private final SnmpClient snmpClient;
    private final Tr069Client tr069Client;
    private final KafkaTemplate<String, NetworkEvent> kafkaTemplate;
    
    @Value("${network-elements.dslam.vendor}")
    private String dslamVendor;
    
    @Value("${network-elements.dslam.hosts}")
    private List<String> dslamHosts;
    
    // ============ Port Provisioning ============
    
    public ProvisioningResult provisionPort(PortProvisioningRequest request) {
        try {
            // 1. Determine adapter based on vendor
            DslamPortAdapter portAdapter = getPortAdapter(request.getDslamId());
            
            // 2. Configure port
            PortConfig config = PortConfig.builder()
                .portNumber(request.getPortNumber())
                .vlanId(request.getVlanId())
                .speedProfile(request.getSpeedProfile())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
            
            portAdapter.configurePort(config);
            
            // 3. Enable port
            portAdapter.enablePort(request.getPortNumber());
            
            // 4. Publish event
            kafkaTemplate.send("network.dslam.port.provisioned",
                PortProvisionedEvent.builder()
                    .dslamId(request.getDslamId())
                    .portNumber(request.getPortNumber())
                    .status("ACTIVE")
                    .timestamp(Instant.now())
                    .build()
            );
            
            return ProvisioningResult.builder()
                .success(true)
                .resourceId(request.getDslamId() + ":" + request.getPortNumber())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to provision DSLAM port: {}", e.getMessage());
            return ProvisioningResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ Port Release ============
    
    public ProvisioningResult releasePort(String dslamId, int portNumber) {
        try {
            DslamPortAdapter portAdapter = getPortAdapter(dslamId);
            
            // 1. Disable port
            portAdapter.disablePort(portNumber);
            
            // 2. Clear configuration
            portAdapter.clearConfig(portNumber);
            
            // 3. Publish event
            kafkaTemplate.send("network.dslam.port.released",
                PortReleasedEvent.builder()
                    .dslamId(dslamId)
                    .portNumber(portNumber)
                    .status("RELEASED")
                    .timestamp(Instant.now())
                    .build()
            );
            
            return ProvisioningResult.success(dslamId + ":" + portNumber);
            
        } catch (Exception e) {
            log.error("Failed to release DSLAM port: {}", e.getMessage());
            return ProvisioningResult.failure(e.getMessage());
        }
    }
    
    // ============ Port Status Query ============
    
    public PortStatus queryPortStatus(String dslamId, int portNumber) {
        try {
            DslamPortAdapter portAdapter = getPortAdapter(dslamId);
            
            // Get port status
            PortInfo portInfo = portAdapter.getPortInfo(portNumber);
            
            return PortStatus.builder()
                .dslamId(dslamId)
                .portNumber(portNumber)
                .adminStatus(portInfo.getAdminStatus())
                .operStatus(portInfo.getOperStatus())
                .speedProfile(portInfo.getSpeedProfile())
                .lineRate(portInfo.getLineRate())
                .snr(portInfo.getSnrMargin())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to query port status: {}", e.getMessage());
            throw new NetworkElementException("Port query failed", e);
        }
    }
    
    // ============ Performance Monitoring ============
    
    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void collectPerformanceMetrics() {
        for (String dslamId : dslamHosts) {
            try {
                DslamPortAdapter portAdapter = getPortAdapter(dslamId);
                
                // Get all active ports
                List<Integer> activePorts = portAdapter.getActivePorts();
                
                for (int port : activePorts) {
                    PortPerformance perf = portAdapter.getPerformance(port);
                    
                    kafkaTemplate.send("network.dslam.performance",
                        PortPerformanceEvent.builder()
                            .dslamId(dslamId)
                            .portNumber(port)
                            .downstreamRate(perf.getDownstreamRate())
                            .upstreamRate(perf.getUpstreamRate())
                            .snrMargin(perf.getSnrMargin())
                            .attenuation(perf.getAttenuation())
                            .errors(perf.getErrors())
                            .timestamp(Instant.now())
                            .build()
                    );
                }
                
            } catch (Exception e) {
                log.error("Failed to collect metrics for DSLAM {}: {}", 
                    dslamId, e.getMessage());
            }
        }
    }
}
```

### 3.4 OLT Adapter (OMCI/TR-069)

#### Supported OLTs

| Vendor | Model | Protocol | Interface |
|--------|-------|----------|-----------|
| Huawei | MA5683T | OMCI, TR-069 | CLI, HTTP |
| ZTE | C300/C320 | OMCI, TR-069 | CLI, HTTP |
| Nokia | ISAM FX | OMCI, TR-069 | CLI, HTTP |

#### OLT Operations

| Operation | Description | Protocol |
|-----------|-------------|----------|
| Register ONT | Register new ONT | OMCI |
| Configure Service | Set service parameters | OMCI, TR-069 |
| Query Status | Get ONT status | OMCI, TR-069 |
| Delete ONT | Remove ONT | OMCI |

#### OLT Adapter Implementation

```java
@Service
@Slf4j
public class OltAdapter {
    
    private final OmciClient omciClient;
    private final Tr069Client tr069Client;
    private final KafkaTemplate<String, FtthEvent> kafkaTemplate;
    
    @Value("${network-elements.olt.vendor}")
    private String oltVendor;
    
    // ============ ONT Registration ============
    
    public OntRegistrationResult registerOnt(OntRegistrationRequest request) {
        try {
            // 1. Register ONT
            OmciResponse registerResponse = omciClient.registerOnt(
                request.getOltId(),
                OntRegistration.builder()
                    .serialNumber(request.getOntSerialNumber())
                    .ontType(request.getOntType())
                    .build()
            );
            
            if (registerResponse.isSuccess()) {
                // 2. Configure ONT service
                configureOntService(request);
                
                // 3. Publish event
                kafkaTemplate.send("ftth.ont.registered",
                    OntRegisteredEvent.builder()
                        .oltId(request.getOltId())
                        .ontSerial(request.getOntSerialNumber())
                        .ontId(registerResponse.getOntId())
                        .status("REGISTERED")
                        .timestamp(Instant.now())
                        .build()
                );
                
                return OntRegistrationResult.builder()
                    .success(true)
                    .ontId(registerResponse.getOntId())
                    .build();
            }
            
            return OntRegistrationResult.builder()
                .success(false)
                .errorMessage(registerResponse.getErrorMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to register ONT: {}", e.getMessage());
            return OntRegistrationResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    private void configureOntService(OntRegistrationRequest request) {
        // Configure using TR-069
        tr069Client.configureService(
            request.getOntSerialNumber(),
            ServiceConfig.builder()
                .wanInterface(request.getWanInterface())
                .vlanId(request.getVlanId())
                .ipAddress(request.getIpAddress())
                .subnetMask(request.getSubnetMask())
                .gateway(request.getGateway())
                .dns1(request.getDns1())
                .dns2(request.getDns2())
                .build()
        );
    }
    
    // ============ ONT Status Query ============
    
    public OntStatus queryOntStatus(String oltId, String ontId) {
        try {
            // Query via OMCI
            OmciOntStatus omciStatus = omciClient.queryOntStatus(oltId, ontId);
            
            // Query via TR-069
            Tr069OntStatus tr069Status = tr069Client.queryOntStatus(ontId);
            
            return OntStatus.builder()
                .oltId(oltId)
                .ontId(ontId)
                .serialNumber(omciStatus.getSerialNumber())
                .online(omciStatus.isOnline())
                .rxPower(tr069Status.getRxPower())
                .txPower(tr069Status.getTxPower())
                .temperature(tr069Status.getTemperature())
                .uptime(tr069Status.getUptime())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to query ONT status: {}", e.getMessage());
            throw new NetworkElementException("ONT query failed", e);
        }
    }
    
    // ============ ONT Service Configuration ============
    
    public ProvisioningResult configureService(OntServiceConfigRequest request) {
        try {
            tr069Client.configureService(
                request.getOntSerial(),
                ServiceConfig.builder()
                    .wanInterface(request.getWanInterface())
                    .vlanId(request.getVlanId())
                    .speedProfile(request.getSpeedProfile())
                    .build()
            );
            
            kafkaTemplate.send("ftth.ont.service.configured",
                OntServiceConfiguredEvent.builder()
                    .ontSerial(request.getOntSerial())
                    .wanInterface(request.getWanInterface())
                    .vlanId(request.getVlanId())
                    .timestamp(Instant.now())
                    .build()
            );
            
            return ProvisioningResult.success(request.getOntSerial());
            
        } catch (Exception e) {
            log.error("Failed to configure ONT service: {}", e.getMessage());
            return ProvisioningResult.failure(e.getMessage());
        }
    }
    
    // ============ ONT Deletion ============
    
    public ProvisioningResult deleteOnt(String oltId, String ontId) {
        try {
            // 1. Delete from OLT
            omciClient.deleteOnt(oltId, ontId);
            
            // 2. Publish event
            kafkaTemplate.send("ftth.ont.deleted",
                OntDeletedEvent.builder()
                    .oltId(oltId)
                    .ontId(ontId)
                    .status("DELETED")
                    .timestamp(Instant.now())
                    .build()
            );
            
            return ProvisioningResult.success(oltId + ":" + ontId);
            
        } catch (Exception e) {
            log.error("Failed to delete ONT: {}", e.getMessage());
            return ProvisioningResult.failure(e.getMessage());
        }
    }
}
```

### 3.5 PCRF Adapter (Diameter)

#### PCRF Operations

| Operation | Description | Diameter Interface |
|-----------|-------------|-------------------|
| Create Policy | Create new policy | Gx CCR-Initial |
| Update Policy | Update policy | Gx CCR-Update |
| Delete Policy | Remove policy | Gx CCR-Terminate |
| Get Policy Status | Query policy | Gx CCR-Update |

#### PCRF Adapter Implementation

```java
@Service
@Slf4j
public class PcrfAdapter {
    
    private final DiameterClient gxClient;
    private final DiameterClient gxaClient;
    private final KafkaTemplate<String, PolicyEvent> kafkaTemplate;
    
    @Value("${network-elements.pcrf.gx-host}")
    private String pcrfHost;
    
    @Value("${network-elements.pcrf.gxa-host}")
    private String gxaHost;
    
    // ============ Policy Creation ============
    
    public PolicyResult createPolicy(PolicyCreationRequest request) {
        try {
            // Build Diameter CCR-Initial
            CreditControlRequest ccr = CreditControlRequest.builder()
                .sessionId(generateSessionId())
                .requestType(RequestType.INITIAL)
                .subscriptionId(request.getSubscriptionId())
                .serviceIdentifier(request.getServiceId())
                .requestedServiceUnit(RequestedServiceUnit.builder()
                    .ccTime(request.getQuotaTime())
                    .ccTotalOctets(request.getQuotaData())
                    .build())
                .build();
            
            // Send to PCRF
            CreditControlAnswer cca = gxClient.sendRequest(ccr);
            
            if (cca.getResultCode() == DiameterResultCode.SUCCESS) {
                kafkaTemplate.send("policy.created",
                    PolicyEvent.builder()
                        .policyId(cca.getPolicyId())
                        .subscriptionId(request.getSubscriptionId())
                        .serviceId(request.getServiceId())
                        .grantedUnits(cca.getGrantedServiceUnit())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return PolicyResult.builder()
                    .success(true)
                    .policyId(cca.getPolicyId())
                    .grantedUnits(cca.getGrantedServiceUnit())
                    .build();
            }
            
            return PolicyResult.builder()
                .success(false)
                .errorMessage(cca.getErrorMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to create policy: {}", e.getMessage());
            return PolicyResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ Policy Update ============
    
    public PolicyResult updatePolicy(PolicyUpdateRequest request) {
        try {
            CreditControlRequest ccr = CreditControlRequest.builder()
                .sessionId(request.getSessionId())
                .requestType(RequestType.UPDATE)
                .usedServiceUnit(UsedServiceUnit.builder()
                    .ccTime(request.getUsedTime())
                    .ccTotalOctets(request.getUsedData())
                    .build())
                .requestedServiceUnit(RequestedServiceUnit.builder()
                    .ccTime(request.getAdditionalTime())
                    .ccTotalOctets(request.getAdditionalData())
                    .build())
                .build();
            
            CreditControlAnswer cca = gxClient.sendRequest(ccr);
            
            if (cca.getResultCode() == DiameterResultCode.SUCCESS) {
                return PolicyResult.builder()
                    .success(true)
                    .policyId(request.getPolicyId())
                    .grantedUnits(cca.getGrantedServiceUnit())
                    .build();
            }
            
            return PolicyResult.builder()
                .success(false)
                .errorMessage(cca.getErrorMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to update policy: {}", e.getMessage());
            return PolicyResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ Policy Termination ============
    
    public PolicyResult terminatePolicy(String policyId, String sessionId) {
        try {
            CreditControlRequest ccr = CreditControlRequest.builder()
                .sessionId(sessionId)
                .requestType(RequestType.TERMINATE)
                .build();
            
            CreditControlAnswer cca = gxClient.sendRequest(ccr);
            
            if (cca.getResultCode() == DiameterResultCode.SUCCESS) {
                kafkaTemplate.send("policy.terminated",
                    PolicyEvent.builder()
                        .policyId(policyId)
                        .sessionId(sessionId)
                        .status("TERMINATED")
                        .timestamp(Instant.now())
                        .build()
                );
                
                return PolicyResult.success(policyId);
            }
            
            return PolicyResult.builder()
                .success(false)
                .errorMessage(cca.getErrorMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to terminate policy: {}", e.getMessage());
            return PolicyResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
}
```

---

## 4. Data Synchronization

### 4.1 Alarm Synchronization

| Alarm Source | Direction | Method | Latency |
|--------------|-----------|--------|---------|
| PSTN Switches | Inbound | SNMP Traps | Real-time |
| HLR/HSS | Inbound | SNMP Traps | Real-time |
| DSLAMs | Inbound | SNMP Traps | Real-time |
| OLTs | Inbound | SNMP Traps | Real-time |
| Routers | Inbound | SNMP Traps | Real-time |
| Firewalls | Inbound | Syslog | Real-time |

### 4.2 Performance Synchronization

| Metric Source | Direction | Method | Frequency |
|---------------|-----------|--------|-----------|
| PSTN Switches | Inbound | SNMP Poll | 5 min |
| HLR/HSS | Inbound | SNMP Poll | 5 min |
| DSLAMs | Inbound | SNMP Poll | 5 min |
| OLTs | Inbound | SNMP Poll | 5 min |
| Routers | Inbound | SNMP Poll | 5 min |
| Firewalls | Inbound | API Poll | 5 min |

### 4.3 Configuration Synchronization

| Configuration | Direction | Method | Frequency |
|---------------|-----------|--------|-----------|
| Line Status | Bidirectional | API | Real-time |
| Subscriber Data | Bidirectional | API | Real-time |
| Service Profiles | Bidirectional | API | Real-time |

---

## 5. Error Handling

### 5.1 Retry Strategy

| Error Type | Retry Count | Backoff | Fallback |
|------------|-------------|---------|----------|
| Connection Timeout | 3 | Exponential (2s, 4s, 8s) | Reconnect |
| Authentication Error | 0 | N/A | Alert immediately |
| Protocol Error | 2 | Fixed (5s) | Log and alert |
| Resource Unavailable | 3 | Exponential | Queue for retry |

### 5.2 Circuit Breaker

```java
@Configuration
public class NetworkElementCircuitBreakerConfig {
    
    @Bean
    public CircuitBreaker tl1CircuitBreaker() {
        return CircuitBreaker.of("tl1-switch",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .build()
        );
    }
    
    @Bean
    public CircuitBreaker snmpCircuitBreaker() {
        return CircuitBreaker.of("snmp-device",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .build()
        );
    }
    
    @Bean
    public CircuitBreaker diameterCircuitBreaker() {
        return CircuitBreaker.of("diameter-pcrf",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .build()
        );
    }
}
```

---

## 6. Monitoring & Metrics

### 6.1 Key Metrics

| Metric | Type | Threshold | Alert |
|--------|------|-----------|-------|
| Connection Status | Gauge | Disconnected | Critical |
| Command Latency (p95) | Gauge | >5s | Warning |
| Command Success Rate | Gauge | <99% | Warning |
| Alarm Processing Lag | Gauge | >1 min | Warning |
| Performance Collection | Gauge | Failed | Warning |

### 6.2 Health Checks

```
Health Check Endpoints:
- /health/network-elements/tl1 - TL1 connectivity
- /health/network-elements/snmp - SNMP connectivity
- /health/network-elements/diameter - Diameter connectivity
- /health/network-elements/alarm - Alarm processing
- /health/network-elements/performance - Performance collection
```

---

## 7. Configuration

### 7.1 Application Properties

```yaml
network-elements:
  tl1:
    host: ${NE_TL1_HOST:switch01.internal}
    port: ${NE_TL1_PORT:2362}
    timeout: 30000
    retry-attempts: 3
  
  snmp:
    version: ${NE_SNMP_VERSION:2c}
    community: ${NE_SNMP_COMMUNITY:public}
    timeout: 5000
    retry-attempts: 2
  
  diameter:
    gx:
      host: ${NE_DIAMETER_GX_HOST:pcrf.internal}
      port: ${NE_DIAMETER_GX_PORT:3868}
      realm: ${NE_DIAMETER_REALM:ytel.com.ye}
    gxa:
      host: ${NE_DIAMETER_GXA_HOST:pcrf.internal}
      port: ${NE_DIAMETER_GXA_PORT:3868}
  
  hlr:
    vendor: ${NE_HLR_VENDOR:huawei}
    host: ${NE_HLR_HOST:hlr.internal}
    port: ${NE_HLR_PORT:10000}
  
  dslam:
    vendor: ${NE_DSLAM_VENDOR:huawei}
    hosts: ${NE_DSLAM_HOSTS:dslam01.internal,dslam02.internal}
    snmp-port: ${NE_DSLAM_SNMP_PORT:161}
    tr069-port: ${NE_DSLAM_TR069_PORT:7547}
  
  olt:
    vendor: ${NE_OLT_VENDOR:huawei}
    hosts: ${NE_OLT_HOSTS:olt01.internal,olt02.internal}
    snmp-port: ${NE_OLT_SNMP_PORT:161}
    tr069-port: ${NE_OLT_TR069_PORT:7547}
  
  pcrf:
    gx-host: ${NE_PCRF_GX_HOST:pcrf.internal}
    gx-port: ${NE_PCRF_GX_PORT:3868}
    gxa-host: ${NE_PCRF_GXA_HOST:pcrf.internal}
    gxa-port: ${NE_PCRF_GXA_PORT:3868}
  
  monitoring:
    alarm-poll-interval: 30000
    performance-poll-interval: 300000
    health-check-interval: 60000

kafka:
  topics:
    network-alarm: network.alarm.detected
    network-line: network.line.event
    network-port: network.port.event
    network-performance: network.performance.event
    policy-event: policy.event
```

---

## 8. Migration Strategy

### Phase 1: Connect & Monitor (Months 1-3)
- Deploy network element adapters
- Establish connections
- Monitor alarm and performance data

### Phase 2: Provisioning Migration (Months 4-6)
- Migrate line provisioning to new platform
- Migrate subscriber provisioning to new platform
- Keep configuration management existing

### Phase 3: Configuration Management (Months 7-9)
- Migrate configuration management to new platform
- Consolidate network inventory
- Implement change management

### Phase 4: Full Integration (Months 10-12)
- All operations via new platform
- Decommission legacy management tools
- Optimize performance

---

## 9. Testing

### 9.1 Test Scenarios

| Scenario | Type | Expected |
|----------|------|----------|
| Provision Line | Integration | Line provisioned on switch |
| Query Line Status | Integration | Status returned correctly |
| Release Line | Integration | Line released |
| Create Subscriber | Integration | Subscriber created in HLR |
| Register ONT | Integration | ONT registered on OLT |
| Alarm Receipt | Integration | Alarm processed |

### 9.2 Load Testing

```
Target: 100 concurrent operations
Expected: <5s p95 latency, 99% success rate

Target: 1000 alarms per minute
Expected: <1s processing time, 100% accuracy
```
