# In-House ADSL/FTTH Adapter Specification

## 1. Overview

The In-House ADSL/FTTH Adapter provides integration between the new BSS/OSS platform and the existing in-house developed system that handles prepaid billing and OSS functions for ADSL and FTTH services.

### 1.1 System Profile

| Attribute | Value |
|-----------|-------|
| **Vendor** | In-house (Yemen PTC) |
| **System Name** | Yemen Net Broadband System |
| **Function** | ADSL/FTTH Prepaid Billing, AAA, OSS |
| **Location** | On-premise Data Center |
| **Protocol** | RADIUS, HTTP/REST, DHCP, SNMP |
| **SLA** | 99.9% availability |

### 1.2 Current Capabilities

- **AAA (Authentication, Authorization, Accounting)**
  - RADIUS authentication
  - Session management
  - Quota enforcement
- **Prepaid Billing**
  - Time-based packages
  - Data-based packages
  - Voucher recharge
- **DSLAM Provisioning**
  - VLAN assignment
  - Port allocation
  - Speed profile configuration
- **FTTH Provisioning**
  - ONT registration
  - Service VLAN mapping
  - QoS configuration

### 1.3 Integration Scope

| Capability | Strategy |
|------------|----------|
| AAA (RADIUS) | Migrate to new platform; keep in-house as backup |
| Prepaid Billing | Migrate to new platform gradually |
| Provisioning | Migrate to new OSS layer |
| DHCP Management | Keep in-house; integrate via API |
| DSLAM/FTTH Config | Migrate to new Provisioning |

---

## 2. Integration Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                         IN-HOUSE BROADBAND SYSTEM ARCHITECTURE                        │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           APPLICATION LAYER                                       │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   Web       │  │   Admin     │  │   Self-     │  │   Voucher   │           │ │
│  │  │   Portal    │  │   Portal    │  │   Service   │  │   System    │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                               │                                        │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           SERVICE LAYER                                           │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │  AAA        │  │   Billing   │  │ Provisioning│  │   DHCP      │           │ │
│  │  │  Service    │  │   Service   │  │   Service   │  │   Service   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                               │                                        │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           DATA LAYER                                              │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                             │ │
│  │  │  MySQL      │  │   Redis     │  │   File     │                             │ │
│  │  │  Database   │  │   Cache     │  │   System   │                             │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                             │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                               │                                        │
└───────────────────────────────────────────────┼────────────────────────────────────────┘
                                                │
                                                ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                         ADAPTER LAYER (NEW BSS/OSS)                                   │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                         IN-HOUSE BROADBAND ADAPTER                               │ │
│  │                                                                                   │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │  │
│  │  │   REST API     │  │   RADIUS        │  │   File         │            │  │
│  │  │   Adapter      │  │   Gateway       │  │   Adapter      │            │  │
│  │  │                │  │                 │  │                │            │  │
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘            │  │
│  │           │                      │                      │                      │  │
│  │           │                      │                      │                      │  │
│  │  ┌────────┴──────────────────────┴──────────────────────┴────────┐           │  │
│  │  │              CANONICAL TRANSFORM LAYER                          │           │  │
│  │  │   • Account Model Mapping                                      │           │  │
│  │  │   • Usage Record Normalization                                 │           │  │
│  │  │   • Provisioning Command Translation                           │           │  │
│  │  └───────────────────────────────────────────────────────────────┘           │  │
│  │                                                                                │  │
│  └────────────────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────────────┘
                                                │
                                                ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              BSS/OSS PLATFORM                                         │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  Customer   │  │  Product    │  │  Billing    │  │  Service    │                  │
│  │  Management │  │  Catalog    │  │  Engine     │  │  Fulfil-    │                  │
│  │             │  │             │  │  (Unified)  │  │  ment       │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  Order      │  │  Revenue    │  │  Usage      │  │  Provisioning│                  │
│  │  Management │  │  Assurance │  │  Analytics  │  │  (Unified)  │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Interface Specifications

### 3.1 REST API Adapter

#### Current API Endpoints (In-House System)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/customer` | POST | Create customer |
| `/api/customer/{id}` | GET | Get customer details |
| `/api/customer/{id}` | PUT | Update customer |
| `/api/service/activate` | POST | Activate broadband service |
| `/api/service/deactivate` | POST | Deactivate service |
| `/api/service/status` | GET | Get service status |
| `/api/account/balance` | GET | Get account balance |
| `/api/account/recharge` | POST | Recharge account |
| `/api/account/deduct` | POST | Deduct from balance |
| `/api/voucher/validate` | POST | Validate voucher |
| `/api/voucher/redeem` | POST | Redeem voucher |
| `/api/usage/query` | GET | Query usage data |
| `/api/line/provision` | POST | Provision DSLAM port |
| `/api/line/release` | POST | Release DSLAM port |

#### Adapter Implementation

```java
@Service
@Slf4j
public class InhouseBroadbandAdapter {
    
    private final WebClient webClient;
    private final KafkaTemplate<String, BroadbandEvent> kafkaTemplate;
    private final RADIUSClient radiusClient;
    private final ObjectMapper objectMapper;
    
    @Value("${inhouse-broadband.api.base-url}")
    private String baseUrl;
    
    @Value("${inhouse-broadband.api.key}")
    private String apiKey;
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("X-API-Key", apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(loggingFilter())
            .build();
    }
    
    // ============ Customer Operations ============
    
    public CustomerResult createCustomer(CustomerRequest request) {
        try {
            InhouseCustomerRequest req = InhouseCustomerRequest.builder()
                .nationalId(request.getNationalId())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .serviceType(request.getServiceType())  // ADSL or FTTH
                .build();
            
            InhouseCustomerResponse response = webClient.post()
                .uri("/api/customer")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhouseCustomerResponse.class)
                .block();
            
            if (response != null) {
                kafkaTemplate.send("customer.created",
                    CustomerCreatedEvent.builder()
                        .customerId(response.getCustomerId())
                        .accountNo(response.getAccountNo())
                        .serviceType(request.getServiceType())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return CustomerResult.success(
                    response.getCustomerId(),
                    response.getAccountNo()
                );
            }
            
            return CustomerResult.failure("No response from inhouse system");
            
        } catch (WebClientResponseException e) {
            log.error("Failed to create customer: {}", e.getResponseBodyAsString());
            return CustomerResult.failure(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating customer: {}", e.getMessage());
            return CustomerResult.failure(e.getMessage());
        }
    }
    
    // ============ Service Activation ============
    
    public ServiceActivationResult activateService(ServiceActivationRequest request) {
        try {
            InhouseActivationRequest req = InhouseActivationRequest.builder()
                .accountNo(request.getAccountNo())
                .serviceType(request.getServiceType())  // ADSL | FTTH
                .packageCode(request.getPackageCode())
                .speedProfile(request.getSpeedProfile())
                .vlanId(request.getVlanId())
                .technicianId(request.getTechnicianId())
                .installAddress(request.getInstallAddress())
                .ontSerialNumber(request.getOntSerialNumber())  // For FTTH
                .build();
            
            InhouseActivationResponse response = webClient.post()
                .uri("/api/service/activate")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhouseActivationResponse.class)
                .block();
            
            if (response != null && "SUCCESS".equals(response.getStatus())) {
                // Trigger provisioning workflow
                kafkaTemplate.send("service.activation.started",
                    ServiceActivationEvent.builder()
                        .orderId(request.getOrderId())
                        .serviceId(response.getServiceId())
                        .accountNo(request.getAccountNo())
                        .serviceType(request.getServiceType())
                        .status("ACTIVATING")
                        .timestamp(Instant.now())
                        .build()
                );
                
                // Provision network elements
                provisionNetworkElements(request, response);
                
                return ServiceActivationResult.builder()
                    .success(true)
                    .serviceId(response.getServiceId())
                    .username(response.getUsername())
                    .password(response.getPassword())
                    .build();
            }
            
            return ServiceActivationResult.builder()
                .success(false)
                .errorMessage(response.getMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Service activation failed: {}", e.getMessage());
            return ServiceActivationResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    private void provisionNetworkElements(
        ServiceActivationRequest request,
        InhouseActivationResponse response
    ) {
        ProvisioningTask task = ProvisioningTask.builder()
            .serviceId(response.getServiceId())
            .serviceType(request.getServiceType())
            .accountNo(request.getAccountNo())
            .vlanId(request.getVlanId())
            .speedProfile(request.getSpeedProfile())
            .ontSerialNumber(request.getOntSerialNumber())
            .build();
        
        kafkaTemplate.send("provisioning.task", task);
    }
    
    // ============ Balance & Recharge ============
    
    public BalanceInfo queryBalance(String accountNo) {
        try {
            InhouseBalanceResponse response = webClient.get()
                .uri("/api/account/balance?accountNo={accountNo}", accountNo)
                .retrieve()
                .bodyToMono(InhouseBalanceResponse.class)
                .block();
            
            if (response != null) {
                return BalanceInfo.builder()
                    .accountNo(accountNo)
                    .mainBalance(response.getMainBalance())
                    .bonusBalance(response.getBonusBalance())
                    .dataQuota(response.getDataQuotaBytes())
                    .timeQuota(response.getTimeQuotaMinutes())
                    .validityEndDate(response.getValidityEndDate())
                    .build();
            }
            
            throw new BroadbandAdapterException("No balance response");
            
        } catch (Exception e) {
            log.error("Balance query failed: {}", e.getMessage());
            throw new BroadbandAdapterException("Balance query failed", e);
        }
    }
    
    public RechargeResult recharge(RechargeRequest request) {
        try {
            InhouseRechargeRequest req = InhouseRechargeRequest.builder()
                .accountNo(request.getAccountNo())
                .voucherCode(request.getVoucherCode())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .referenceId(generateReferenceId())
                .build();
            
            InhouseRechargeResponse response = webClient.post()
                .uri("/api/account/recharge")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(InhouseRechargeResponse.class)
                .block();
            
            if (response != null && response.isSuccess()) {
                kafkaTemplate.send("billing.recharge.completed",
                    BroadbandRechargeEvent.builder()
                        .accountNo(request.getAccountNo())
                        .amount(request.getAmount())
                        .newBalance(response.getNewBalance())
                        .validityDays(response.getValidityDays())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return RechargeResult.builder()
                    .success(true)
                    .newBalance(response.getNewBalance())
                    .validityEndDate(response.getValidityEndDate())
                    .build();
            }
            
            return RechargeResult.builder()
                .success(false)
                .errorMessage(response.getMessage())
                .build();
                
        } catch (Exception e) {
            log.error("Recharge failed: {}", e.getMessage());
            return RechargeResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    public VoucherValidationResult validateVoucher(String voucherCode) {
        try {
            InhouseVoucherResponse response = webClient.post()
                .uri("/api/voucher/validate")
                .bodyValue(Map.of("voucherCode", voucherCode))
                .retrieve()
                .bodyToMono(InhouseVoucherResponse.class)
                .block();
            
            return VoucherValidationResult.builder()
                .valid(response.isValid())
                .voucherValue(response.getValue())
                .voucherType(response.getType())
                .validityDays(response.getValidityDays())
                .build();
                
        } catch (Exception e) {
            return VoucherValidationResult.builder()
                .valid(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
}
```

### 3.2 RADIUS Gateway

#### RADIUS Flow

```
┌──────────┐    Access-Request    ┌──────────┐    Access-Request    ┌──────────┐
│  CPE     │────────────────────▶│  ADAPTER │────────────────────▶│ Inhouse  │
│ (Modem)  │                      │  (RADIUS)│                      │  AAA     │
│          │◀────────────────────│          │◀────────────────────│          │
└──────────┘    Access-Accept    └──────────┘    Access-Accept    └──────────┘
```

#### Supported RADIUS Attributes

| Attribute | Direction | Description |
|-----------|-----------|-------------|
| User-Name | In/Out | Username |
| User-Password | In | Password |
| NAS-IP-Address | In | NAS identifier |
| NAS-Port | In | Physical port |
| Service-Type | In | Framed/User |
| Framed-Protocol | In | PPP/Async |
| Framed-IP-Address | Out | Assigned IP |
| Framed-Netmask | Out | IP netmask |
| Session-Timeout | Out | Max session time |
| Data/Bandwidth Limits | Out | Rate limits |

#### RADIUS Adapter Implementation

```java
@Service
@Slf4j
public class InhouseRadiusAdapter {
    
    private final RADIUSClient radiusClient;
    private final KafkaTemplate<String, RadiusEvent> kafkaTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${inhouse-broadband.radius.auth-port}")
    private int authPort;
    
    @Value("${inhouse-broadband.radius.acct-port}")
    private int acctPort;
    
    @Value("${inhouse-broadband.radius.secret}")
    private String radiusSecret;
    
    @PostConstruct
    public void initializeRadius() {
        radiusClient = RADIUSClient.builder()
            .authPort(authPort)
            .accountingPort(acctPort)
            .secret(radiusSecret)
            .build();
    }
    
    // ============ Authentication ============
    
    public RadiusAuthResult authenticate(RadiusAuthRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. Check cache first
            String cached = redisTemplate.opsForValue()
                .get("radius:auth:" + request.getUsername());
            
            if (cached != null) {
                return fromJson(cached, RadiusAuthResult.class);
            }
            
            // 2. Build RADIUS Access-Request
            AccessRequest accessRequest = AccessRequest.builder()
                .userName(request.getUsername())
                .userPassword(request.getPassword())
                .nasIpAddress(request.getNasIpAddress())
                .nasPort(request.getNasPort())
                .callingStationId(request.getMacAddress())
                .build();
            
            // 3. Forward to inhouse AAA
            AccessResponse response = radiusClient.authenticate(accessRequest);
            
            RadiusAuthResult result;
            if (response.getType() == AccessType.ACCEPT) {
                result = RadiusAuthResult.builder()
                    .accepted(true)
                    .sessionId(response.getSessionId())
                    .framedIp(response.getFramedIp())
                    .framedNetmask(response.getFramedNetmask())
                    .sessionTimeout(response.getSessionTimeout())
                    .uploadRate(response.getUploadRate())
                    .downloadRate(response.getDownloadRate())
                    .build();
                
                // Cache successful auth
                redisTemplate.opsForValue().set(
                    "radius:auth:" + request.getUsername(),
                    toJson(result),
                    Duration.ofMinutes(5)
                );
                
                log.info("RADIUS authentication successful for user: {}", 
                    request.getUsername());
                    
            } else {
                result = RadiusAuthResult.builder()
                    .accepted(false)
                    .errorMessage(response.getReplyMessage())
                    .build();
                
                log.warn("RADIUS authentication failed for user: {}", 
                    request.getUsername());
            }
            
            // 4. Publish event
            publishAuthEvent(request, result, System.currentTimeMillis() - startTime);
            
            return result;
            
        } catch (Exception e) {
            log.error("RADIUS authentication error: {}", e.getMessage());
            
            // Fallback to API call
            return authenticateViaApi(request);
        }
    }
    
    private RadiusAuthResult authenticateViaApi(RadiusAuthRequest request) {
        try {
            InhouseCustomerResponse response = webClient.post()
                .uri("/api/auth/radius")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InhouseCustomerResponse.class)
                .block();
            
            if (response != null && response.isAuthenticated()) {
                return RadiusAuthResult.builder()
                    .accepted(true)
                    .sessionId(response.getSessionId())
                    .framedIp(response.getFramedIp())
                    .framedNetmask("255.255.255.0")
                    .sessionTimeout(86400) // 24 hours
                    .build();
            }
            
            return RadiusAuthResult.builder()
                .accepted(false)
                .errorMessage("Authentication failed")
                .build();
                
        } catch (Exception e) {
            return RadiusAuthResult.builder()
                .accepted(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ Accounting ============
    
    public void processAccounting(AccountingRequest request) {
        try {
            AccountingRecord record = AccountingRecord.builder()
                .sessionId(request.getSessionId())
                .username(request.getUsername())
                .nasIpAddress(request.getNasIpAddress())
                .framedIpAddress(request.getFramedIpAddress())
                .inputOctets(request.getInputOctets())
                .outputOctets(request.getOutputOctets())
                .sessionTime(request.getSessionTime())
                .terminateCause(request.getTerminateCause())
                .timestamp(Instant.now())
                .build();
            
            // Publish to Kafka for real-time processing
            kafkaTemplate.send("broadband.accounting", record);
            
            // Also send to inhouse system
            radiusClient.sendAccounting(request);
            
            log.debug("Accounting record processed for session: {}", 
                request.getSessionId());
                
        } catch (Exception e) {
            log.error("Failed to process accounting: {}", e.getMessage());
        }
    }
}
```

### 3.3 Provisioning Adapter

#### DSLAM Provisioning

```java
@Service
@Slf4j
public class InhouseProvisioningAdapter {
    
    private final KafkaTemplate<String, ProvisioningEvent> kafkaTemplate;
    private final NetworkElementAdapter networkAdapter;
    
    public ProvisioningResult provisionAdslPort(AdslProvisioningRequest request) {
        try {
            // 1. Reserve VLAN
            String vlanId = networkAdapter.reserveVlan(
                request.getDslamId(), 
                request.getVlanType()  // INTERNET | VOICE | IPTV
            );
            
            // 2. Configure DSLAM port
            networkAdapter.configureDslamPort(
                DslamPortConfig.builder()
                    .dslamId(request.getDslamId())
                    .portNumber(request.getPortNumber())
                    .vlanId(vlanId)
                    .speedProfile(request.getSpeedProfile())
                    .build()
            );
            
            // 3. Update inhouse system
            webClient.post()
                .uri("/api/line/provision")
                .bodyValue(Map.of(
                    "accountNo", request.getAccountNo(),
                    "dslamId", request.getDslamId(),
                    "portNumber", request.getPortNumber(),
                    "vlanId", vlanId
                ))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            
            kafkaTemplate.send("provisioning.completed",
                ProvisioningEvent.builder()
                    .serviceId(request.getServiceId())
                    .type("ADSL")
                    .status("COMPLETED")
                    .vlanId(vlanId)
                    .timestamp(Instant.now())
                    .build()
            );
            
            return ProvisioningResult.builder()
                .success(true)
                .vlanId(vlanId)
                .build();
                
        } catch (Exception e) {
            log.error("ADSL provisioning failed: {}", e.getMessage());
            return ProvisioningResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ FTTH Provisioning ============
    
    public ProvisioningResult provisionFtthOnt(FttHProvisioningRequest request) {
        try {
            // 1. Register ONT
            OntRegistration ontRegistration = networkAdapter.registerOnt(
                OntConfig.builder()
                    .ontSerial(request.getOntSerialNumber())
                    .oltId(request.getOltId())
                    .ontType(request.getOntType())
                    .build()
            );
            
            // 2. Configure ONT service
            networkAdapter.configureOntService(
                OntServiceConfig.builder()
                    .ontId(ontRegistration.getOntId())
                    .servicePort(request.getServicePort())
                    .vlanId(request.getVlanId())
                    .speedProfile(request.getSpeedProfile())
                    .qosProfile(request.getQosProfile())
                    .build()
            );
            
            // 3. Update inhouse system
            webClient.post()
                .uri("/api/ont/register")
                .bodyValue(Map.of(
                    "accountNo", request.getAccountNo(),
                    "ontSerial", request.getOntSerialNumber(),
                    "oltId", request.getOltId(),
                    "servicePort", request.getServicePort()
                ))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            
            kafkaTemplate.send("provisioning.completed",
                ProvisioningEvent.builder()
                    .serviceId(request.getServiceId())
                    .type("FTTH")
                    .status("COMPLETED")
                    .ontId(ontRegistration.getOntId())
                    .timestamp(Instant.now())
                    .build()
            );
            
            return ProvisioningResult.builder()
                .success(true)
                .ontId(ontRegistration.getOntId())
                .build();
                
        } catch (Exception e) {
            log.error("FTTH provisioning failed: {}", e.getMessage());
            return ProvisioningResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
}
```

---

## 4. Data Synchronization

### 4.1 Usage Data Sync

| Data Type | Frequency | Method | Volume |
|-----------|-----------|--------|--------|
| Session Records | Real-time | RADIUS Accounting | ~200K/day |
| Data Usage | Real-time | RADIUS Accounting | ~200K/day |
| Daily Summary | Daily | File Export | ~400K records |
| Monthly Summary | Monthly | File Export | ~400K records |

### 4.2 Customer Data Sync

| Data Type | Direction | Frequency | Method |
|-----------|-----------|-----------|--------|
| New Customers | Inhouse → Platform | Real-time | Kafka Event |
| Customer Updates | Inhouse → Platform | Real-time | Kafka Event |
| Service Activations | Inhouse → Platform | Real-time | Kafka Event |
| Service Deactivations | Inhouse → Platform | Real-time | Kafka Event |
| Balance Changes | Inhouse → Platform | Real-time | Kafka Event |

### 4.3 Sync Implementation

```java
@Service
@Slf4j
public class InhouseSyncService {
    
    private final KafkaConsumer<String, BroadbandEvent> eventConsumer;
    private final InhouseBroadbandAdapter adapter;
    private final PlatformCustomerRepository customerRepo;
    
    @KafkaListener(topics = "inhouse.customer.event", groupId = "broadband-sync")
    public void handleCustomerEvent(ConsumerRecord<String, BroadbandEvent> record) {
        BroadbandEvent event = record.value();
        
        try {
            switch (event.getEventType()) {
                case "CUSTOMER_CREATED":
                    syncCustomerToPlatform(event);
                    break;
                case "CUSTOMER_UPDATED":
                    updateCustomerInPlatform(event);
                    break;
                case "SERVICE_ACTIVATED":
                    syncServiceActivation(event);
                    break;
                case "SERVICE_DEACTIVATED":
                    syncServiceDeactivation(event);
                    break;
                case "RECHARGE_COMPLETED":
                    syncRecharge(event);
                    break;
            }
        } catch (Exception e) {
            log.error("Failed to process event: {}", event, e);
            // Send to dead letter queue
        }
    }
    
    private void syncCustomerToPlatform(BroadbandEvent event) {
        CustomerData customerData = event.getCustomerData();
        
        PlatformCustomer customer = PlatformCustomer.builder()
            .externalId(customerData.getCustomerId())
            .accountNo(customerData.getAccountNo())
            .customerType(CustomerType.RESIDENTIAL)
            .serviceCategory(ServiceCategory.BROADBAND)
            .nationalId(customerData.getNationalId())
            .name(customerData.getName())
            .email(customerData.getEmail())
            .phone(customerData.getPhone())
            .address(customerData.getAddress())
            .status(CustomerStatus.ACTIVE)
            .build();
        
        customerRepo.save(customer);
        log.info("Synced customer to platform: {}", customer.getExternalId());
    }
}
```

---

## 5. Error Handling

### 5.1 Retry Strategy

| Error Type | Retry Count | Backoff | Fallback |
|------------|-------------|---------|----------|
| API Timeout | 3 | Exponential (2s, 4s, 8s) | Queue for retry |
| API Error (5xx) | 3 | Exponential | Use cached data |
| API Error (4xx) | 0 | N/A | Alert immediately |
| RADIUS Timeout | 2 | Fixed (1s) | Fallback to API |
| Network Error | Circuit Break | N/A | Alert |

### 5.2 Circuit Breaker

```java
@Configuration
public class InhouseCircuitBreakerConfig {
    
    @Bean
    public CircuitBreaker inhouseApiCircuitBreaker() {
        return CircuitBreaker.of("inhouse-broadband-api",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .build()
        );
    }
    
    @Bean
    public CircuitBreaker inhouseRadiusCircuitBreaker() {
        return CircuitBreaker.of("inhouse-broadband-radius",
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
| API Latency (p95) | Gauge | >300ms | Warning |
| API Success Rate | Gauge | <99% | Warning |
| RADIUS Auth Latency | Gauge | >100ms | Warning |
| RADIUS Auth Success | Gauge | <99.5% | Critical |
| Usage Sync Lag | Gauge | >5 min | Warning |
| Failed Operations | Counter | >10/min | Critical |

### 6.2 Health Checks

```
Health Check Endpoints:
- /health/inhouse-broadband/api - API connectivity
- /health/inhouse-broadband/radius - RADIUS server status
- /health/inhouse-broadband/sync - Data sync status
- /health/inhouse-broadband/provisioning - Provisioning queue
```

---

## 7. Configuration

### 7.1 Application Properties

```yaml
inhouse-broadband:
  api:
    base-url: ${INHOUSE_BROADBAND_API_URL:http://broadband.internal:8080}
    key: ${INHOUSE_BROADBAND_API_KEY:}
    timeout: 15000
    retry-attempts: 3
    connection-pool-size: 20
  
  radius:
    auth-port: ${INHOUSE_BROADBAND_RADIUS_AUTH:1812}
    acct-port: ${INHOUSE_BROADBAND_RADIUS_ACCT:1813}
    secret: ${INHOUSE_BROADBAND_RADIUS_SECRET:}
    timeout: 5000
    retry-attempts: 2
  
  provisioning:
    dslam:
      vendor: ${INHOUSE_BROADBAND_DSLAM_VENDOR:huawei}
      config-timeout: 30000
    ftth:
      vendor: ${INHOUSE_BROADBAND_FTTH_VENDOR:huawei}
      config-timeout: 30000
  
  sync:
    enabled: true
    topics:
      customer-event: inhouse.customer.event
      accounting-event: inhouse.accounting.event
  
  security:
    credential-store: hashicorp-vault
    secret-path: secret/inhouse-broadband

kafka:
  topics:
    broadband-usage: broadband.usage
    broadband-accounting: broadband.accounting
    provisioning-task: provisioning.task
```

---

## 8. Migration Strategy

### Phase 1: Connect & Observe (Months 1-3)
- Deploy adapters
- Inhouse system remains primary
- New platform receives data (read-only)
- Monitor data quality

### Phase 2: New Subscribers via Platform (Months 4-6)
- New ADSL/FTTH subscribers created in new platform
- Sync to inhouse for AAA
- Gradually shift provisioning

### Phase 3: RADIUS Migration (Months 7-9)
- Deploy new RADIUS in platform
- Split traffic between old and new
- Migrate to new platform RADIUS

### Phase 4: Billing Migration (Months 10-12)
- Migrate billing to new platform
- Inhouse becomes pure AAA backup

### Phase 5: Retirement (Months 13-18)
- Decommission inhouse billing
- Keep minimal integration for legacy

---

## 9. Testing

### 9.1 Test Scenarios

| Scenario | Type | Expected |
|----------|------|----------|
| Create Customer | Integration | Customer in both systems |
| Activate Service | Integration | DSLAM port configured |
| RADIUS Authentication | Integration | User gets IP, online |
| Recharge | Integration | Balance updated |
| Usage Recording | Integration | Usage in platform |
| Disconnect Service | Integration | Port released |

### 9.2 Load Testing

```
Target: 500 concurrent RADIUS auths
Expected: <50ms p95 latency, 99.9% success rate

Target: 1000 API calls/second
Expected: <100ms p95 latency, 99.9% success rate
```
