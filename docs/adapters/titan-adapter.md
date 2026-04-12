# TITAN Adapter Specification

## 1. Overview

The TITAN Adapter provides integration between the new BSS/OSS platform and the existing TITAN system for landline voice services.

### 1.1 System Profile

| Attribute | Value |
|-----------|-------|
| **Vendor** | Telco grade (TITAN) |
| **Function** | Landline Voice Billing & Provisioning |
| **Location** | On-premise Data Center |
| **Protocol** | TL1 (Transaction Language 1), SNMP, CDR Files |
| **SLA** | 99.9% availability |

### 1.2 Capabilities

- CDR (Call Detail Record) ingestion and processing
- Service provisioning (new lines, changes, termination)
- Number management (allocation, reservation, portability)
- Real-time status queries
- Alarm monitoring

---

## 2. Integration Architecture

```
┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│    TITAN SYSTEM     │     │   TITAN ADAPTER     │     │   BSS/OSS PLATFORM  │
│                     │     │                     │     │                     │
│  ┌───────────────┐ │     │  ┌───────────────┐  │     │  ┌───────────────┐  │
│  │  CDR Export   │ │────▶│  │  CDR Reader   │  │────▶│  │ Billing       │  │
│  │  (Hourly)     │ │     │  │  Parser       │  │     │  │ Engine        │  │
│  └───────────────┘ │     │  └───────────────┘  │     │  └───────────────┘  │
│                     │     │                     │     │                     │
│  ┌───────────────┐ │     │  ┌───────────────┐  │     │  ┌───────────────┐  │
│  │  TL1 Gateway   │ │◀───▶│  │  TL1 Client   │  │◀───▶│  │ Order          │  │
│  │                │ │     │  │  (Send/Recv)  │  │     │  │ Management     │  │
│  └───────────────┘ │     │  └───────────────┘  │     │  └───────────────┘  │
│                     │     │                     │     │                     │
│  ┌───────────────┐ │     │  ┌───────────────┐  │     │  ┌───────────────┐  │
│  │  SNMP Agent   │ │────▶│  │  SNMP Trap   │  │────▶│  │ Fault          │  │
│  │                │ │     │  │  Receiver     │  │     │  │ Management     │  │
│  └───────────────┘ │     │  └───────────────┘  │     │  └───────────────┘  │
│                     │     │                     │     │                     │
│  ┌───────────────┐ │     │  ┌───────────────┐  │     │  ┌───────────────┐  │
│  │  Number Pool  │ │◀───▶│  │  Number       │  │◀───▶│  │ Resource      │  │
│  │  Management   │ │     │  │  Adapter      │  │     │  │ Inventory     │  │
│  └───────────────┘ │     │  └───────────────┘  │     │  └───────────────┘  │
└─────────────────────┘     └─────────────────────┘     └─────────────────────┘
```

---

## 3. Interface Specifications

### 3.1 CDR Ingestion

#### File Specification

| Parameter | Value |
|-----------|-------|
| **Format** | Fixed-length ASCII |
| **Delivery** | SFTP, hourly |
| **Location** | `/opt/titan/cdr/export/` |
| **Naming** | `CDR_YYYYMMDD_HHMMSS.dat` |
| **Compression** | GZIP |

#### CDR Record Structure

```
Field Position | Field Name              | Length | Data Type | Description
----------------|-------------------------|--------|-----------|----------------------
1-10            | RECORD_ID               | 10     | String    | Unique record ID
11-22           | CALLING_NUMBER          | 12     | String    | Originating number
23-34           | CALLED_NUMBER           | 12     | String    | Terminating number
35-42           | CALL_START_TIME         | 8      | String    | YYYYMMDD
43-48           | CALL_START_TIMESTAMP    | 6      | String    | HHMMSS
49-54           | CALL_DURATION           | 6      | Integer   | Duration in seconds
55-59           | CHARGEABLE_DURATION     | 5      | Integer   | Billable seconds
60-64           | CHARGE_AMOUNT            | 5      | Decimal   | Charge in YER (x100)
65-67           | CALL_TYPE                | 3      | Integer   | 1=Local, 2=National, 3=Intl
68-70           | RATE_ZONE               | 3      | Integer   | Rate zone code
71-73           | DISCOUNT_CODE           | 3      | String    | Discount applied
74-80           | FILLER                  | 7      | String    | Reserved
```

#### CDR Processing Flow

```
TITAN CDR Export
       │
       ▼
┌─────────────────┐
│  SFTP Poller    │ (Every 5 minutes)
│  (Adapter)      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  CDR Validator  │ - Check record format
│                 │ - Validate number ranges
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  CDR Normalizer  │ - Convert to canonical format
│                 │ - Add subscriber ID mapping
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Kafka Producer │ - Publish to cdr-ingested topic
│                 │ - Topic: billing.cdr.titan
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Billing Engine │ - Rate and charge
│                 │
└─────────────────┘
```

### 3.2 TL1 Provisioning Interface

#### Supported Commands

| Command | Description | Direction |
|---------|-------------|-----------|
| `ACT-USER` | Activate user account | Outbound |
| `INH-USER` | Inhibit user account | Outbound |
| `ENT-USER` | Create new subscriber | Outbound |
| `CANC-USER` | Cancel subscriber | Outbound |
| `ENT-SUB` | Create subscription | Outbound |
| `CANC-SUB` | Cancel subscription | Outbound |
| `RTRV-SUB` | Retrieve subscription | Outbound |
| `ENT-NM` | Allocate number | Outbound |
| `RTRV-NM` | Query number status | Outbound |
| `RTRV-ALM` | Retrieve alarms | Inbound |
| `RTRV-STATE` | Retrieve line state | Outbound |

#### TL1 Message Format

**Request:**
```
TID:OSS01;CTAG:001;ACT-USER:::UN=username,PWD=password;
```

**Response:**
```
TID:TITAN01;CTAG:001;;:::;
```

#### Java Implementation

```java
@Service
@Slf4j
public class TitanProvisioningAdapter {
    
    private final TitanTl1Client tl1Client;
    private final TitanCdrReader cdrReader;
    private final KafkaTemplate<String, CdrEvent> kafkaTemplate;
    
    @Value("${titan.tl1.host}")
    private String tl1Host;
    
    @Value("${titan.tl1.port}")
    private int tl1Port;
    
    @Value("${titan.cdr.directory}")
    private String cdrDirectory;
    
    @PostConstruct
    public void initialize() {
        tl1Client = new TitanTl1Client(tl1Host, tl1Port);
        tl1Client.connect();
    }
    
    public ProvisioningResult activateSubscriber(SubscriberRequest request) {
        try {
            // 1. Allocate MSISDN
            String msisdn = numberPoolService.reserveNumber(
                request.getServiceType(), 
                request.getExchange()
            );
            
            // 2. Create subscriber in TITAN
            String tl1Response = tl1Client.sendCommand(
                String.format("ENT-USER:::%s,%s,%s,%s",
                    msisdn,
                    request.getPlanCode(),
                    request.getCustomerId(),
                    request.getFeatures())
            );
            
            // 3. Activate in HLR (via Network Adapter)
            hlrAdapter.activateLine(msisdn, request.getImsi());
            
            // 4. Publish event
            kafkaTemplate.send("order.fulfilled",
                new OrderFulfilledEvent(
                    request.getOrderId(),
                    msisdn,
                    "TITAN_ACTIVATED"
                )
            );
            
            return ProvisioningResult.success(msisdn);
            
        } catch (Exception e) {
            log.error("Failed to activate subscriber: {}", e.getMessage());
            return ProvisioningResult.failure(e.getMessage());
        }
    }
    
    public void processCdrFiles() {
        List<File> newCdrFiles = cdrReader.getNewFiles(cdrDirectory);
        
        for (File cdrFile : newCdrFiles) {
            try {
                List<CdrRecord> records = cdrReader.parse(cdrFile);
                
                for (CdrRecord record : records) {
                    CdrEvent event = CdrEvent.builder()
                        .recordId(record.getRecordId())
                        .callingNumber(record.getCallingNumber())
                        .calledNumber(record.getCalledNumber())
                        .duration(record.getDuration())
                        .charge(record.getCharge())
                        .callType(record.getCallType())
                        .timestamp(record.getCallStartTime())
                        .source("TITAN")
                        .build();
                    
                    kafkaTemplate.send("billing.cdr.titan", event);
                }
                
                cdrReader.markAsProcessed(cdrFile);
                log.info("Processed {} CDR records from {}", records.size(), cdrFile.getName());
                
            } catch (Exception e) {
                log.error("Failed to process CDR file {}: {}", cdrFile.getName(), e.getMessage());
            }
        }
    }
    
    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void scheduledCdrProcessing() {
        processCdrFiles();
    }
}
```

### 3.3 SNMP Alarm Interface

#### Supported Traps

| Trap OID | Description | Severity |
|----------|-------------|----------|
| 1.3.6.1.4.1.9999.1.1 | Link Failure | CRITICAL |
| 1.3.6.1.4.1.9999.1.2 | Equipment Failure | MAJOR |
| 1.3.6.1.4.1.9999.1.3 | Power Alarm | MAJOR |
| 1.3.6.1.4.1.9999.1.4 | Environmental Alarm | MINOR |
| 1.3.6.1.4.1.9999.1.5 | Performance Degradation | WARNING |

#### Alarm Processing

```java
@Service
@Slf4j
public class TitanAlarmAdapter {
    
    private final KafkaTemplate<String, NetworkAlarmEvent> kafkaTemplate;
    
    @SnmpTrapListener(community = "public", version = SnmpVersion.V2C)
    public void onTrap(SnmpTrapEvent event) {
        NetworkAlarmEvent alarm = NetworkAlarmEvent.builder()
            .alarmId(generateAlarmId())
            .sourceSystem("TITAN")
            .networkElement(event.getDeviceId())
            .alarmType(event.getOid())
            .description(event.getDescription())
            .severity(mapSeverity(event.getSeverity()))
            .timestamp(Instant.now())
            .build();
        
        kafkaTemplate.send("network.alarm.detected", alarm);
        log.info("Received alarm from TITAN: {}", alarm.getDescription());
    }
    
    private Severity mapSeverity(int titanSeverity) {
        return switch (titanSeverity) {
            case 1 -> Severity.CRITICAL;
            case 2 -> Severity.MAJOR;
            case 3 -> Severity.MINOR;
            default -> Severity.WARNING;
        };
    }
}
```

### 3.4 Number Management Interface

#### Number Pool Operations

```java
@Service
public class TitanNumberAdapter {
    
    private final TitanTl1Client tl1Client;
    
    public String reserveNumber(String serviceType, String exchange) {
        // Query available numbers from TITAN
        String response = tl1Client.sendCommand(
            String.format("RTRV-NM:::%s,%s", serviceType, exchange)
        );
        
        // Parse response and return available number
        return parseAvailableNumber(response);
    }
    
    public void allocateNumber(String msisdn, String subscriberId) {
        tl1Client.sendCommand(
            String.format("ENT-NM:::%s,%s", msisdn, subscriberId)
        );
    }
    
    public NumberStatus queryNumberStatus(String msisdn) {
        String response = tl1Client.sendCommand(
            String.format("RTRV-NM:::%s", msisdn)
        );
        return parseNumberStatus(response);
    }
    
    public void releaseNumber(String msisdn) {
        tl1Client.sendCommand(
            String.format("CANC-NM:::%s", msisdn)
        );
    }
}
```

---

## 4. Data Synchronization

### 4.1 Subscriber Data Sync

| Data Element | Direction | Frequency | Method |
|--------------|-----------|-----------|--------|
| New Subscribers | TITAN → Platform | Real-time | TL1 Event |
| Subscriber Updates | TITAN → Platform | Real-time | TL1 Event |
| Subscriber Termination | TITAN → Platform | Real-time | TL1 Event |
| Subscription Status | Platform → TITAN | On-demand | TL1 Query |

### 4.2 CDR Reconciliation

| Check | Frequency | Action |
|-------|-----------|--------|
| Volume Check | Hourly | Alert if >10% variance |
| Duplicate Detection | Real-time | Flag duplicates |
| Missing CDR | Daily | Generate gap report |
| Rating Validation | Daily | Spot check rates |

---

## 5. Error Handling

### 5.1 Retry Strategy

| Error Type | Retry Count | Backoff | Circuit Breaker |
|------------|-------------|---------|-----------------|
| Network Timeout | 3 | Exponential (30s) | Open after 5 failures |
| TL1 Parse Error | 0 | N/A | N/A |
| Authentication | 0 | N/A | Alert immediately |
| Resource Busy | 5 | Linear (10s) | Open after 10 failures |

### 5.2 Fallback Mechanism

```java
@Service
public class TitanFallbackAdapter {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public void storeCdrForRetry(CdrRecord record) {
        String key = "titan:cdr:retry:" + record.getRecordId();
        redisTemplate.opsForValue().set(key, toJson(record), Duration.ofHours(24));
        
        // Add to retry queue
        redisTemplate.opsForList().leftPush("titan:cdr:retry:queue", record.getRecordId());
    }
    
    @Scheduled(fixedDelay = 60000) // 1 minute
    public void retryFailedCdr() {
        String recordId = redisTemplate.opsForList().rightPop("titan:cdr:retry:queue");
        if (recordId != null) {
            String json = redisTemplate.opsForValue().get("titan:cdr:retry:" + recordId);
            CdrRecord record = fromJson(json);
            processCdrRecord(record);
        }
    }
}
```

---

## 6. Monitoring & Health

### 6.1 Health Checks

| Check | Endpoint | Threshold |
|-------|----------|-----------|
| TL1 Connection | `/health/tl1` | Connected |
| CDR Directory Access | `/health/cdr` | Writable |
| CDR Processing Lag | `/health/lag` | < 15 minutes |
| Message Queue Depth | `/health/queue` | < 10000 |

### 6.2 Metrics

```java
@Component
public class TitanAdapterMetrics {
    
    private final MeterRegistry registry;
    
    public Counter cdrProcessed = Counter.builder("titan.cdr.processed")
        .description("Number of CDR records processed")
        .register(registry);
    
    public Counter cdrFailed = Counter.builder("titan.cdr.failed")
        .description("Number of CDR records failed")
        .register(registry);
    
    public Timer tl1CommandDuration = Timer.builder("titan.tl1.command.duration")
        .description("TL1 command execution time")
        .register(registry);
    
    public Gauge cdrLag = Gauge.builder("titan.cdr.lag.minutes", this::getCdrLag)
        .description("CDR processing lag in minutes")
        .register(registry);
}
```

---

## 7. Configuration

### 7.1 Application Properties

```yaml
titan:
  tl1:
    host: ${TITAN_TL1_HOST:10.1.1.100}
    port: ${TITAN_TL1_PORT:2362}
    timeout: 30000
    retry-attempts: 3
    connection-pool-size: 10
  
  cdr:
    directory: ${TITAN_CDR_DIR:/opt/titan/cdr/export}
    processed-directory: ${TITAN_CDR_PROCESSED:/opt/titan/cdr/processed}
    poll-interval: 300000  # 5 minutes
    batch-size: 1000
  
  snmp:
    host: ${TITAN_SNMP_HOST:10.1.1.101}
    community: ${TITAN_SNMP_COMMUNITY:public}
    version: v2c
    trap-port: 162
  
  number-pool:
    exchange-areas:
      - Sanaa
      - Aden
      - Taiz
      - Hodeidah
      - Ibb

kafka:
  topics:
    billing-cdr: billing.cdr.titan
    order-fulfillment: order.fulfilled
    network-alarms: network.alarm.detected
```

---

## 8. Testing

### 8.1 Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class TitanAdapterTest {
    
    @Mock
    private TitanTl1Client tl1Client;
    
    @Mock
    private KafkaTemplate<String, CdrEvent> kafkaTemplate;
    
    @InjectMocks
    private TitanProvisioningAdapter adapter;
    
    @Test
    void shouldActivateSubscriber() {
        // Given
        SubscriberRequest request = SubscriberRequest.builder()
            .orderId("ORD-001")
            .customerId("CUST-001")
            .planCode("HAFTARI")
            .serviceType("FIXED")
            .build();
        
        when(tl1Client.sendCommand(anyString()))
            .thenReturn("COMPLD");
        
        // When
        ProvisioningResult result = adapter.activateSubscriber(request);
        
        // Then
        assertTrue(result.isSuccess());
        verify(kafkaTemplate).send(eq("order.fulfilled"), any());
    }
    
    @Test
    void shouldRetryOnTimeout() {
        // Given
        when(tl1Client.sendCommand(anyString()))
            .thenThrow(new TimeoutException())
            .thenReturn("COMPLD");
        
        // When
        ProvisioningResult result = adapter.activateSubscriber(request);
        
        // Then
        assertTrue(result.isSuccess());
        verify(tl1Client, times(2)).sendCommand(anyString());
    }
}
```

### 8.2 Integration Tests

```java
@SpringBootTest
@AutoConfigureTestContainer
class TitanAdapterIntegrationTest {
    
    @Container
    static GenericContainer<?> titanSimulator = new GenericContainer<>("titan-simulator:latest")
        .withExposedPorts(2362, 162);
    
    @Test
    void shouldProcessCdrFile() throws Exception {
        // Upload test CDR file
        uploadTestCdrFile("test-cdr.dat");
        
        // Wait for processing
        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<CdrEvent> events = getCdrEvents();
                assertThat(events).hasSize(100);
            });
    }
}
```

---

## 9. Security

### 9.1 TL1 Authentication

```yaml
titan:
  tl1:
    security:
      authentication:
        type: USER_PASSWORD
        credential-store: vault
        secret-path: secret/titan/credentials
      
      encryption:
        enabled: true
        algorithm: AES-256
```

### 9.2 CDR File Security

- SFTP with key-based authentication
- File integrity check (SHA-256)
- Encryption at rest
- Access logging

---

## 10. Migration Strategy

### Phase 1: Parallel Run
- TITAN continues as primary
- New platform receives CDR data
- Compare billing results

### Phase 2: New Subscriber Migration
- New subscribers in new platform
- TITAN handles existing

### Phase 3: Gradual Migration
- Migrate by exchange area
- Validate each migration batch

### Phase 4: Retirement
- Decommission TITAN after validation
- Archive for regulatory compliance
