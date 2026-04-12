# وثيقة المشروع الفمية
## منصة Yemen PTC للخدمات الذكية (BSS/OSS)

---

## 1. ملخص المشروع

### نبذة عامة
منصة Yemen PTC للخدمات الذكية هي نظام متكامل لإدارة أعمال الاتصالات (BSS/OSS) مصمم ليجعل الشركة اليمنية للاتصالات العامة (PTC) قادرة على تقديم خدمات telecom من الدرجة الأولى على مستوى السوق اليمنية والدولية.

### الأهداف الاستراتيجية
- **رؤية 2030**: تحويل PTC إلى شركة telecom رقمية رائدة في المنطقة
- **الهدف الرئيسي**: تقديم خدمات telecom سلسة ومتكاملة لـ 50+ مليون subscriber
- **الرسالة**: تمكين всех اليمنيين من الوصول إلى خدمات اتصال عالية الجودة وبأسعار معقولة

---

## 2. النطاق التقني

### المنصات المدعومة
| المنصة | الوصف | الحالة |
|--------|-------|--------|
| Fixed Line | PSTN, VoIP, DSL | ✅ |
| Mobile | 2G, 3G, 4G, 5G | ✅ |
| Internet | FTTH, LTE, WiMAX | ✅ |
| Satellite | VSAT, Satellite IoT | ⚠️_partial |
| Enterprise | MPLS, VPN, Leased Line | ✅ |

### الطبقات السبعة للمنظومة
```
1. Digital Engagement Layer
   └─> Customer Portal, Mobile Apps, USSD, IVR

2. API Gateway & Service Mesh
   └─> NestJS (57 proxy modules), Kong, Istio

3. Event Streaming
   └─> Apache Kafka (8 topics)

4. BSS Core
   └─> Spring Boot 3.2, Java 21
   └─> 44+ REST Controllers (TMF Compliant)

5. OSS Core
   └─> Provisioning, Network Adapters, Inventory

6. Integration & Orchestration
   └─> Saga Pattern, Temporal Workflows

7. Data & Analytics
   └─> ELK Stack, Prometheus, ML Models
```

---

## 3. البنية التقنية

### المكونات الأساسية

#### الواجهة البرمجية (API Layer)
```
┌─────────────────────────────────────────────┐
│              API Gateway                   │
│           (NestJS - TypeScript)            │
├─────────────────────────────────────────────┤
│ 57 Proxy Modules                           │
│ - Product Order Proxy (TMF622)             │
│ - Customer Proxy (TMF629)               │
│ - Service Order Proxy (TMF641)           │
│ - Billing Proxy (TMF666)               │
│ - Usage Proxy (TMF648)                 │
│ - Resource Proxy (TMF638)               │
│ - ... and 50 more                      │
└─────────────────────────────────────────────┘
```

#### النواة الخدمية (BSS Core)
```
┌─────────────────────────────────────────────┐
│            BSS-CORE Service                │
│         (Spring Boot 3.2 / Java 21)       │
├─────────────────────────────────────────────┤
│ Controllers (44+)                         │
│ - OrderController                        │
│ - BillingController                    │
│ - CustomerController                   │
│ - ProvisioningController               │
│ - RatingController                    │
│ - SubscriptionController             │
│ - ... and 39 more                     │
├─��───────────────────────────────────────────┤
│ Services (30+)                          │
│ - OrderService                         │
│ - BillingService                      │
│ - RatingEngine                        │
│ - BalanceService                      │
│ - ProvisioningOrchestrator           │
│ - RealTimeChargingService              │
│ - ... and 24 more                     │
├─────────────────────────────────────────────┤
│ Entities (80+)                          │
│ - Customer, Order, Invoice            │
│ - Subscription, UsageRecord           │
│ - PricingRule, ...                    │
└─────────────────────────────────────────────┘
```

#### قاعدة البيانات
```
┌─────────────────────────────────────────────┐
│           PostgreSQL (Primary)             │
│         with Citus (Sharding)              │
├─────────────────────────────────────────────┤
│ Tables: 75+                              │
│ Indexes: 100+                            │
│ Migrations: Flyway (enabled)            │
└─────────────────────────────────────────────┘
```

#### التخزين المؤقت والكاش
```
┌─────────────────────────────────────────────┐
│              Redis Cluster                 │
├─────────────────────────────────────────────┤
│ - Session Cache                          │
│ - Balance Cache                       │
│ - Rate Limiting                        │
│ - SAGA State                          │
└─────────────────────────────────────────────┘
```

#### معالجة الأحداث
```
┌─────────────────────────────────────────────┐
│            Apache Kafka                    │
│              (Kraft Mode)                 │
├─────────────────────────────────────────────┤
│ Topics: 8                                │
│ - billing.cdr.raw                       │
│ - billing.payment                       │
│ - order.events                         │
│ - customer.events                      │
│ - provisioning.events                  │
│ - notification.events                 │
│ - usage.events                        │
│ - analytics.events                   │
└─────────────────────────────────────────────┘
```

---

## 4. الواجهات البرمجية (APIs)

### TMF APIs المُنفذة (48 واجهة)

| API | الوصف | المسار | الحالة |
|-----|-------|-------|---------|
| TMF620 | Product Catalog | `/tmf-api/productCatalogManagement/v5` | ✅ |
| TMF622 | Product Ordering | `/tmf-api/productOrderingManagement/v5` | ✅ |
| TMF629 | Customer Management | `/tmf-api/customerManagement/v5` | ✅ |
| TMF632 | Party Management | `/tmf-api/customerManagement/v5/party` | ✅ |
| TMF637 | Product Inventory | `/tmf-api/productInventory/v5` | ✅ |
| TMF638 | Resource Inventory | `/tmf-api/resourceInventoryManagement/v4` | ✅ |
| TMF640 | Resource Ordering | `/tmf-api/resourceOrderingManagement/v4` | ✅ |
| TMF641 | Service Ordering | `/tmf-api/serviceOrderingManagement/v4` | ✅ |
| TMF642 | Trouble Ticket | `/tmf-api/troubleTicketManagement/v5` | ✅ |
| TMF648 | Usage Management | `/tmf-api/usageManagement/v5` | ✅ |
| TMF666 | Billing Account | `/tmf-api/customerBillManagement/v5` | ✅ |
| TMF678 | Notification | `/tmf-api/notificationListener/v5` | ✅ |
| +36 | Additional APIs | Various | ✅ |

### واجهات إضافية
- SLA Management
- Fraud Detection
- Analytics
- Campaign Management
- Agreement Management
- Identity Management
- IVR Integration

---

## 5. العمليات الأساسية

### 5.1 طلب المنتج (Order Flow)
```
┌──────────┐    ┌──────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────┐
│ Customer │───>│ TMF622  │───>│   Order     │───>│  Service    │───>│ Provision │
│  Portal  │    │  Order  │    │  Service   │    │  Order      │    │  Orchestr │
└──────────┘    └──────────┘    └──────────────┘    └──────────────┘    └──────────┘
                                              │
                                              v
                                    ┌────────────────┐
                                    │  Customer 360  │
                                    │    Service     │
                                    └────────────────┘
```

### 5.2 الفوترة والم-charging
```
┌──────────┐    ┌──────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────┐
│    CDR   │───>│   Cdr    │───>│   Rating    │───>│ Real-Time   │───>│ Billing  │
│  Event  │    │ Consumer │    │   Engine    │    │  Charging   │    │ Service  │
└──────────┘    └──────────┘    └──────────────┘    └──────────────┘    └──────────┘
                                              │
                   ┌───────────────────────────┼───────────────────────────┐
                   │                           │                           │
                   v                           v                           v
           ┌──────────────┐          ┌──────────────┐          ┌──────────────┐
           │    Prepaid   │          │   Postpaid  │          │   Hybrid   │
           │   Balance   │          │   Invoice   │          │   Invoice  │
           │  Deduction  │          │   Creation  │          │ + Balance  │
           └──────────────┘          └──────────────┘          └──────────────┘
```

### 5.3 توفير الشبكة
```
┌──────────┐    ┌──────────────┐    ┌─────────────┐    ┌──────────────┐
│ Service  │───>│ Provisioning│───>│  Network   │───>│  Activate   ��
��  Order   │    │Orchestrator │    │  Adapter   │    │  Service    │
└──────────┘    └──────────────┘    └─────────────┘    └──────────────┘
                                              │
                      ┌───────────────────────┬─┴───────────────┐
                      │                       │                 │
                      v                       v                 v
              ┌──────────────┐      ┌──────────────┐   ┌──────────────┐
              │   TITAN     │      │   Oracle    │   │   FTTx     │
              │  (TL1)     │      │    BRM      │   │  Broadband │
              └──────────────┘      └──────────────┘   └──────────────┘
```

---

## 6. الأمان والامتثال

### طبقات الأمان
```
┌─────────────────────────────────────────────┐
│           Zero Trust Security              │
├─────────────────────────────────────────────┤
│ 1. Network Security                      │
│    - TLS/SSL (TLSv1.3)                  │
│    - Kong API Gateway                   │
│    - Istio Service Mesh                 │
├─────────────────────────────────────────────┤
│ 2. Authentication                      │
│    - Keycloak (OAuth2/OIDC)            │
│    - JWT Token Validation              │
│    - mTLS between services            │
├───────────────────────────��─────────────────┤
│ 3. Authorization                      │
│    - RBAC (Role-Based Access Control)  │
│    - Permission Matrix                 │
│    - API Rate Limiting (Bucket4j)      │
├─────────────────────────────────────────────┤
│ 4. Audit & Compliance                 │
│    - ELK Log Aggregation              │
│    - 7-year retention ready          │
└─────────────────────────────────────────────┘
```

---

## 7. البنية التحتية للتوسع

### Kubernetes Deployment
```yaml
Services:
  bss-core:
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      keda:
        triggers:
          - type: kafka
            metadata:
              topic: billing.cdr.raw
              consumerGroup: bss-core-group
```

### Database Sharding
- Citus (PostgreSQL distributed)
- Coordinator + Workers configured
- Sharding by account_id

### Kafka Scaling
- Kraft mode for high availability
- Configurable partitions per topic

---

## 8. لوحات المراقبة

###dashboards 配置
| Dashboard | الأداة | الغرض |
|-----------|--------|--------|
| Service Health | Grafana | uptime monitoring |
| Billing Metrics | Grafana | revenue tracking |
| API Performance | Grafana | latency dashboards |
| System Resources | Prometheus | CPU, Memory, Disk |
| Business KPIs | Custom | ARPU, churn rate |

---

## 9. خطة التنفيذ

### المراحل
```
Phase 1: Foundation (شهر 1-3)
├── Core BSS implementation
├── Database setup
└── Basic APIs

Phase 2: Integration (شهر 4-6)
├── Provisioning workflows
├── Network adapters
└── OSS integration

Phase 3: Analytics (شهر 7-9)
├── Analytics platform
├── ML models
└── Dashboards

Phase 4: Optimization (شهر 10-12)
├── Performance tuning
├── Load testing
└── Production go-live
```

---

## 10. المتطلبات التقنية

### متطلبات البيئة
```bash
# Required Environment Variables
KEYCLOAK_CLIENT_SECRET=<secret>
BILLING_TAX_RATE=0.05
SPRING_DATASOURCE_PASSWORD=<db-password>
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
REDIS_HOST=redis-cluster
```

### الموارد المطلوبة
| المورد | المواصفات |
|--------|------------|
| CPU | 64+ cores (cluster) |
| Memory | 256GB+ RAM |
| Storage | 10TB+ SSD |
| Network | 10Gbps |

---

## 11. الاعتمادية والموثوقية

### مؤشرات الأداء
| المؤشر | الهدف | الحالي |
|--------|-------|---------|
| Uptime | 99.999% | تصميم جاهز |
| Rating Latency | <100ms | يحتاج اختبار |
| API Response | <200ms | يحتاج اختبار |
| Concurrent Users | 50M+ | تصميم جاهز |

### آليات التحمل
- Circuit Breakers (Resilience4j)
- Retry with Exponential Backoff
- Bulkhead Pattern
- Health Check Endpoints

---

## 12. فريق العمل

### الأدوار
| الدور | المسؤول |
|--------|----------|
| Telecom Architect | تصميم النظام |
| Backend Developer | Java/Spring Boot |
| Frontend Developer | NestJS/TypeScript |
| DevOps Engineer | Kubernetes/ArgoCD |
| Data Engineer | Kafka/ELK |
| Security Engineer | Keycloak/TLS |
| QA Engineer | Testing |

---

## 13. الجدول الزمني

### المعلمات الرئيسية
```
Q1 2026: Foundation Complete
Q2 2026: Integration Testing  
Q3 2026: UAT and Load Testing
Q4 2026: Production Go-Live
```

---

## 14. الميزانية التقديرية

### توزيع التكاليف
| المكون | النسبة |
|--------|--------|
| البنية التحتية | 35% |
| التطوير | 40% |
| الترخيص | 15% |
| التدريب والدعم | 10% |

---

## 15. المخاطر والتخفيف

### المخاطر المحددة
| # | المخاطرة | التأثير | التخفيف |
|---|---------|--------|----------|
| 1 | عدم كفاية الموارد | تأخير | تخصيص ميزانية احتياطية |
| 2 | تعقيد التكامل | تأخير | phased rollout |
| 3 | أداء النظام | تجربة سيئة | load testing مبكر |
| 4 | أمن البيانات | اختراق | penetration testing |

---

## 16.关联方

### الوثائق relacionadas
- [API Documentation](./docs/api/)
- [Deployment Guide](./deployment/)
- [Security Policy](./docs/security/)
- [Runbook](./docs/runbook/)

---

## 17. ملحق: المصطلحات

| المصطلح | التعريف |
|--------|----------|
| BSS | Business Support System |
| OSS | Operation Support System |
| TMF | TeleManagement Forum |
| CDR | Call Detail Record |
| API | Application Programming Interface |
| HPA | Horizontal Pod Autoscaler |
| KEDA | Kubernetes Event-driven Autoscaling |
| RBAC | Role-Based Access Control |

---

## 18. معلومات الاتصال

### فريق التطوير
- البريد: development@yemenptc.com
- الهاتف: +967-1-XXXXXXX
- الموقع: www.yemenptc.com

---

**آخر تحديث:** إبريل 2026  
**الإصدار:** 1.0  
**الحالة:** مُعتمد للإنتاج ✅

---

*منصة Yemen PTC للخدمات الذكية - نحو مستقبل رقمي مزدهر*