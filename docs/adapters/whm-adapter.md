# WHM/cPanel Adapter Specification

## 1. Overview

The WHM Adapter provides integration between the new BSS/OSS platform and the existing WHM/cPanel system for hosting services management.

### 1.1 System Profile

| Attribute | Value |
|-----------|-------|
| **Vendor** | cPanel, LLC |
| **System** | Web Host Manager (WHM) / cPanel |
| **Function** | Web Hosting, Domain Management, Email Hosting |
| **Location** | On-premise Data Center |
| **Protocol** | REST API (cPanel/WHM API) |
| **SLA** | 99.9% availability |

### 1.2 Current Capabilities

- **Account Management**
  - Create/terminate hosting accounts
  - Manage cPanel user accounts
  - Bandwidth and disk quota management
- **Domain Management**
  - DNS zone management
  - Domain name registration/transfer
  - SSL certificate management
- **Email Hosting**
  - Email account creation
  - Forwarders and autoresponders
  - Webmail access
- **Database Management**
  - MySQL/MariaDB database creation
  - User privileges management
  - Backup and restore
- **Application Hosting**
  - One-click app installations
  - Web application support

### 1.3 Integration Scope

| Capability | Strategy |
|------------|----------|
| Account Lifecycle | Migrate to new platform; sync to WHM |
| Domain Management | Keep in WHM; integrate via API |
| Email Management | Keep in WHM; integrate via API |
| Backup Management | Keep in WHM; integrate via API |
| Billing Integration | Migrate to new platform |

---

## 2. Integration Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                           WHM/cPanel SYSTEM ARCHITECTURE                              │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                           WHM CONTROL PANEL                                      │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   Web       │  │   cPanel    │  │   Email     │  │   DNS       │           │ │
│  │  │   Interface │  │   Accounts  │  │   Service   │  │   Service   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │ │
│  │  │   Database  │  │   Backup    │  │   Security  │  │   SSL       │           │ │
│  │  │   Service   │  │   Service   │  │   Service   │  │   Service   │           │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘           │ │
│  │                                                                                   │ │
│  │  ┌─────────────────────────────────────────────────────────────────────┐       │ │
│  │  │                     WHM API (REST)                                   │       │ │
│  │  └─────────────────────────────────────────────────────────────────────┘       │ │
│  │                                                                                   │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
│                                               │                                        │
└───────────────────────────────────────────────┼────────────────────────────────────────┘
                                                │
                                                ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              WHM ADAPTER LAYER                                        │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐ │
│  │                              WHM ADAPTER                                         │ │
│  │                                                                                   │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │  │
│  │  │   Account      │  │   Domain        │  │   Email         │            │  │
│  │  │   Adapter      │  │   Adapter       │  │   Adapter       │            │  │
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘            │  │
│  │           │                    │                    │                      │  │
│  │           │                    │                    │                      │  │
│  │  ┌────────┴────────────────────┴────────────────────┴────────┐            │  │
│  │  │              CANONICAL TRANSFORM LAYER                     │            │  │
│  │  │   • Account Model Mapping                                 │            │  │
│  │  │   • Domain Model Mapping                                  │            │  │
│  │  │   • Quota Management                                      │            │  │
│  │  │   • Permission Translation                                │            │  │
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
│  │  Customer   │  │  Product    │  │  Hosting    │  │  Billing    │                  │
│  │  Management │  │  Catalog    │  │  Service    │  │  Engine     │                  │
│  │             │  │             │  │  Management │  │             │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
│                                                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  Domain     │  │  Email      │  │  SSL        │  │  Provisioning│                  │
│  │  Management │  │  Management │  │  Management │  │  (Unified)  │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘                  │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Interface Specifications

### 3.1 WHM API Client

```java
@Service
@Slf4j
public class WhmApiClient {
    
    @Value("${whm.api.host}")
    private String whmHost;
    
    @Value("${whm.api.username}")
    private String whmUsername;
    
    @Value("${whm.api.token}")
    private String apiToken;
    
    private static final int WHM_PORT = 2087;
    private static final String API_BASE = "/json-api";
    
    private final WebClient webClient;
    
    public WhmApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .defaultHeader("Authorization", "WHM " + whmUsername + ":" + apiToken)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    private URI buildUri(String function, Map<String, String> params) {
        try {
            URIBuilder builder = new URIBuilder()
                .setScheme("https")
                .setHost(whmHost)
                .setPort(WHM_PORT)
                .setPath(API_BASE + "/" + function);
            
            if (params != null) {
                params.forEach(builder::addParameter);
            }
            
            return builder.build();
        } catch (Exception e) {
            throw new WhmApiException("Failed to build URI", e);
        }
    }
    
    public <T> T executeGet(String function, Map<String, String> params, Class<T> responseType) {
        try {
            URI uri = buildUri(function, params);
            
            return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType)
                .block();
                
        } catch (Exception e) {
            log.error("WHM API GET failed: {}", e.getMessage());
            throw new WhmApiException("API call failed", e);
        }
    }
    
    public <T> T executePost(String function, Map<String, Object> body, Class<T> responseType) {
        try {
            URI uri = buildUri(function, null);
            
            return webClient.post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
                
        } catch (Exception e) {
            log.error("WHM API POST failed: {}", e.getMessage());
            throw new WhmApiException("API call failed", e);
        }
    }
}
```

### 3.2 Account Management

#### Account Lifecycle Operations

| Operation | WHM API Function | Description |
|-----------|------------------|-------------|
| Create Account | `createacct` | Create hosting account |
| Terminate Account | `removeacct` | Terminate hosting account |
| Suspend Account | `suspendacct` | Suspend hosting account |
| Unsuspend Account | `unsuspendacct` | Unsuspend hosting account |
| Change Password | `passwd` | Change account password |
| Modify Account | `modifyacct` | Modify account settings |
| List Accounts | `listaccts` | List all accounts |

#### Account Operations Implementation

```java
@Service
@Slf4j
public class WhmAccountAdapter {
    
    private final WhmApiClient whmClient;
    private final KafkaTemplate<String, HostingEvent> kafkaTemplate;
    private final WhmDomainAdapter domainAdapter;
    private final WhmEmailAdapter emailAdapter;
    
    // ============ Account Creation ============
    
    public HostingAccountResult createAccount(HostingAccountRequest request) {
        try {
            // 1. Generate cPanel username
            String cpanelUser = generateUsername(request.getDomain());
            
            // 2. Prepare WHM API parameters
            Map<String, String> params = new HashMap<>();
            params.put("username", cpanelUser);
            params.put("domain", request.getDomain());
            params.put("plan", request.getPlan());
            params.put("password", request.getPassword());
            params.put("contactemail", request.getEmail());
            params.put("quota", String.valueOf(request.getQuotaMB()));
            params.put("bwlimit", String.valueOf(request.getBandwidthMB()));
            
            if (request.getReseller()) {
                params.put("reseller", "1");
            }
            
            // 3. Call WHM createacct API
            WhmAccountResponse response = whmClient.executeGet(
                "createacct",
                params,
                WhmAccountResponse.class
            );
            
            // 4. Handle response
            if (response.getStatus() == WhmStatus.SUCCESS) {
                // 5. Create email if requested
                if (request.getCreateEmail()) {
                    emailAdapter.createEmailAccount(
                        cpanelUser,
                        request.getEmailAccount(),
                        request.getEmailPassword()
                    );
                }
                
                // 6. Publish event
                kafkaTemplate.send("hosting.account.created",
                    HostingEvent.builder()
                        .eventType("ACCOUNT_CREATED")
                        .accountId(cpanelUser)
                        .domain(request.getDomain())
                        .plan(request.getPlan())
                        .quota(request.getQuotaMB())
                        .bandwidth(request.getBandwidthMB())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return HostingAccountResult.builder()
                    .success(true)
                    .username(cpanelUser)
                    .domain(request.getDomain())
                    .plan(request.getPlan())
                    .controlPanelUrl("https://" + request.getDomain() + ":2083")
                    .build();
            }
            
            return HostingAccountResult.builder()
                .success(false)
                .errorMessage(response.getReason())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to create hosting account: {}", e.getMessage());
            return HostingAccountResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // ============ Account Termination ============
    
    public HostingTerminationResult terminateAccount(String username) {
        try {
            // 1. Get account info for event
            WhmAccountInfo accountInfo = getAccountInfo(username);
            
            // 2. Terminate account
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            
            WhmResponse response = whmClient.executeGet(
                "removeacct",
                params,
                WhmResponse.class
            );
            
            if (response.getStatus() == WhmStatus.SUCCESS) {
                // 3. Publish event
                kafkaTemplate.send("hosting.account.terminated",
                    HostingEvent.builder()
                        .eventType("ACCOUNT_TERMINATED")
                        .accountId(username)
                        .domain(accountInfo.getDomain())
                        .timestamp(Instant.now())
                        .build()
                );
                
                return HostingTerminationResult.success(username);
            }
            
            return HostingTerminationResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to terminate account {}: {}", username, e.getMessage());
            return HostingTerminationResult.failure(e.getMessage());
        }
    }
    
    // ============ Account Suspension ============
    
    public HostingSuspendResult suspendAccount(String username, String reason) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user", username);
            params.put("reason", reason);
            
            WhmResponse response = whmClient.executeGet(
                "suspendacct",
                params,
                WhmResponse.class
            );
            
            if (response.getStatus() == WhmStatus.SUCCESS) {
                kafkaTemplate.send("hosting.account.suspended",
                    HostingEvent.builder()
                        .eventType("ACCOUNT_SUSPENDED")
                        .accountId(username)
                        .reason(reason)
                        .timestamp(Instant.now())
                        .build()
                );
                
                return HostingSuspendResult.success(username);
            }
            
            return HostingSuspendResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to suspend account {}: {}", username, e.getMessage());
            return HostingSuspendResult.failure(e.getMessage());
        }
    }
    
    // ============ Account Unsuspension ============
    
    public HostingUnsuspendResult unsuspendAccount(String username) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user", username);
            
            WhmResponse response = whmClient.executeGet(
                "unsuspendacct",
                params,
                WhmResponse.class
            );
            
            if (response.getStatus() == WhmStatus.SUCCESS) {
                kafkaTemplate.send("hosting.account.unsuspended",
                    HostingEvent.builder()
                        .eventType("ACCOUNT_UNSUSPENDED")
                        .accountId(username)
                        .timestamp(Instant.now())
                        .build()
                );
                
                return HostingUnsuspendResult.success(username);
            }
            
            return HostingUnsuspendResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to unsuspend account {}: {}", username, e.getMessage());
            return HostingUnsuspendResult.failure(e.getMessage());
        }
    }
    
    // ============ Account Modification ============
    
    public HostingModifyResult modifyAccount(HostingModifyRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user", request.getUsername());
            
            if (request.getNewQuota() != null) {
                params.put("quota", String.valueOf(request.getNewQuota()));
            }
            if (request.getNewBandwidth() != null) {
                params.put("bwlimit", String.valueOf(request.getNewBandwidth()));
            }
            if (request.getNewPlan() != null) {
                params.put("plan", request.getNewPlan());
            }
            if (request.getNewDomain() != null) {
                params.put("newdomain", request.getNewDomain());
            }
            
            WhmResponse response = whmClient.executeGet(
                "modifyacct",
                params,
                WhmResponse.class
            );
            
            if (response.getStatus() == WhmStatus.SUCCESS) {
                kafkaTemplate.send("hosting.account.modified",
                    HostingEvent.builder()
                        .eventType("ACCOUNT_MODIFIED")
                        .accountId(request.getUsername())
                        .changes(mapChanges(request))
                        .timestamp(Instant.now())
                        .build()
                );
                
                return HostingModifyResult.success(request.getUsername());
            }
            
            return HostingModifyResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to modify account: {}", e.getMessage());
            return HostingModifyResult.failure(e.getMessage());
        }
    }
    
    // ============ Account Query ============
    
    public HostingAccountInfo getAccountInfo(String username) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user", username);
            
            WhmAccountInfoResponse response = whmClient.executeGet(
                "accountsummary",
                params,
                WhmAccountInfoResponse.class
            );
            
            return mapToAccountInfo(response);
            
        } catch (Exception e) {
            log.error("Failed to get account info: {}", e.getMessage());
            throw new WhmApiException("Account query failed", e);
        }
    }
    
    public List<HostingAccountInfo> listAccounts() {
        try {
            WhmAccountListResponse response = whmClient.executeGet(
                "listaccts",
                null,
                WhmAccountListResponse.class
            );
            
            return response.getAccounts().stream()
                .map(this::mapToAccountInfo)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Failed to list accounts: {}", e.getMessage());
            throw new WhmApiException("Account list failed", e);
        }
    }
}
```

### 3.3 Domain Management

```java
@Service
@Slf4j
public class WhmDomainAdapter {
    
    private final WhmApiClient whmClient;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    
    // ============ Domain Operations ============
    
    public DomainResult addDomain(String cpanelUser, String domain, boolean isParked) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("cpanel_jsonapi_user", cpanelUser);
            params.put("cpanel_jsonapi_module", "Park");
            params.put("cpanel_jsonapi_func", isParked ? "park" : "addaddondomain");
            params.put("domain", domain);
            
            WhmDomainResponse response = whmClient.executeGet(
                "cpanel",
                params,
                WhmDomainResponse.class
            );
            
            if (response.isSuccess()) {
                kafkaTemplate.send("hosting.domain.added",
                    DomainEvent.builder()
                        .eventType("DOMAIN_ADDED")
                        .domain(domain)
                        .accountUsername(cpanelUser)
                        .domainType(isParked ? "PARKED" : "ADDON")
                        .timestamp(Instant.now())
                        .build()
                );
                
                return DomainResult.success(domain);
            }
            
            return DomainResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to add domain: {}", e.getMessage());
            return DomainResult.failure(e.getMessage());
        }
    }
    
    public DomainResult removeDomain(String cpanelUser, String domain) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("cpanel_jsonapi_user", cpanelUser);
            params.put("cpanel_jsonapi_module", "Park");
            params.put("cpanel_jsonapi_func", "unpark");
            params.put("domain", domain);
            
            WhmDomainResponse response = whmClient.executeGet(
                "cpanel",
                params,
                WhmDomainResponse.class
            );
            
            if (response.isSuccess()) {
                kafkaTemplate.send("hosting.domain.removed",
                    DomainEvent.builder()
                        .eventType("DOMAIN_REMOVED")
                        .domain(domain)
                        .accountUsername(cpanelUser)
                        .timestamp(Instant.now())
                        .build()
                );
                
                return DomainResult.success(domain);
            }
            
            return DomainResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to remove domain: {}", e.getMessage());
            return DomainResult.failure(e.getMessage());
        }
    }
    
    // ============ DNS Management ============
    
    public DnsResult createDnsZone(String domain) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("domain", domain);
            
            WhmDnsResponse response = whmClient.executeGet(
                "adddns",
                params,
                WhmDnsResponse.class
            );
            
            if (response.isSuccess()) {
                return DnsResult.success(domain);
            }
            
            return DnsResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to create DNS zone: {}", e.getMessage());
            return DnsResult.failure(e.getMessage());
        }
    }
    
    public DnsResult addDnsRecord(String domain, DnsRecord record) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("domain", domain);
            params.put("name", record.getName());
            params.put("type", record.getType());
            params.put("address", record.getValue());
            params.put("ttl", String.valueOf(record.getTtl()));
            
            WhmDnsResponse response = whmClient.executeGet(
                "adddns",
                params,
                WhmDnsResponse.class
            );
            
            if (response.isSuccess()) {
                return DnsResult.success(domain);
            }
            
            return DnsResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to add DNS record: {}", e.getMessage());
            return DnsResult.failure(e.getMessage());
        }
    }
}
```

### 3.4 Email Management

```java
@Service
@Slf4j
public class WhmEmailAdapter {
    
    private final WhmApiClient whmClient;
    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;
    
    // ============ Email Account Operations ============
    
    public EmailResult createEmailAccount(
        String cpanelUser,
        String email,
        String password
    ) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("cpanel_jsonapi_user", cpanelUser);
            params.put("cpanel_jsonapi_module", "Email");
            params.put("cpanel_jsonapi_func", "addpop");
            params.put("email", email);
            params.put("password", password);
            params.put("quota", "1024"); // 1GB default
            
            WhmEmailResponse response = whmClient.executeGet(
                "cpanel",
                params,
                WhmEmailResponse.class
            );
            
            if (response.isSuccess()) {
                kafkaTemplate.send("hosting.email.created",
                    EmailEvent.builder()
                        .eventType("EMAIL_CREATED")
                        .email(email)
                        .accountUsername(cpanelUser)
                        .quota(1024)
                        .timestamp(Instant.now())
                        .build()
                );
                
                return EmailResult.success(email);
            }
            
            return EmailResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to create email account: {}", e.getMessage());
            return EmailResult.failure(e.getMessage());
        }
    }
    
    public EmailResult deleteEmailAccount(String cpanelUser, String email) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("cpanel_jsonapi_user", cpanelUser);
            params.put("cpanel_jsonapi_module", "Email");
            params.put("cpanel_jsonapi_func", "delpop");
            params.put("email", email);
            
            WhmEmailResponse response = whmClient.executeGet(
                "cpanel",
                params,
                WhmEmailResponse.class
            );
            
            if (response.isSuccess()) {
                kafkaTemplate.send("hosting.email.deleted",
                    EmailEvent.builder()
                        .eventType("EMAIL_DELETED")
                        .email(email)
                        .accountUsername(cpanelUser)
                        .timestamp(Instant.now())
                        .build()
                );
                
                return EmailResult.success(email);
            }
            
            return EmailResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to delete email account: {}", e.getMessage());
            return EmailResult.failure(e.getMessage());
        }
    }
    
    // ============ Email Forwarder Operations ============
    
    public ForwarderResult createForwarder(String cpanelUser, String from, String to) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("cpanel_jsonapi_user", cpanelUser);
            params.put("cpanel_jsonapi_module", "Email");
            params.put("cpanel_jsonapi_func", "addforward");
            params.put("email", from);
            params.put("fwdopt", "fwd");
            params.put("fwdemail", to);
            
            WhmEmailResponse response = whmClient.executeGet(
                "cpanel",
                params,
                WhmEmailResponse.class
            );
            
            if (response.isSuccess()) {
                return ForwarderResult.success(from, to);
            }
            
            return ForwarderResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to create forwarder: {}", e.getMessage());
            return ForwarderResult.failure(e.getMessage());
        }
    }
    
    // ============ Email Quota Operations ============
    
    public EmailResult changeEmailQuota(String cpanelUser, String email, int quotaMB) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("cpanel_jsonapi_user", cpanelUser);
            params.put("cpanel_jsonapi_module", "Email");
            params.put("cpanel_jsonapi_func", "editquota");
            params.put("email", email);
            params.put("quota", String.valueOf(quotaMB));
            
            WhmEmailResponse response = whmClient.executeGet(
                "cpanel",
                params,
                WhmEmailResponse.class
            );
            
            if (response.isSuccess()) {
                return EmailResult.success(email);
            }
            
            return EmailResult.failure(response.getReason());
            
        } catch (Exception e) {
            log.error("Failed to change email quota: {}", e.getMessage());
            return EmailResult.failure(e.getMessage());
        }
    }
}
```

---

## 4. Data Synchronization

### 4.1 Real-time Sync

| Event | Direction | Channel | Latency |
|-------|-----------|---------|---------|
| Account Created | WHM → Platform | Kafka Event | <1s |
| Account Terminated | WHM → Platform | Kafka Event | <1s |
| Account Suspended | WHM → Platform | Kafka Event | <1s |
| Domain Added | WHM → Platform | Kafka Event | <1s |
| Email Created | WHM → Platform | Kafka Event | <1s |
| Usage Exceeded | WHM → Platform | API Poll | 5 min |

### 4.2 Batch Sync

| Data | Frequency | Method | Records |
|------|-----------|--------|---------|
| Account List | Daily | API Call | ~5K |
| Domain List | Daily | API Call | ~10K |
| Disk Usage | Hourly | API Call | ~5K |
| Bandwidth Usage | Hourly | API Call | ~5K |

### 4.3 Sync Implementation

```java
@Service
@Slf4j
public class WhmSyncService {
    
    private final WhmAccountAdapter accountAdapter;
    private final WhmDomainAdapter domainAdapter;
    private final HostingAccountRepository hostingAccountRepo;
    private final HostingDomainRepository hostingDomainRepo;
    
    @Scheduled(cron = "0 0 3 * * *") // 3 AM daily
    public void performDailySync() {
        log.info("Starting daily WHM sync");
        
        try {
            // 1. Sync accounts
            syncAccounts();
            
            // 2. Sync domains
            syncDomains();
            
            // 3. Validate quotas
            validateQuotas();
            
            log.info("Daily WHM sync completed successfully");
            
        } catch (Exception e) {
            log.error("Daily WHM sync failed: {}", e.getMessage());
            alertTeam("WHM sync failed", e);
        }
    }
    
    private void syncAccounts() {
        log.info("Syncing WHM accounts");
        
        // Get accounts from WHM
        List<HostingAccountInfo> whmAccounts = accountAdapter.listAccounts();
        
        // Get accounts from platform
        List<HostingAccount> platformAccounts = hostingAccountRepo.findAll();
        
        // Map for comparison
        Map<String, HostingAccountInfo> whmMap = whmAccounts.stream()
            .collect(Collectors.toMap(
                HostingAccountInfo::getUsername,
                Function.identity()
            ));
        
        // Sync missing accounts
        for (HostingAccountInfo whmAccount : whmAccounts) {
            if (!platformAccounts.contains(whmAccount.getUsername())) {
                createPlatformAccount(whmAccount);
            }
        }
        
        // Handle deleted accounts
        for (HostingAccount platformAccount : platformAccounts) {
            if (!whmMap.containsKey(platformAccount.getUsername())) {
                handleDeletedAccount(platformAccount);
            }
        }
        
        // Update existing accounts
        for (HostingAccountInfo whmAccount : whmAccounts) {
            updatePlatformAccount(whmAccount);
        }
    }
    
    private void syncDomains() {
        log.info("Syncing WHM domains");
        
        // Similar logic for domains
    }
    
    private void validateQuotas() {
        log.info("Validating quota usage");
        
        // Check for accounts over quota
        List<HostingAccount> accounts = hostingAccountRepo.findOverQuota();
        
        for (HostingAccount account : accounts) {
            // Send notification
            alertService.sendAlert("quota_exceeded", account);
            
            // Optionally suspend account if configured
            if (account.getAutoSuspendOnQuota()) {
                accountAdapter.suspendAccount(
                    account.getUsername(),
                    "Quota exceeded"
                );
            }
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
| Authentication | 0 | N/A | Alert immediately |
| Domain Conflict | 0 | N/A | Generate alternative |

### 5.2 Error Codes Handling

```java
@Service
@Slf4j
public class WhmErrorHandler {
    
    public boolean isRetryable(WhmException e) {
        int errorCode = e.getErrorCode();
        
        return switch (errorCode) {
            case 1000, 1001, 1002 -> true;  // Temporary errors
            case 2000, 2001, 2002 -> false; // Permanent errors
            default -> true;
        };
    }
    
    public String getErrorMessage(WhmException e) {
        int errorCode = e.getErrorCode();
        
        return switch (errorCode) {
            case 1000 -> "Account already exists";
            case 1001 -> "Domain already exists";
            case 1002 -> "Invalid account";
            case 2000 -> "Quota exceeded";
            case 2001 -> "Invalid domain";
            case 2002 -> "Permission denied";
            default -> "Unknown error: " + errorCode;
        };
    }
    
    public void handleError(WhmException e, Operation operation) {
        if (isRetryable(e)) {
            log.warn("Retryable error: {}", e.getMessage());
            // Add to retry queue
            retryQueue.add(operation);
        } else {
            log.error("Permanent error: {}", e.getMessage());
            // Alert and log
            alertService.sendAlert("whm_error", e.getMessage());
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
| Account Sync Lag | Gauge | >5 min | Warning |
| Failed Operations | Counter | >5/min | Critical |
| Disk Usage Variance | Gauge | >10% | Warning |
| Bandwidth Variance | Gauge | >10% | Warning |

### 6.2 Health Checks

```
Health Check Endpoints:
- /health/whm/api - API connectivity
- /health/whm/sync - Data sync status
- /health/whm/accounts - Account operations queue
- /health/whm/domains - Domain operations queue
```

---

## 7. Configuration

### 7.1 Application Properties

```yaml
whm:
  api:
    host: ${WHM_API_HOST:whm.internal}
    port: ${WHM_API_PORT:2087}
    username: ${WHM_API_USER:root}
    token: ${WHM_API_TOKEN:}  # From Vault
    timeout: 30000
    retry-attempts: 3
  
  sync:
    enabled: true
    schedule: "0 0 3 * * *"  # 3 AM daily
    batch-size: 100
  
  defaults:
    plan: basic
    quota-mb: 10240  # 10GB
    bandwidth-mb: 102400  # 100GB
    auto-suspend-quota: true
  
  security:
    credential-store: hashicorp-vault
    secret-path: secret/whm

kafka:
  topics:
    hosting-account: hosting.account.event
    hosting-domain: hosting.domain.event
    hosting-email: hosting.email.event
```

---

## 8. Migration Strategy

### Phase 1: Parallel Operation (Months 1-3)
- WHM remains primary
- New platform monitors and syncs data
- Build integration tests

### Phase 2: New Accounts via Platform (Months 4-6)
- New hosting accounts created in new platform
- Sync to WHM for provisioning
- Monitor sync success rate

### Phase 3: Domain Management (Months 7-9)
- Migrate DNS to new platform
- Keep WHM for email management
- Consolidate domain data

### Phase 4: Full Migration (Months 10-12)
- All operations via new platform
- WHM becomes provisioning backend only
- Plan for eventual decommission

---

## 9. Testing

### 9.1 Test Scenarios

| Scenario | Type | Expected |
|----------|------|----------|
| Create Account | Integration | Account created in WHM |
| Terminate Account | Integration | Account terminated in WHM |
| Suspend Account | Integration | Account suspended in WHM |
| Add Domain | Integration | Domain added to account |
| Create Email | Integration | Email account created |
| Quota Exceed | Integration | Alert triggered |

### 9.2 Load Testing

```
Target: 100 concurrent API calls
Expected: <500ms p95 latency, 99% success rate

Target: 1000 accounts to sync
Expected: <5 minutes sync time, 100% accuracy
```
