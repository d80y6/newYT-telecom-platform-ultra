# Phase 1: Foundation Design
**Date**: 2026-03-29
**Status**: Approved
**Scope**: Shared SDK, Event Backbone, Database Schemas, Observability

## 1. Architecture Overview

Hybrid platform with three layers:

- **Java Core (Spring Boot 3.2)**: TMF632 Party, TMF620 Catalog, TMF622 Orders, TMF638/639 Inventory, TMF647/657 Billing. Domain data in PostgreSQL (Citus), MongoDB (catalog), Neo4j (topology).
- **Go Charging Engine**: Kafka consumer for CDRs, rates against product price rules, writes to Cassandra. REST API for balance queries backed by Redis (<5ms p99).
- **TypeScript API Gateway (NestJS)**: Composition layer aggregating Java/Go service calls into unified TMF API responses.

**Communication**: Kafka async (CloudEvents with Avro, Confluent Schema Registry) + REST sync for queries/commands.

## 2. Shared SDK (`tmf-sdk`)

Java module providing base classes for all services:

- `BaseTmfEntity`: Abstract JPA entity with UUID v7, href, timestamps, version
- `TmfCharacteristics`: JSONB column helper for TMF dynamic attributes
- `CloudEventBuilder`: Wraps state changes into CloudEvents v1.0
- `IdempotencyInterceptor`: Redis-backed idempotency key handling (24h TTL)
- `TmfQueryParser`: TMF `?fields=`, `?filter=`, `?sort=` to JPA Criteria

## 3. Event Backbone

Domain topics with 6 partitions, 7-day retention, keyed by entity ID:

| Topic | Key | Producer | Consumers |
|-------|-----|----------|-----------|
| party.events | party_id | TMF632 | TMF629, TMF647, TMF681 |
| catalog.events | offering_id | TMF620 | TMF622, TMF679 |
| order.events | order_id | TMF622 | TMF641, TMF640 |
| service.events | service_id | TMF641 | TMF639, TMF672 |
| resource.events | resource_id | TMF640 | TMF638, TMF672 |
| usage.events | account_id | TMF648 | Charging Engine |
| billing.events | account_id | TMF657 | TMF671, TMF650 |

Schema Registry enforces backward compatibility.

## 4. Database Schemas

### PostgreSQL (Citus) - Party & Billing
Sharded by customer_id. Tables: party, billing_account, privacy_consent. GIN indexes on JSONB characteristics and name (trigram).

### MongoDB - Product Catalog
Flexible schema for product specs with nested characteristics arrays.

### Neo4j - Resource Topology
Graph model: OLT→PON Port→Splitter→ONT→Service, Router→MPLS VPN→Site.

## 5. Observability

- OpenTelemetry auto-instrumentation with custom business spans
- Trace context propagation via Kafka headers
- Grafana dashboards per domain (Party, Catalog, Orders, Charging, Billing, Platform)
- PagerDuty alerts for revenue-impacting issues (charging latency, saga failures)

## 6. Project Structure

```
bss-core/                 # Enhanced Java services
charging-engine/          # Go microservice
api-gateway/              # NestJS composition
shared/kafka/             # Avro schemas
shared/otel/              # Collector config
infrastructure/           # K8s + Terraform
```
