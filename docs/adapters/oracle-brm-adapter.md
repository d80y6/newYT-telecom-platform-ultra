# Oracle BRM Adapter Specification

## 1. Overview

The Oracle BRM Adapter provides integration between the new BSS/OSS platform and the existing Oracle Billing and Revenue Management (BRM) system for 4G/LTE FWB mobile services.

### 1.1 System Profile

| Attribute | Value |
|-----------|-------|
| **Vendor** | Oracle |
| **System** | Oracle BRM 12.0 |
| **Function** | 4G/LTE Rating, Charging, Subscriber Management |
| **Location** | On-premise Data Center |
| **Protocol** | Diameter (Gy/Gx), REST API, File Export |
| **SLA** | 99.99% availability |

### 1.2 Current Capabilities

- Real-time rating and charging (OCS)
- Prepaid and postpaid billing
- Policy control integration (PCRF)
- Discount and promotion management
- Invoice generation
- Customer account management

### 1.3 Integration Scope

| Capability | Strategy |
|------------|----------|
| **Rating/Charging** | Keep Oracle BRM as rating engine; wrap with adapters |
| **Subscriber Management** | Migrate to new platform; sync to BRM |
| **Invoice Generation** | Migrate to new platform |
| **Policy Control** | Keep PCRF; integrate via Diameter |
| **Discount Management** | Migrate to new Product Catalog |

---

## 2. Integration Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              ORACLE BRM ARCHITECTURE                                  │
│                                                                                      │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐           │
│  │  OCS        │    │  Billing    │    │  PCM        │    │  Pin Bill   │           │
│  │  (Online    │    │  (Offline   │    │  (Policy    │    │  (Invoice   │           │
│  │  Charging)  │    │  Rating)    │    │  Control)   │    │  Manager)   │           │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘           │
│         │                  │                  │                  │                  │
│         │    ┌─────────────┴──────────────────┴──────────────────┴──────────┐     │
│         │    │                                                              │     │
│         │    │              ORACLE BRM CORE                                 │     │
│         │    │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │     │
│         │    │  │  Account     │  │  Service     │  │  Balance     │         │     │
│         │    │  │  Manager     │  │  Manager     │  │  Manager     │         │     │
│         │    │  └──────────────┘  └──────────────┘  └──────────────┘         │     │
│         │    │                                                              │     │
│         └────┴──────────────────────────────────────────────────────────────┴─────┘
│                                    │                                                   │
└────────────────────────────────────┼───────────────────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              ORACLE BRM ADAPTER LAYER                                 │
│                                                                                      │
│  ┌────────────────────────────────────────────────────────────────────────────────┐  │
│  │                         ORACLE BRM ADAPTER                                    │  │
│  │                                                                                │  │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │  │
│  │  │   REST API      │  │  Diameter       │  │   File         │            │  │
│  │  │   Adapter       │  │  Gateway        │  │   Adapter       │            │  │
│  │  │  (SOAP/REST)    │  │  (Gy/Gx)        │  │  (CDR/EDR)     │            │  │
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘            │  │
│  │           │                    │                    │                      │  │
│  │           │                    │                    │                      │  │
│  │  ┌────────┴────────────────────┴────────────────────┴────────┐            │  │
│  │  │              CANONICAL TRANSFORM LAYER                     │            │  │
│  │  │   • Data Format Normalization                              │            │  │
│  │  │   • Field Mapping                                          │            │  │
│  │  │   • Business Rule Translation                              │            │  │
│  │  └────────────────────────────────────────────────────────────┘            │  │
│  └────────────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              BSS/OSS PLATFORM                                         │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  Customer   │  │  Product    │  │  Billing    │  │  Policy     │                  │
│  │  Management │  │  Catalog    │  │  Engine     │  │  Control    │                  │
│  │             │  │             │  │             │  │  (Unified)  │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  Order      │  │  Revenue    │  │  Usage      │  │  Rating     │                  │
│  │  Management │  │  Assurance  │  │  Analytics  │  │  (New)      │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Interface Specifications

### 3.1 REST API Adapter

#### Supported Operations

| Operation | Endpoint | Description |
|-----------|----------|-------------|
| Create Account | `POST /account` | Create new subscriber account |
| Query Account | `GET /account/{accountNo}` | Get account details |
| Update Account | `PUT /account/{accountNo}` | Update account info |
| Create Service | `POST /service` | Add new service (SIM) |
| Query Balance | `GET /balance/{serviceId}` | Get current balance |
| Recharge | `POST /balance/recharge` | Add credit to account |
| Adjust Balance | `POST /balance/adjust` | Manual balance adjustment |
| Create Product | `POST /product` | Create subscription product |
| Query Product | `GET /product/{productId}` | Get product details |

#### API Client Implementation

```java
@Service
@Slf4j
public class OracleBrmRestAdapter {
    
    private final OracleBrmClient brmClient;
    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${oracle-brm.api.base-url}")
    private String baseUrl;
    
    @Value("${oracle-brm.api.username}")
    private String username;
    
    @Value("${oracle-brm.api.password}")
    private String password;
    
    public AccountResult createAccount(AccountRequest request) {
        try {
            // 1. Map to BRM canonical format
            BrmAccount brmAccount = mapToBrmAccount(request);
            
            // 2. Call BRM REST API
            ResponseEntity<BrmAccountResponse> response = 
                brmClient.createAccount(brmAccount);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                // 3. Publish account created event
                kafkaTemplate.send("customer.account.created",
                    AccountEvent.builder()
                        .accountNo(response.getBody().getAccountNo())
                        .customerId(request.getCustomerId())
                        .source("ORACLE_BRM")
                        .timestamp(Instant.now())
                        .build()
                );
                
                return AccountResult.success(
                    response.getBody().getAccountNo(),
                    mapFromBrmAccount(response.getBody())
                );
            }
            
            return AccountResult.failure("Failed to create account");
            
        } catch (Exception e) {
            log.error("Failed to create account in BRM: {}", e.getMessage());
            return AccountResult.failure(e.getMessage());
        }
    }
    
    public BalanceInfo queryBalance(String serviceId) {
        try {
            BrmBalanceResponse response = brmClient.getBalance(serviceId);
            return mapFromBrmBalance(response);
        } catch (Exception e) {
            log.error("Failed to query balance: {}", e.getMessage());
            throw new BrmIntegrationException("Balance query failed", e);
        }
    }
    
    public RechargeResult recharge(RechargeRequest request) {
        try {
            // 1. Validate recharge amount
            validateRechargeAmount(request.getAmount());
            
            // 2. Call BRM recharge API
            BrmRechargeResponse response = brmClient.recharge(
                BrmRechargeRequest.builder()
                    .accountNo(request.getAccountNo())
                    .serviceId(request.getServiceId())
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .referenceId(generateReferenceId())
                    .build()
            );
            
            // 3. Publish recharge event
            kafkaTemplate.send("billing.recharge.completed",
                RechargeEvent.builder()
                    .accountNo(request.getAccountNo())
                    .serviceId(request.getServiceId())
                    .amount(request.getAmount())
                    .referenceId(request.getReferenceId())
                    .resultCode(response.getResultCode())
                    .timestamp(Instant.now())
                    .build()
            );
            
            return RechargeResult.builder()
                .success(true)
                .balance(response.getNewBalance())
                .referenceId(request.getReferenceId())
                .build();
                
        } catch (Exception e) {
            log.error("Recharge failed: {}", e.getMessage());
            return RechargeResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    private BrmAccount mapToBrmAccount(AccountRequest request) {
        return BrmAccount.builder()
            .accountType(request.isPrepaid() ? "Prepaid" : "Postpaid")
            .billCycle(request.getBillCycle())
            .currency("YER")
            .company(request.getCompany())
            .person(request.getPerson())
            .contacts(mapContacts(request.getContacts()))
            .products(mapProducts(request.getProducts()))
            .paymentMethod(request.getPaymentMethod())
            .build();
    }
}
```

### 3.2 Diameter Gateway (Gy/Gx Interface)

#### Diameter Protocol Support

| Interface | Application | Direction | Purpose |
|-----------|-------------|-----------|---------|
| **Gy** | Credit Control | Outbound | Online charging requests |
| **Gx** | Policy & Charging | Outbound | Policy rules installation |
| **Ro** | Billing | Outbound | Offline charging |
| **RADIUS** | AAA | Outbound | Authentication/Accounting |

#### Diameter Message Flow (Gy - Online Charging)

```
┌─────────────┐    CCR-I    ┌─────────────┐    CCA-I    ┌─────────────┐
│   Mobile   │────────────▶│   ADAPTER   │────────────▶│  Oracle     │
│   Device   │             │   (Gy)      │             │  BRM OCS    │
│            │◀────────────│             │◀────────────│             │
└─────────────┘    CCA-I    └─────────────┘    CCR-I    └─────────────┘
```

#### Diameter Adapter Implementation

```java
@Service
@Slf4j
public class OracleBrmDiameterAdapter {
    
    private final DiameterClient diameterClient;
    private final KafkaTemplate<String, ChargingEvent> kafkaTemplate;
    private final CacheService cacheService;
    
    @Value("${oracle-brm.diameter.gy.realm}")
    private String gyRealm;
    
    @Value("${oracle-brm.diameter.origin-host}")
    private String originHost;
    
    @PostConstruct
    public void initializeDiameter() {
        diameterClient = DiameterClient.builder()
            .originHost(originHost)
            .originRealm(gyRealm)
            .vendorId(10415) // 3GPP
            .applicationId(4) // Gy
            .build();
        
        diameterClient.connect();
    }
    
    public CreditControlAnswer processChargingRequest(CreditControlRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. Build Diameter CCR message
            CreditControlRequest ccRequest = buildCcRequest(request);
            
            // 2. Send to Oracle BRM OCS
            CreditControlAnswer ccAnswer = diameterClient.sendRequest(ccRequest);
            
            // 3. Publish charging event
            publishChargingEvent(request, ccAnswer, "SUCCESS");
            
            // 4. Update local cache
            cacheService.updateQuota(
                request.getSubscriptionId(),
                ccAnswer.getGrantedUnits()
            );
            
            return ccAnswer;
            
        } catch (Exception e) {
            log.error("Charging request failed: {}", e.getMessage());
            
            // 5. Fallback to balance check
            return handleFallback(request, e);
            
        } finally {
            metrics.recordDiameterLatency(
                "Gy", 
                System.currentTimeMillis() - startTime
            );
        }
    }
    
    private CreditControlRequest buildCcRequest(CreditControlRequest req) {
        return CreditControlRequest.builder()
            .sessionId(req.getSessionId())
            .subscriptionId(req.getSubscriptionId())
            .serviceIdentifier(req.getServiceIdentifier())
            .requestedServiceUnit(RequestedServiceUnit.builder()
                .ccTime(req.getRequestedTime())
                .ccInputOctets(req.getRequestedData())
                .ccOutputOctets(req.getRequestedData())
                .build())
            .usedServiceUnit(UsedServiceUnit.builder()
                .ccTime(req.getUsedTime())
                .ccInputOctets(req.getUsedData())
                .ccOutputOctets(req.getUsedData())
                .build())
            .trigger(Trigger.builder()
                .type(TriggerType.SERVICE_SPECIFIC_UNIT)
                .build())
            .build();
    }
    
    private CreditControlAnswer handleFallback(CreditControlRequest request, Exception e) {
        log.warn("Diameter failed, falling back to REST API: {}", e.getMessage());
        
        // Query balance via REST API
        BalanceInfo balance = oracleBrmRestAdapter.queryBalance(
            request.getSubscriptionId()
        );
        
        // Calculate granted units based on balance
        long availableBalance = balance.getMainBalance();
        long ratePerUnit = getRateForService(request.getServiceIdentifier());
        long grantedUnits = Math.min(
            availableBalance / ratePerUnit,
            request.getRequestedServiceUnit()
        );
        
        return CreditControlAnswer.builder()
            .resultCode(DiameterResultCode.OK)
            .grantedServiceUnit(GrantedServiceUnit.builder()
                .ccTime(grantedUnits)
                .build())
            .validityTime(300) // 5 minutes
            .build();
    }
    
    private void publishChargingEvent(
        CreditControlRequest request, 
        CreditControlAnswer answer,
        String status
    ) {
        ChargingEvent event = ChargingEvent.builder()
            .sessionId(request.getSessionId())
            .subscriptionId(request.getSubscriptionId())
            .serviceId(request.getServiceIdentifier())
            .requestedUnits(request.getRequestedServiceUnit())
            .grantedUnits(answer.getGrantedServiceUnit())
            .resultCode(answer.getResultCode())
            .timestamp(Instant.now())
            .sourceSystem("ORACLE_BRM")
            .status(status)
            .build();
        
        kafkaTemplate.send("billing.charging.event", event);
    }
}
```

### 3.3 File Adapter (CDR/EDR Processing)

#### Batch File Processing

| File Type | Format | Frequency | Volume |
|-----------|--------|-----------|--------|
| EDR (Event Detail Record) | XML/CSV | Hourly | ~50M/day |
| CDR (Call Detail Record) | Binary | Hourly | ~10M/day |
| Balance Snapshot | CSV | Daily | ~1M records |
| Invoice Export | XML | Daily | ~400K records |

#### File Processing Flow

```
BRM Export Files
       │
       ▼
┌─────────────────┐
│  SFTP Poller    │ - Monitor directory
│                 │ - Validate file integrity
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  File Parser    │ - Parse EDR/CDR format
│                 │ - Handle large files (streaming)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Normalizer     │ - Map to canonical format
│                 │ - Enrich with subscriber data
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Validator      │ - Validate records
│                 │ - Deduplicate
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Kafka Producer │ - Publish to usage events
│                 │ - Batch or streaming
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Rating Engine  │ - Rate usage events
│                 │ - Calculate charges
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Balance Update│ - Update customer balances
│                 │ - Post to BRM (optional)
└─────────────────┘
```

```java
@Service
@Slf4j
public class OracleBrmFileAdapter {
    
    private final SftpClient sftpClient;
    private final OracleBrmClient brmClient;
    private final KafkaTemplate<String, UsageEvent> kafkaTemplate;
    private final RatingEngineAdapter ratingEngine;
    
    @Value("${oracle-brm.file.directory}")
    private String fileDirectory;
    
    @Value("${oracle-brm.file.processed-dir}")
    private String processedDirectory;
    
    private static final String EDR_TOPIC = "billing.usage.edr";
    private static final String CDR_TOPIC = "billing.usage.cdr";
    
    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void processExportedFiles() {
        List<SftpFile> newFiles = sftpClient.listFiles(
            fileDirectory, 
            "*.xml"
        );
        
        for (SftpFile file : newFiles) {
            try {
                processFile(file);
                sftpClient.move(file, processedDirectory);
                log.info("Processed file: {}", file.getFilename());
            } catch (Exception e) {
                log.error("Failed to process file {}: {}", 
                    file.getFilename(), e.getMessage());
            }
        }
    }
    
    private void processFile(SftpFile file) {
        String fileType = determineFileType(file.getFilename());
        
        try (InputStream inputStream = sftpClient.download(file.getFilename())) {
            
            if ("EDR".equals(fileType)) {
                processEdrFile(inputStream);
            } else if ("CDR".equals(fileType)) {
                processCdrFile(inputStream);
            } else if ("BALANCE".equals(fileType)) {
                processBalanceFile(inputStream);
            } else if ("INVOICE".equals(fileType)) {
                processInvoiceFile(inputStream);
            }
            
        } catch (Exception e) {
            throw new FileProcessingException(
                "Failed to process file: " + file.getFilename(), e
            );
        }
    }
    
    private void processEdrFile(InputStream inputStream) {
        XmlMapper xmlMapper = new XmlMapper();
        StreamingBoweniCallback<EdrRecord> callback = new StreamingBoweniCallback<>(
            record -> {
                // Normalize EDR record
                UsageEvent event = normalizeEdrRecord(record);
                
                // Rate the event
                RatedEvent ratedEvent = ratingEngine.rate(event);
                
                // Publish to Kafka
                kafkaTemplate.send(EDR_TOPIC, ratedEvent);
                
                return true;
            },
            1000 // Process in batches of 1000
        );
        
        xmlMapper.readValues(inputStream, callback);
    }
    
    private UsageEvent normalizeEdrRecord(EdrRecord record) {
        // Map BRM EDR format to canonical format
        return UsageEvent.builder()
            .eventId(record.getEventId())
            .subscriptionId(record.getSubscriberNo())
            .serviceId(record.getServiceId())
            .eventType(mapEventType(record.getEventType()))
            .usageQuantity(record.getUsageValue())
            .usageUnit(mapUsageUnit(record.getUsageUnit()))
            .timestamp(record.getEventTimestamp())
            .location(record.getLocation())
            .ratingGroup(record.getRatingGroup())
            .sourceSystem("ORACLE_BRM")
            .build();
    }
}
```

---

## 4. Data Synchronization

### 4.1 Real-time Sync (Events)

| Event | Direction | Channel | Latency |
|-------|-----------|---------|---------|
| Account Created | BRM → Platform | Kafka | <1s |
| Account Updated | BRM → Platform | Kafka | <1s |
| Balance Changed | BRM → Platform | Kafka | <1s |
| Recharge Completed | BRM → Platform | Kafka | <1s |
| Subscription Changed | BRM → Platform | Kafka | <1s |
| Service Activated | Platform → BRM | REST API | <5s |
| Balance Debited | Platform → BRM | REST API | <5s |

### 4.2 Batch Sync

| Data | Frequency | Method | Records |
|------|-----------|--------|---------|
| Full Account Dump | Daily | File Export | ~1M |
| Balance Snapshot | Hourly | File Export | ~1M |
| Rated Events | Hourly | File Export | ~50M |
| Invoice Data | Daily | File Export | ~400K |

### 4.3 Reconciliation

```java
@Service
@Slf4j
public class OracleBrmReconciliationService {
    
    private final OracleBrmClient brmClient;
    private final AccountRepository platformAccountRepo;
    private final KafkaTemplate<String, ReconciliationEvent> kafkaTemplate;
    
    @Scheduled(cron = "0 0 2 * * *") // 2 AM daily
    public void performDailyReconciliation() {
        log.info("Starting daily reconciliation with Oracle BRM");
        
        // 1. Account count reconciliation
        reconcileAccountCounts();
        
        // 2. Balance reconciliation
        reconcileBalances();
        
        // 3. Service status reconciliation
        reconcileServiceStatus();
        
        log.info("Daily reconciliation completed");
    }
    
    private void reconcileAccountCounts() {
        long platformCount = platformAccountRepo.count();
        long brmCount = brmClient.getAccountCount();
        
        if (platformCount != brmCount) {
            ReconciliationEvent event = ReconciliationEvent.builder()
                .type("ACCOUNT_COUNT")
                .platformCount(platformCount)
                .brmCount(brmCount)
                .variance(platformCount - brmCount)
                .severity(Math.abs(platformCount - brmCount) > 100 
                    ? Severity.HIGH 
                    : Severity.MEDIUM)
                .timestamp(Instant.now())
                .build();
            
            kafkaTemplate.send("reconciliation.discrepancy", event);
            alertTeam(event);
        }
    }
    
    private void reconcileBalances() {
        // Sample 10% of accounts for balance check
        List<String> sampleAccounts = platformAccountRepo
            .findRandomAccounts(10000);
        
        int mismatches = 0;
        for (String accountNo : sampleAccounts) {
            BigDecimal platformBalance = platformAccountRepo
                .getBalance(accountNo);
            BigDecimal brmBalance = brmClient.getBalance(accountNo);
            
            if (platformBalance.compareTo(brmBalance) != 0) {
                mismatches++;
                
                kafkaTemplate.send("reconciliation.balance.mismatch",
                    BalanceMismatchEvent.builder()
                        .accountNo(accountNo)
                        .platformBalance(platformBalance)
                        .brmBalance(brmBalance)
                        .difference(platformBalance.subtract(brmBalance))
                        .timestamp(Instant.now())
                        .build()
                );
            }
        }
        
        log.info("Balance reconciliation: {} mismatches out of {} accounts",
            mismatches, sampleAccounts.size());
    }
}
```

---

## 5. Error Handling

### 5.1 Retry Strategy

| Scenario | Retry Count | Backoff | Alert Threshold |
|----------|-------------|---------|-----------------|
| REST API Timeout | 3 | Exponential (2s, 4s, 8s) | After 3 failures |
| REST API Error (5xx) | 3 | Exponential | After 3 failures |
| REST API Error (4xx) | 0 | None | Immediate |
| Diameter Timeout | 2 | Fixed (1s) | After 2 failures |
| Diameter Unreachable | Circuit Break | N/A | Immediate |
| File Processing Error | 2 | Linear | After 2 failures |

### 5.2 Circuit Breaker

```java
@Service
public class OracleBrmCircuitBreaker {
    
    private CircuitBreaker restCircuitBreaker;
    private CircuitBreaker diameterCircuitBreaker;
    
    @PostConstruct
    public void initializeCircuitBreakers() {
        restCircuitBreaker = CircuitBreaker.of("oracle-brm-rest",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build()
        );
        
        diameterCircuitBreaker = CircuitBreaker.of("oracle-brm-diameter",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .build()
        );
    }
    
    public <T> T executeWithRestCircuitBreaker(
        Supplier<T> operation,
        String operationName
    ) {
        return restCircuitBreaker.executeSupplier(() -> {
            try {
                return operation.get();
            } catch (Exception e) {
                log.error("BRM REST operation failed: {}", operationName, e);
                throw e;
            }
        });
    }
}
```

---

## 6. Monitoring & Metrics

### 6.1 Key Metrics

| Metric | Type | Threshold | Alert |
|--------|------|-----------|-------|
| REST API Latency (p95) | Gauge | >500ms | Warning |
| REST API Latency (p99) | Gauge | >1000ms | Critical |
| Diameter Success Rate | Gauge | <99.5% | Warning |
| Diameter Latency (avg) | Gauge | >100ms | Warning |
| File Processing Lag | Gauge | >1 hour | Warning |
| Failed Operations | Counter | >10/min | Critical |
| Account Sync Lag | Gauge | >5 min | Warning |

### 6.2 Dashboard Panels

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ORACLE BRM ADAPTER DASHBOARD                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────────┐  ┌──────────────────────────┐        │
│  │  REST API Success Rate   │  │  Diameter Success Rate   │        │
│  │  ████████████████████99% │  │  ████████████████████99.5%│        │
│  └──────────────────────────┘  └──────────────────────────┘        │
│                                                                      │
│  ┌──────────────────────────┐  ┌──────────────────────────┐        │
│  │  REST API Latency (ms)    │  │  Diameter Latency (ms)   │        │
│  │  Avg: 45   P95: 120       │  │  Avg: 12   P95: 45        │        │
│  └──────────────────────────┘  └──────────────────────────┘        │
│                                                                      │
│  ┌──────────────────────────┐  ┌──────────────────────────┐        │
│  │  File Processing          │  │  Account Sync           │        │
│  │  Today: 1.2M records     │  │  Lag: 2 min              │        │
│  │  Failed: 0                │  │  Pending: 500            │        │
│  └──────────────────────────┘  └──────────────────────────┘        │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────┐       │
│  │  Operations per Second (last 1 hour)                    │       │
│  │  ▂▃▅▆▇█▇▆▅▄▃▂▃▄▅▆▇█▇▆▅▄▃▂                                 │       │
│  └──────────────────────────────────────────────────────────┘       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 7. Configuration

### 7.1 Application Properties

```yaml
oracle-brm:
  api:
    base-url: ${ORACLE_BRM_API_URL:https://brm.internal:8443}
    username: ${ORACLE_BRM_API_USER:bss_adapter}
    password: ${ORACLE_BRM_API_PASS:}  # From Vault
    connection-pool-size: 20
    timeout: 10000
    retry-attempts: 3
  
  diameter:
    gy:
      host: ${ORACLE_BRM_DIAMETER_GY_HOST:10.1.2.100}
      port: ${ORACLE_BRM_DIAMETER_GY_PORT:3868}
      realm: ${ORACLE_BRM_DIAMETER_REALM:brm.ytel.com.ye}
      application-id: 4  # Gy
    gx:
      host: ${ORACLE_BRM_DIAMETER_GX_HOST:10.1.2.101}
      port: ${ORACLE_BRM_DIAMETER_GX_PORT:3868}
      realm: ${ORACLE_BRM_DIAMETER_REALM:brm.ytel.com.ye}
      application-id: 16777238  # Gx
    origin-host: ${ORACLE_BRM_DIAMETER_ORIGIN:bss-adapter.ytel.com.ye}
    origin-realm: ${ORACLE_BRM_DIAMETER_ORIGIN_REALM:ytel.com.ye}
    connection-pool-size: 50
    timeout: 5000
  
  file:
    directory: ${ORACLE_BRM_FILE_DIR:/opt/brm/export}
    processed-directory: ${ORACLE_BRM_FILE_PROCESSED:/opt/brm/processed}
    archive-directory: ${ORACLE_BRM_FILE_ARCHIVE:/opt/brm/archive}
    poll-interval: 300000
    batch-size: 10000
  
  sync:
    realtime:
      enabled: true
      kafka-topics:
        - customer.account.created
        - billing.recharge.completed
        - billing.usage.event
    batch:
      enabled: true
      schedule: "0 0 2 * * *"  # 2 AM daily
      
  security:
    credential-store: hashicorp-vault
    secret-path: secret/oracle-brm
    tls:
      enabled: true
      key-store: /etc/ssl/adapter.keystore
      key-store-password: ${KEYSTORE_PASS:}
      trust-store: /etc/ssl/ca.truststore

kafka:
  topics:
    billing-usage-edr: billing.usage.edr
    billing-usage-cdr: billing.usage.cdr
    customer-events: customer.account.event
    reconciliation-events: reconciliation.event
```

---

## 8. Migration Strategy

### Phase 1: Parallel Operation (Months 1-3)
- Oracle BRM remains primary
- New platform connects as secondary consumer
- CDR/EDR replicated to new platform
- Compare billing results

### Phase 2: New Subscribers via Platform (Months 4-6)
- New 4G subscribers created in new platform
- Platform syncs to Oracle BRM for rating
- Monitor sync latency

### Phase 3: Gradual Migration (Months 7-12)
- Migrate existing subscribers by segment
- Priority: High-value → Medium → Low
- Validate billing accuracy after each batch

### Phase 4: Hybrid Operation (Months 13-18)
- New platform handles full lifecycle
- Oracle BRM purely for rating
- Phase out remaining BRM dependencies

### Phase 5: Retirement (Months 19-24)
- Migrate rating to new platform
- Decommission Oracle BRM
- Archive data for compliance

---

## 9. Security

### 9.1 API Security

```java
@Configuration
public class OracleBrmSecurityConfig {
    
    @Bean
    public RestTemplate oracleBrmRestTemplate(
        @Value("${oracle-brm.api.username}") String username,
        @Value("${oracle-brm.api.password}") String password
    ) {
        return RestTemplateBuilder.builder()
            .basicAuthentication(username, password)
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
    }
}
```

### 9.2 Diameter Security

- TLS/DTLS encryption for Diameter connections
- Certificate-based mutual authentication
- Message integrity verification

---

## 10. Testing

### 10.1 Test Scenarios

| Scenario | Type | Expected Result |
|----------|------|-----------------|
| Create Account | Integration | Account in BRM + Platform |
| Query Balance | Unit | Correct balance returned |
| Recharge | Integration | Balance updated in both |
| Rate Usage Event | Integration | Correct charges applied |
| Diameter Timeout | Chaos | Fallback to REST |
| Duplicate CDR | Integration | Deduplicated correctly |
| Reconciliation | Integration | All accounts matched |

### 10.2 Load Testing

```
Target: 1000 concurrent operations
- 500 balance queries
- 300 recharges
- 100 account creates
- 100 service activations

Expected Results:
- p95 latency < 500ms
- p99 latency < 1000ms
- Error rate < 0.1%
- Throughput > 500 ops/sec
```
