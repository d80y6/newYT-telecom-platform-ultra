# Adapter Registry Specification

## 1. Overview

The Adapter Registry is the central management component that provides a unified interface for all system integrations. It manages adapter lifecycle, routing, and provides monitoring capabilities.

### 1.1 Core Responsibilities

| Responsibility | Description |
|----------------|-------------|
| **Adapter Registration** | Register and manage all adapters |
| **Request Routing** | Route requests to appropriate adapters |
| **Health Monitoring** | Monitor adapter health and availability |
| **Load Balancing** | Distribute load across adapter instances |
| **Circuit Breaking** | Manage circuit breaker state |
| **Fallback Handling** | Execute fallback strategies |
| **Metrics Collection** | Collect adapter metrics |

### 1.2 Registered Adapters

| Adapter | System | Protocol | Priority |
|---------|--------|----------|----------|
| TITAN Adapter | Landline Voice | TL1, SNMP | Critical |
| Oracle BRM Adapter | 4G/LTE FWB | REST, Diameter | Critical |
| In-house Broadband Adapter | ADSL/FTTH | REST, RADIUS | Critical |
| WHM Adapter | Hosting | REST | Medium |
| MPLS/PRI Adapter | Enterprise | REST, SNMP | Medium |
| Network Elements Adapter | Network | TL1, SNMP, Diameter | Critical |

---

## 2. Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              BSS/OSS PLATFORM                                         │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  Product    │  │  Order      │  │  Billing    │  │  Customer   │                  │
│  │  Catalog    │  │  Management │  │  Engine     │  │  Management │                  │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘                  │
│         │                │                │                │                        │
│         └────────────────┴────────────────┴────────────────┘                        │
│                                    │                                                │
└────────────────────────────────────┼────────────────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              ADAPTER REGISTRY                                        │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           UNIFIED INTERFACE                                      │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   Request   │  │   Response  │  │   Event     │  │   Health    │           │ │
│  │  │   Handler   │  │   Handler   │  │   Handler   │  │   Handler   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           ROUTING ENGINE                                         │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   Service   │  │   Protocol  │  │   Priority  │  │   Fallback  │           │ │
│  │  │   Router    │  │   Router    │  │   Handler   │  │   Handler   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           ADAPTER REGISTRY                                       │ │
│  │                                                                                   │ │
│  │  ┌─────────────────────────────────────────────────────────────────────┐       │ │
│  │  │                         ADAPTER INSTANCES                           │       │ │
│  │  │                                                                      │       │ │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │       │ │
│  │  │  │   TITAN     │  │   Oracle    │  │   Inhouse   │  │   WHM       │ │       │ │
│  │  │  │   Adapter   │  │   BRM       │  │   Broadband │  │   Adapter   │ │       │ │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │       │ │
│  │  │                                                                      │       │ │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │       │ │
│  │  │  │   MPLS/PRI  │  │   Network   │  │   Partner   │                  │       │ │
│  │  │  │   Adapter   │  │   Elements  │  │   Adapter   │                  │       │ │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘                  │       │ │
│  │  │                                                                      │       │ │
│  │  └─────────────────────────────────────────────────────────────────────┘       │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           RESILIENCE LAYER                                       │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   Circuit   │  │   Retry     │  │   Timeout   │  │   Bulkhead  │           │ │
│  │  │   Breaker   │  │   Handler   │  │   Handler   │  │   Handler   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           MONITORING LAYER                                       │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   Health    │  │   Metrics   │  │   Alerting  │  │   Logging   │           │ │
│  │  │   Checker   │  │   Collector │  │   Manager   │  │   Manager   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                      │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Core Components

### 3.1 Adapter Registry Interface

```java
public interface AdapterRegistry {
    
    // ============ Registration ============
    
    /**
     * Register a new adapter
     * @param adapter The adapter to register
     * @param metadata Adapter metadata
     */
    void register(Adapter adapter, AdapterMetadata metadata);
    
    /**
     * Unregister an adapter
     * @param adapterId The adapter ID to unregister
     */
    void unregister(String adapterId);
    
    /**
     * Get adapter by ID
     * @param adapterId The adapter ID
     * @return The adapter instance
     */
    Adapter getAdapter(String adapterId);
    
    /**
     * Get adapter by service type
     * @param serviceType The service type
     * @return The adapter instance
     */
    Adapter getAdapterByServiceType(String serviceType);
    
    /**
     * Get all adapters
     * @return List of all adapters
     */
    List<Adapter> getAllAdapters();
    
    // ============ Health Monitoring ============
    
    /**
     * Check adapter health
     * @param adapterId The adapter ID
     * @return Health status
     */
    HealthStatus checkHealth(String adapterId);
    
    /**
     * Get all adapter health statuses
     * @return Map of adapter ID to health status
     */
    Map<String, HealthStatus> getAllHealthStatuses();
    
    // ============ Metrics ============
    
    /**
     * Get adapter metrics
     * @param adapterId The adapter ID
     * @return Adapter metrics
     */
    AdapterMetrics getMetrics(String adapterId);
    
    /**
     * Get all adapter metrics
     * @return List of adapter metrics
     */
    List<AdapterMetrics> getAllMetrics();
}
```

### 3.2 Adapter Metadata

```java
@Data
@Builder
public class AdapterMetadata {
    
    /**
     * Unique adapter ID
     */
    private String adapterId;
    
    /**
     * Adapter name
     */
    private String name;
    
    /**
     * Adapter description
     */
    private String description;
    
    /**
     * Service type this adapter handles
     */
    private ServiceType serviceType;
    
    /**
     * Protocol type
     */
    private ProtocolType protocolType;
    
    /**
     * Priority (1=highest, 10=lowest)
     */
    private int priority;
    
    /**
     * Supported operations
     */
    private List<Operation> supportedOperations;
    
    /**
     * Health check endpoint
     */
    private String healthCheckEndpoint;
    
    /**
     * Health check interval (seconds)
     */
    private int healthCheckIntervalSeconds;
    
    /**
     * Maximum concurrent connections
     */
    private int maxConnections;
    
    /**
     * Connection timeout (milliseconds)
     */
    private int connectionTimeoutMs;
    
    /**
     * Read timeout (milliseconds)
     */
    private int readTimeoutMs;
    
    /**
     * Enable circuit breaker
     */
    private boolean circuitBreakerEnabled;
    
    /**
     * Circuit breaker configuration
     */
    private CircuitBreakerConfig circuitBreakerConfig;
    
    /**
     * Retry configuration
     */
    private RetryConfig retryConfig;
    
    /**
     * Fallback adapter ID (if primary fails)
     */
    private String fallbackAdapterId;
    
    /**
     * Tags for grouping/filtering
     */
    private Map<String, String> tags;
}
```

### 3.3 Adapter Interface

```java
public interface Adapter {
    
    /**
     * Get adapter ID
     */
    String getAdapterId();
    
    /**
     * Get adapter metadata
     */
    AdapterMetadata getMetadata();
    
    /**
     * Execute an operation
     * @param operation The operation to execute
     * @param request The operation request
     * @return Operation response
     */
    OperationResponse execute(Operation operation, OperationRequest request);
    
    /**
     * Execute operation asynchronously
     * @param operation The operation to execute
     * @param request The operation request
     * @return CompletableFuture for response
     */
    CompletableFuture<OperationResponse> executeAsync(
        Operation operation,
        OperationRequest request
    );
    
    /**
     * Check adapter health
     * @return Health status
     */
    HealthStatus checkHealth();
    
    /**
     * Get adapter metrics
     * @return Adapter metrics
     */
    AdapterMetrics getMetrics();
    
    /**
     * Initialize adapter
     */
    void initialize();
    
    /**
     * Shutdown adapter
     */
    void shutdown();
}
```

---

## 4. Implementation

### 4.1 Adapter Registry Implementation

```java
@Service
@Slf4j
public class AdapterRegistryImpl implements AdapterRegistry {
    
    private final Map<String, Adapter> adapters = new ConcurrentHashMap<>();
    private final Map<String, AdapterMetadata> metadataMap = new ConcurrentHashMap<>();
    private final Map<String, HealthStatus> healthStatusMap = new ConcurrentHashMap<>();
    private final Map<String, AdapterMetrics> metricsMap = new ConcurrentHashMap<>();
    
    private final HealthChecker healthChecker;
    private final MetricsCollector metricsCollector;
    private final AlertService alertService;
    
    @Autowired
    public AdapterRegistryImpl(
        HealthChecker healthChecker,
        MetricsCollector metricsCollector,
        AlertService alertService
    ) {
        this.healthChecker = healthChecker;
        this.metricsCollector = metricsCollector;
        this.alertService = alertService;
    }
    
    @PostConstruct
    public void initialize() {
        log.info("Initializing Adapter Registry");
        startHealthCheckScheduler();
        startMetricsCollector();
    }
    
    // ============ Registration ============
    
    @Override
    public void register(Adapter adapter, AdapterMetadata metadata) {
        String adapterId = metadata.getAdapterId();
        
        if (adapters.containsKey(adapterId)) {
            log.warn("Adapter already registered: {}", adapterId);
            throw new AdapterAlreadyRegisteredException(adapterId);
        }
        
        adapters.put(adapterId, adapter);
        metadataMap.put(adapterId, metadata);
        
        // Initialize adapter
        adapter.initialize();
        
        // Perform initial health check
        HealthStatus health = adapter.checkHealth();
        healthStatusMap.put(adapterId, health);
        
        log.info("Registered adapter: {} ({})", metadata.getName(), adapterId);
    }
    
    @Override
    public void unregister(String adapterId) {
        Adapter adapter = adapters.remove(adapterId);
        metadataMap.remove(adapterId);
        healthStatusMap.remove(adapterId);
        metricsMap.remove(adapterId);
        
        if (adapter != null) {
            adapter.shutdown();
            log.info("Unregistered adapter: {}", adapterId);
        }
    }
    
    @Override
    public Adapter getAdapter(String adapterId) {
        return adapters.get(adapterId);
    }
    
    @Override
    public Adapter getAdapterByServiceType(String serviceType) {
        return adapters.values().stream()
            .filter(a -> a.getMetadata().getServiceType().name().equals(serviceType))
            .findFirst()
            .orElseThrow(() -> new AdapterNotFoundException(
                "No adapter found for service type: " + serviceType));
    }
    
    @Override
    public List<Adapter> getAllAdapters() {
        return new ArrayList<>(adapters.values());
    }
    
    // ============ Health Monitoring ============
    
    @Override
    public HealthStatus checkHealth(String adapterId) {
        Adapter adapter = adapters.get(adapterId);
        if (adapter == null) {
            return HealthStatus.unknown(adapterId);
        }
        
        HealthStatus status = adapter.checkHealth();
        healthStatusMap.put(adapterId, status);
        
        // Alert if unhealthy
        if (status.getStatus() == Status.DOWN) {
            alertService.sendAlert("adapter_unhealthy", 
                String.format("Adapter %s is DOWN: %s", adapterId, status.getMessage()));
        }
        
        return status;
    }
    
    @Override
    public Map<String, HealthStatus> getAllHealthStatuses() {
        return new HashMap<>(healthStatusMap);
    }
    
    // ============ Metrics ============
    
    @Override
    public AdapterMetrics getMetrics(String adapterId) {
        Adapter adapter = adapters.get(adapterId);
        if (adapter == null) {
            return null;
        }
        
        return adapter.getMetrics();
    }
    
    @Override
    public List<AdapterMetrics> getAllMetrics() {
        return adapters.values().stream()
            .map(Adapter::getMetrics)
            .collect(Collectors.toList());
    }
    
    // ============ Scheduled Tasks ============
    
    private void startHealthCheckScheduler() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            for (String adapterId : adapters.keySet()) {
                try {
                    checkHealth(adapterId);
                } catch (Exception e) {
                    log.error("Health check failed for adapter {}: {}", 
                        adapterId, e.getMessage());
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }
    
    private void startMetricsCollector() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            for (Adapter adapter : adapters.values()) {
                try {
                    metricsCollector.collect(adapter.getAdapterId(), adapter.getMetrics());
                } catch (Exception e) {
                    log.error("Metrics collection failed for adapter {}: {}", 
                        adapter.getAdapterId(), e.getMessage());
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
}
```

### 4.2 Request Router Implementation

```java
@Service
@Slf4j
public class RequestRouter {
    
    private final AdapterRegistry adapterRegistry;
    private final ResilienceManager resilienceManager;
    private final MetricsCollector metricsCollector;
    
    public OperationResponse route(OperationRequest request) {
        String adapterId = selectAdapter(request);
        Adapter adapter = adapterRegistry.getAdapter(adapterId);
        
        if (adapter == null) {
            throw new AdapterNotFoundException("No adapter found for request");
        }
        
        // Check health
        HealthStatus health = adapterRegistry.checkHealth(adapterId);
        if (health.getStatus() == Status.DOWN) {
            return handleFallback(adapterId, request);
        }
        
        // Execute with resilience
        return resilienceManager.execute(adapter, request);
    }
    
    private String selectAdapter(OperationRequest request) {
        // Select adapter based on service type
        String serviceType = request.getServiceType();
        Adapter adapter = adapterRegistry.getAdapterByServiceType(serviceType);
        
        if (adapter == null) {
            throw new AdapterNotFoundException(
                "No adapter found for service type: " + serviceType);
        }
        
        return adapter.getAdapterId();
    }
    
    private OperationResponse handleFallback(String primaryAdapterId, OperationRequest request) {
        AdapterMetadata metadata = adapterRegistry.getAdapter(primaryAdapterId).getMetadata();
        String fallbackAdapterId = metadata.getFallbackAdapterId();
        
        if (fallbackAdapterId != null) {
            Adapter fallbackAdapter = adapterRegistry.getAdapter(fallbackAdapterId);
            if (fallbackAdapter != null) {
                log.warn("Primary adapter {} down, using fallback {}", 
                    primaryAdapterId, fallbackAdapterId);
                return resilienceManager.execute(fallbackAdapter, request);
            }
        }
        
        throw new AdapterUnavailableException("No available adapter");
    }
}
```

### 4.3 Resilience Manager Implementation

```java
@Service
@Slf4j
public class ResilienceManager {
    
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    private final Map<String, RetryConfig> retryConfigs = new ConcurrentHashMap<>();
    private final MetricsCollector metricsCollector;
    
    public OperationResponse execute(Adapter adapter, OperationRequest request) {
        String adapterId = adapter.getAdapterId();
        
        // Get or create circuit breaker
        CircuitBreaker circuitBreaker = circuitBreakers.computeIfAbsent(
            adapterId,
            id -> createCircuitBreaker(adapter.getMetadata().getCircuitBreakerConfig())
        );
        
        // Get retry config
        RetryConfig retryConfig = retryConfigs.computeIfAbsent(
            adapterId,
            id -> adapter.getMetadata().getRetryConfig()
        );
        
        // Execute with resilience
        return executeWithResilience(adapter, request, circuitBreaker, retryConfig);
    }
    
    private OperationResponse executeWithResilience(
        Adapter adapter,
        OperationRequest request,
        CircuitBreaker circuitBreaker,
        RetryConfig retryConfig
    ) {
        String adapterId = adapter.getAdapterId();
        long startTime = System.currentTimeMillis();
        
        try {
            // Execute with circuit breaker
            return circuitBreaker.execute(() -> {
                // Execute with retry
                return retryTemplate(retryConfig, () -> 
                    adapter.execute(request.getOperation(), request)
                );
            });
            
        } catch (CircuitBreakerOpenException e) {
            log.warn("Circuit breaker open for adapter: {}", adapterId);
            metricsCollector.recordCircuitBreakerOpen(adapterId);
            throw new AdapterUnavailableException("Circuit breaker open", e);
            
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            metricsCollector.recordFailure(adapterId, latency);
            throw new AdapterExecutionException("Execution failed", e);
            
        } finally {
            long latency = System.currentTimeMillis() - startTime;
            metricsCollector.recordLatency(adapterId, latency);
        }
    }
    
    private <T> T retryTemplate(RetryConfig config, Supplier<T> operation) {
        int attempt = 0;
        Exception lastException = null;
        
        while (attempt < config.getMaxRetries()) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                attempt++;
                
                if (attempt < config.getMaxRetries()) {
                    long waitTime = calculateBackoff(attempt, config);
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(ie);
                    }
                }
            }
        }
        
        throw new RetryExhaustedException("Retry exhausted", lastException);
    }
    
    private long calculateBackoff(int attempt, RetryConfig config) {
        if (config.getBackoffStrategy() == BackoffStrategy.EXPONENTIAL) {
            return (long) (config.getInitialBackoffMs() * Math.pow(2, attempt - 1));
        } else {
            return config.getInitialBackoffMs() * attempt;
        }
    }
    
    private CircuitBreaker createCircuitBreaker(CircuitBreakerConfig config) {
        return CircuitBreaker.of(config.getName(),
            CircuitBreakerConfig.custom()
                .failureRateThreshold(config.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofSeconds(config.getWaitDurationSeconds()))
                .slidingWindowSize(config.getSlidingWindowSize())
                .minimumNumberOfCalls(config.getMinimumNumberOfCalls())
                .build()
        );
    }
}
```

---

## 5. Configuration

### 5.1 Application Properties

```yaml
adapter-registry:
  health-check:
    interval-seconds: 60
    timeout-seconds: 5
  
  metrics:
    collection-interval-seconds: 30
    retention-days: 30
  
  circuit-breaker:
    enabled: true
    default-config:
      failure-rate-threshold: 50
      wait-duration-seconds: 60
      sliding-window-size: 10
      minimum-number-of-calls: 5
  
  retry:
    enabled: true
    default-config:
      max-retries: 3
      backoff-strategy: exponential
      initial-backoff-ms: 1000
  
  adapters:
    titan:
      enabled: true
      priority: 1
      protocol: tl1
      service-type: fixed-line
    
    oracle-brm:
      enabled: true
      priority: 1
      protocol: rest
      service-type: mobile-4g
    
    inhouse-broadband:
      enabled: true
      priority: 1
      protocol: rest
      service-type: broadband
    
    whm:
      enabled: true
      priority: 2
      protocol: rest
      service-type: hosting
    
    mpls-pri:
      enabled: true
      priority: 2
      protocol: rest
      service-type: enterprise
    
    network-elements:
      enabled: true
      priority: 1
      protocol: tl1-snmp-diameter
      service-type: network

kafka:
  topics:
    adapter-health: adapter.health.event
    adapter-metrics: adapter.metrics.event
    adapter-alert: adapter.alert.event
```

---

## 6. Monitoring

### 6.1 Health Check Endpoints

```
GET /api/v1/adapter-registry/health
Response: {
  "status": "UP",
  "adapters": {
    "titan": {"status": "UP", "latencyMs": 45},
    "oracle-brm": {"status": "UP", "latencyMs": 23},
    "inhouse-broadband": {"status": "UP", "latencyMs": 34},
    "whm": {"status": "UP", "latencyMs": 56},
    "mpls-pri": {"status": "UP", "latencyMs": 12},
    "network-elements": {"status": "UP", "latencyMs": 8}
  }
}

GET /api/v1/adapter-registry/metrics
Response: {
  "adapters": {
    "titan": {"requests": 1234, "failures": 2, "latencyMs": {"p50": 23, "p95": 45}},
    "oracle-brm": {"requests": 5678, "failures": 1, "latencyMs": {"p50": 12, "p95": 23}}
  }
}
```

### 6.2 Metrics

| Metric | Type | Description |
|--------|------|-------------|
| adapter_requests_total | Counter | Total requests per adapter |
| adapter_failures_total | Counter | Total failures per adapter |
| adapter_latency_seconds | Histogram | Request latency per adapter |
| adapter_circuit_breaker_state | Gauge | Circuit breaker state (0=closed, 1=open) |
| adapter_health_status | Gauge | Adapter health (0=down, 1=up) |

### 6.3 Alerting

| Alert | Condition | Severity | Action |
|-------|-----------|----------|--------|
| Adapter Down | Health status = DOWN for 5 min | Critical | Page on-call |
| High Failure Rate | Failure rate > 10% for 5 min | Warning | Notify team |
| High Latency | p95 latency > 5s for 5 min | Warning | Notify team |
| Circuit Breaker Open | Circuit breaker open > 1 min | Critical | Page on-call |

---

## 7. Security

### 7.1 Authentication

```java
@Configuration
@EnableWebSecurity
public class AdapterRegistrySecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/adapter-registry/health").permitAll()
                .requestMatchers("/api/v1/adapter-registry/**").authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .build();
    }
}
```

### 7.2 Authorization

```java
@Service
public class AdapterAuthorizationService {
    
    public boolean canAccessAdapter(String userId, String adapterId) {
        // Check user roles
        User user = userService.getUser(userId);
        
        return user.getRoles().stream()
            .anyMatch(role -> role.hasPermission("ADAPTER_" + adapterId.toUpperCase()));
    }
    
    public boolean canExecuteOperation(String userId, String operation) {
        // Check user roles
        User user = userService.getUser(userId);
        
        return user.getRoles().stream()
            .anyMatch(role -> role.hasPermission("OPERATION_" + operation.toUpperCase()));
    }
}
```

---

## 8. Testing

### 8.1 Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class AdapterRegistryTest {
    
    @Mock
    private Adapter adapter;
    
    @Mock
    private AdapterMetadata metadata;
    
    @InjectMocks
    private AdapterRegistryImpl adapterRegistry;
    
    @Test
    void shouldRegisterAdapter() {
        // Given
        when(metadata.getAdapterId()).thenReturn("test-adapter");
        when(adapter.getMetadata()).thenReturn(metadata);
        when(adapter.checkHealth()).thenReturn(HealthStatus.up("test-adapter"));
        
        // When
        adapterRegistry.register(adapter, metadata);
        
        // Then
        assertNotNull(adapterRegistry.getAdapter("test-adapter"));
        verify(adapter).initialize();
    }
    
    @Test
    void shouldUnregisterAdapter() {
        // Given
        when(metadata.getAdapterId()).thenReturn("test-adapter");
        adapterRegistry.register(adapter, metadata);
        
        // When
        adapterRegistry.unregister("test-adapter");
        
        // Then
        assertNull(adapterRegistry.getAdapter("test-adapter"));
        verify(adapter).shutdown();
    }
}
```

### 8.2 Integration Tests

```java
@SpringBootTest
@AutoConfigureTestContainer
class AdapterRegistryIntegrationTest {
    
    @Container
    static GenericContainer<?> kafka = new GenericContainer<>("confluentinc/cp-kafka:latest")
        .withExposedPorts(9092);
    
    @Autowired
    private AdapterRegistry adapterRegistry;
    
    @Test
    void shouldRouteRequestToCorrectAdapter() {
        // Given
        OperationRequest request = OperationRequest.builder()
            .serviceType("FIXED_LINE")
            .operation(Operation.PROVISION_LINE)
            .build();
        
        // When
        OperationResponse response = adapterRegistry.route(request);
        
        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }
}
```

---

## 9. Deployment

### 9.1 Deployment Configuration

```yaml
# docker-compose.yml
version: '3.8'
services:
  adapter-registry:
    image: ytel/adapter-registry:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - ADAPTER_REGISTRY_HEALTH_CHECK_INTERVAL=60
    depends_on:
      - kafka
      - redis
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/v1/adapter-registry/health"]
      interval: 30s
      timeout: 10s
      retries: 3
  
  kafka:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9092:9092"
  
  redis:
    image: redis:latest
    ports:
      - "6379:6379"
```

### 9.2 Kubernetes Deployment

```yaml
# adapter-registry-deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: adapter-registry
spec:
  replicas: 3
  selector:
    matchLabels:
      app: adapter-registry
  template:
    metadata:
      labels:
        app: adapter-registry
    spec:
      containers:
      - name: adapter-registry
        image: ytel/adapter-registry:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: KAFKA_BOOTSTRAP_SERVERS
          value: "kafka:9092"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /api/v1/adapter-registry/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/v1/adapter-registry/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

---

## 10. Migration Strategy

### Phase 1: Foundation (Months 1-3)
- Deploy adapter registry
- Register adapters with metadata
- Implement health checks
- Set up monitoring

### Phase 2: Traffic Migration (Months 4-6)
- Migrate traffic to adapter registry
- Implement circuit breakers
- Add retry mechanisms
- Monitor performance

### Phase 3: Optimization (Months 7-9)
- Optimize routing logic
- Add caching layer
- Implement bulkheading
- Performance tuning

### Phase 4: Full Integration (Months 10-12)
- All services via adapter registry
- Decommission direct integrations
- Optimize and scale
- Documentation
