# Yemen PTC BSS/OSS Platform

Tier-1 BSS/OSS Platform for Yemen Public Telecommunications Corporation (PTC).

## Architecture

- **12 Business Services** - Customer, Account, Order, Billing, Subscription, Product, Inventory, TroubleTicket, Provisioning, Rating, Notification, RevenueAssurance
- **11 TMF-Compliant REST APIs** - All 10 TMF APIs implemented
- **27 JPA Repositories** - PostgreSQL, Elasticsearch, Neo4j, MongoDB
- **22 Domain Entities** - Full domain model
- **External Adapters** - TITAN, Oracle BRM, In-house systems
- **Kafka Event Consumers** - CDR processing, payment processing
- **Revenue Assurance** - Fraud detection, leakage detection

## TMF API Coverage (10/10)

| TMF API | Endpoint |
|---------|----------|
| TMF620 - Product Catalog | `/tmf-api/productCatalogManagement/v5` |
| TMF622 - Product Ordering | `/tmf-api/productOrderingManagement/v5` |
| TMF632 - Party Management | `/tmf-api/customerManagement/v5` |
| TMF637 - Product Inventory | `/tmf-api/productInventory/v5` |
| TMF641 - Service Ordering | `/tmf-api/serviceOrderingManagement/v4` |
| TMF642 - Trouble Ticket | `/tmf-api/troubleTicketManagement/v5` |
| TMF666 - Billing Account | `/tmf-api/customerBillManagement/v5` |
| TMF638 - Resource Inventory | `/tmf-api/resourceInventoryManagement/v4` |
| TMF688 - Usage Management | `/tmf-api/usageManagement/v5` |
| TMF678 - Notification | `/tmf-api/notificationListener/v5` |

## Quick Start

### Local Development
```bash
# Start infrastructure
docker-compose up -d

# Build and run
cd bss-core
mvn clean compile spring-boot:run

# Run tests
mvn test
```

### Kubernetes Deployment
```bash
kubectl apply -f deployment/kubernetes/bss-core-deployment.yaml
```

## Project Structure
```
bss-core/src/main/java/com/yemenptc/bss/coreservice/
├── service/          # 12 business services
├── controller/       # 11 REST controllers
├── repository/       # 27 JPA repositories
├── entity/           # 22 domain entities
├── adapter/          # External system adapters
├── kafka/            # Event consumers
├── config/           # Application configs
├── exception/        # Exception handling
└── temporal/         # Temporal workflows
```

## Technology Stack

- **Java 21** with Spring Boot 3.2
- **PostgreSQL** for transactional data
- **Redis** for caching
- **Kafka** for event streaming
- **Elasticsearch** for search
- **Temporal.io** for workflows
- **Lombok** for boilerplate reduction

## Build Status

```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
