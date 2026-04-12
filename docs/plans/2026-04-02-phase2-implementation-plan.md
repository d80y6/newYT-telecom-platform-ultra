# Phase 2 Implementation Plan: Missing TMF APIs

**Date**: 2026-04-02  
**Status**: In Progress  
**Scope**: Implement all 12 missing TMF APIs and deploy locally

## Implementation Order

### High Priority (Phase 2)
1. TMF629 - Customer Management
2. TMF640 - Resource Order Management  
3. TMF648 - Usage Management

### Medium Priority (Phase 3)
4. TMF672 - Performance Management
5. TMF673 - SLA Management
6. TMF674 - Notification Management
7. TMF679 - Product Offering Management
8. TMF680 - Product Configuration Management
9. TMF682 - Product Charging

### Low Priority (Phase 4)
10. TMF681 - Agreement Specification Management
11. TMF684 - Agreement Management
12. TMF688 - Campaign Management

---

## TMF629 - Customer Management

### Purpose
Customer 360 view, segments, account hierarchies

### Endpoints
- `GET /customerManagement/v5/customer` - List customers with 360 view
- `GET /customerManagement/v5/customer/{id}` - Get customer 360
- `GET /customerManagement/v5/customer/{id}/segment` - Customer segments
- `POST /customerManagement/v5/customer/{id}/segment` - Assign segment
- `GET /customerManagement/v5/customer/{id}/hierarchy` - Account hierarchy

### Schemas
- Customer360 (extends Customer with enriched data)
- CustomerSegment (segment type, score, attributes)
- AccountHierarchy (tree of related accounts)

---

## TMF640 - Resource Order Management

### Purpose
Resource provisioning orders, equipment deployment

### Endpoints
- `GET /resourceOrderingManagement/v4/resourceOrder` - List resource orders
- `POST /resourceOrderingManagement/v4/resourceOrder` - Create resource order
- `GET /resourceOrderingManagement/v4/resourceOrder/{id}` - Get resource order
- `PATCH /resourceOrderingManagement/v4/resourceOrder/{id}` - Update resource order
- `POST /resourceOrderingManagement/v4/resourceOrder/{id}/stateChange` - State change
- `GET /resourceOrderingManagement/v4/resourceOrder/{id}/appointment` - Get appointment

### Order Types
- ALLOCATION - Allocate resource
- DEALLOCATION - Release resource
- MODIFICATION - Modify resource attributes
- TRANSFER - Transfer between customers

### States
- ACKNOWLEDGED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED

---

## TMF648 - Usage Management

### Purpose
Real-time usage data collection, rating events, usage thresholds

### Endpoints
- `GET /usageManagement/v5/usage` - List usage records
- `POST /usageManagement/v5/usage` - Record usage
- `GET /usageManagement/v5/usage/{id}` - Get usage detail
- `GET /usageManagement/v5/usage/{id}/ratedUsage` - Get rated usage
- `POST /usageManagement/v5/usage/threshold` - Set threshold alert
- `GET /usageManagement/v5/usageAccumulation` - Get accumulated usage

### Usage Types
- DATA, VOICE, SMS, CONTENT, EVENT

---

## TMF672 - Performance Management

### Purpose
Network KPIs, SLA monitoring

### Endpoints
- `GET /performanceManagement/v5/performanceMetric` - List metrics
- `POST /performanceManagement/v5/performanceMetric` - Create metric
- `GET /performanceManagement/v5/performanceMetric/{id}` - Get metric
- `GET /performanceManagement/v5/performanceThreshold` - List thresholds
- `POST /performanceManagement/v5/performanceThreshold` - Set threshold
- `GET /performanceManagement/v5/performanceReport` - Generate report

### Metric Types
- NETWORK_AVAILABILITY, LATENCY, THROUGHPUT, PACKET_LOSS, JITTER

---

## TMF673 - SLA Management

### Purpose
SLA templates, SLA violations

### Endpoints
- `GET /slaManagement/v5/slaTemplate` - List SLA templates
- `POST /slaManagement/v5/slaTemplate` - Create template
- `GET /slaManagement/v5/slaTemplate/{id}` - Get template
- `GET /slaManagement/v5/sla` - List active SLAs
- `POST /slaManagement/v5/sla` - Create SLA
- `GET /slaManagement/v5/sla/{id}/violation` - Get violations
- `POST /slaManagement/v5/sla/{id}/violation` - Report violation

---

## TMF674 - Notification Management

### Purpose
Omni-channel notifications (SMS, Email, Push, IVR)

### Endpoints
- `GET /notificationManagement/v5/notification` - List notifications
- `POST /notificationManagement/v5/notification` - Send notification
- `GET /notificationManagement/v5/notification/{id}` - Get notification status
- `GET /notificationManagement/v5/notificationTemplate` - List templates
- `POST /notificationManagement/v5/notificationTemplate` - Create template
- `GET /notificationManagement/v5/channel` - List channels

### Channels
- SMS, EMAIL, PUSH, IVR, WHATSAPP, TELEGRAM

---

## TMF679 - Product Offering Management

### Purpose
Advanced product configuration

### Endpoints
- `GET /productOfferingManagement/v5/productOffering` - List offerings
- `POST /productOfferingManagement/v5/productOffering` - Create offering
- `GET /productOfferingManagement/v5/productOffering/{id}` - Get offering
- `PUT /productOfferingManagement/v5/productOffering/{id}` - Update offering
- `GET /productOfferingManagement/v5/productOfferingPrice` - List prices
- `POST /productOfferingManagement/v5/productOffering/{id}/bundle` - Add bundle

---

## TMF680 - Product Configuration Management

### Purpose
Product bundling rules

### Endpoints
- `GET /productConfigurationManagement/v5/productConfiguration` - List configs
- `POST /productConfigurationManagement/v5/productConfiguration` - Create config
- `GET /productConfigurationManagement/v5/productConfiguration/{id}` - Get config
- `PUT /productConfigurationManagement/v5/productConfiguration/{id}` - Update
- `GET /productConfigurationManagement/v5/bundlingRule` - List rules
- `POST /productConfigurationManagement/v5/bundlingRule` - Create rule

### Bundle Types
- SIMPLE_BUNDLE, MIXED_BUNDLE, COMPULSORY_BUNDLE

---

## TMF681 - Agreement Specification Management

### Purpose
Contract templates

### Endpoints
- `GET /agreementSpecificationManagement/v5/agreementSpecification` - List specs
- `POST /agreementSpecificationManagement/v5/agreementSpecification` - Create
- `GET /agreementSpecificationManagement/v5/agreementSpecification/{id}` - Get
- `GET /agreementSpecificationManagement/v5/template` - List templates

---

## TMF682 - Product Charging

### Purpose
Advanced charging rules

### Endpoints
- `GET /productCharging/v5/chargingRule` - List rules
- `POST /productCharging/v5/chargingRule` - Create rule
- `GET /productCharging/v5/chargingRule/{id}` - Get rule
- `GET /productCharging/v5/chargingPlan` - List plans
- `POST /productCharging/v5/chargingPlan` - Create plan

### Rule Types
- FLAT_RATE, USAGE_BASED, TIERED, Bundled

---

## TMF684 - Agreement Management

### Purpose
Customer contracts

### Endpoints
- `GET /agreementManagement/v5/agreement` - List agreements
- `POST /agreementManagement/v5/agreement` - Create agreement
- `GET /agreementManagement/v5/agreement/{id}` - Get agreement
- `PATCH /agreementManagement/v5/agreement/{id}` - Update agreement
- `GET /agreementManagement/v5/agreement/{id}/document` - Get documents
- `POST /agreementManagement/v5/agreement/{id}/terminate` - Terminate

### Agreement Types
- CONTRACT, PREPAID, POSTPAID, TENANT

---

## TMF688 - Campaign Management

### Purpose
Marketing campaigns, lead management

### Endpoints
- `GET /campaignManagement/v5/campaign` - List campaigns
- `POST /campaignManagement/v5/campaign` - Create campaign
- `GET /campaignManagement/v5/campaign/{id}` - Get campaign
- `PATCH /campaignManagement/v5/campaign/{id}` - Update campaign
- `GET /campaignManagement/v5/campaign/{id}/offer` - Campaign offers
- `POST /campaignManagement/v5/campaign/{id}/trigger` - Trigger campaign
- `GET /campaignManagement/v5/lead` - List leads
- `POST /campaignManagement/v5/lead` - Create lead

### Campaign Types
- PROMOTIONAL, RETENTION, ACQUISITION, LOYALTY

---

## Implementation Approach

### Java (bss-core)
1. Create entity classes in `entity/` package
2. Create repository interfaces in `repository/` package
3. Create service classes in `service/` package
4. Create controller classes in `controller/` package
5. Add Kafka event producers for state changes

### API Gateway (NestJS)
1. Create proxy modules in `modules/` for each new API
2. Add routes to app.module.ts
3. Update swagger documentation

### Database
- Use existing PostgreSQL tables with new columns where needed
- Create new tables for new entities via Flyway migrations

---

## Deployment

Local deployment via Docker Compose:
```bash
docker-compose up -d
```

Services:
- PostgreSQL (port 5432)
- Redis (port 6379)
- Kafka (port 9092)
- Elasticsearch (port 9200)
- Kong API Gateway (port 8000)
- BSS Core (port 8080)
- API Gateway (port 3000)
- Charging Engine (port 8081)