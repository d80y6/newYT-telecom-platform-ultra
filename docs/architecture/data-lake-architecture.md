# Yemen PTC Data Lake Architecture

## Overview

The Yemen PTC Data Lake implements a modern bronze/silver/gold architecture for analytics and ML/AI workloads.

## Architecture Layers

### Zone 1: Bronze (Raw Data)
- **Purpose**: Immutable raw data ingestion
- **Storage**: S3-compatible object storage
- **Format**: Parquet/ORC with schema-on-read
- **Retention**: 90 days raw, 7 years archive

**Tables:**
- `bronze.customers_raw` - Customer CDC events
- `bronze.orders_raw` - Order events
- `bronze.usage_raw` - CDR/usage records
- `bronze.billing_raw` - Billing transactions
- `bronze.network_raw` - Network element metrics

### Zone 2: Silver (Cleaned/Enriched)
- **Purpose**: Cleansed, validated, business-ready data
- **Storage**: PostgreSQL + Elasticsearch
- **Format**: Delta Lake tables
- **Schema**: Enforced schema with quality gates

**Tables:**
- `silver.customers` - Deduplicated customer profiles
- `silver.orders` - Cleaned order data
- `silver.subscriptions` - Active subscriptions
- `silver.usage_agg` - Aggregated usage by customer/period
- `silver.revenue_agg` - Revenue aggregations

### Zone 3: Gold (Curated/Analytics)
- **Purpose**: Business-level aggregates for BI/ML
- **Storage**: Snowflake-compatible data warehouse
- **Format**: Materialized views

**Tables:**
- `gold.daily_revenue` - Daily revenue by service/region
- `gold.customer360` - Customer 360 view
- `gold.churn_risk` - Churn prediction scores
- `gold.fraud_scores` - Real-time fraud detection
- `gold.mvno_metrics` - MVNO-specific KPIs

## ETL Pipeline Design

### Pipeline 1: Customer Sync
```
Kafka (CDC) → Flink (Transform) → Bronze → Spark (Dedupe) → Silver → Gold (Customer360)
```

### Pipeline 2: Usage Processing
```
Kafka (CDR) → Flink (Rating) → Bronze → Spark (Aggregate) → Silver (usage_agg) → Gold
```

### Pipeline 3: Billing Pipeline
```
Kafka (Payments) → Flink (Validate) → Bronze → Spark (Apply) → Silver (accounts) → Gold (revenue)
```

## Data Quality Framework

### Quality Gates
- **Bronze**: Schema validation, null checks
- **Silver**: Business rules, referential integrity
- **Gold**: Aggregation accuracy, drift detection

### DQ Metrics Tracked
- Completeness (nulls, missing)
- Accuracy (reconciliation)
- Timeliness (pipeline lag)
- Consistency (cross-table)

## Integration Points

### Upstream Sources
- PostgreSQL (via Debezium CDC)
- MongoDB (via Mongo Connector)
- Kafka (event streaming)
- Redis (real-time cache)

### Downstream Consumers
- Grafana dashboards
- Jupyter notebooks
- ML training pipelines
- External reporting APIs

## Technology Stack

| Component | Technology |
|-----------|------------|
| Storage | S3 / MinIO |
| Compute | Spark / Flink |
| Streaming | Kafka |
| Warehouse | Trino / Starburst |
| Orchestration | Airflow |
| DQ | Great Expectations |
| Catalog | Apache Atlas |

## Retention Policies

| Zone | Retention | Archive |
|------|-----------|---------|
| Bronze | 90 days | Glacier |
| Silver | 1 year | Cold storage |
| Gold | 7 years | Archive |
