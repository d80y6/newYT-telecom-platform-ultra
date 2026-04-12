# BSS/OSS Platform Operations Runbook

## Overview
This runbook provides operational procedures for the Yemen PTC BSS/OSS Platform.

---

## 1. Service Management

### Start All Services
```bash
cd /opt/bss-oss/deployment
docker-compose up -d
```

### Stop All Services
```bash
docker-compose down
```

### Restart Specific Service
```bash
docker-compose restart bss-core
```

### Check Service Status
```bash
docker-compose ps
kubectl get pods -n bss-oss
```

---

## 2. Database Operations

### Connect to PostgreSQL
```bash
psql -h localhost -U bss_admin -d bss_oss
```

### Run Migrations
```bash
mvn flyway:migrate
```

### Backup Database
```bash
pg_dump -h localhost -U bss_admin bss_oss > backup_$(date +%Y%m%d).sql
```

### Restore Database
```bash
psql -h localhost -U bss_admin bss_oss < backup_20240101.sql
```

---

## 3. Kafka Operations

### List Topics
```bash
kafka-topics --bootstrap-server localhost:9092 --list
```

### Create Topic
```bash
kafka-topics --bootstrap-server localhost:9092 --create \
  --topic new-topic --partitions 6 --replication-factor 1
```

### Check Consumer Lag
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --describe --group bss-oss-group
```

---

## 4. Monitoring

### Check Prometheus
```bash
curl http://localhost:9090/-/healthy
```

### Check Grafana
```bash
curl http://localhost:3000/api/health
```

### View Application Logs
```bash
docker-compose logs -f bss-core
kubectl logs -f deployment/bss-core -n bss-oss
```

---

## 5. Common Issues

### Issue: Service Not Starting
1. Check logs: `docker-compose logs bss-core`
2. Verify database connection
3. Check Kafka availability
4. Verify port availability

### Issue: High Latency
1. Check database connections
2. Monitor Kafka consumer lag
3. Check Redis cache hit rate
4. Review JVM heap usage

### Issue: Failed Payments
1. Check payment gateway connectivity
2. Verify account balance
3. Check transaction logs
4. Review payment status in database

---

## 6. Emergency Procedures

### Scale Up Services
```bash
kubectl scale deployment bss-core --replicas=5 -n bss-oss
```

### Rollback Deployment
```bash
kubectl rollout undo deployment/bss-core -n bss-oss
```

### Emergency Database Maintenance
```bash
# Put system in maintenance mode
kubectl scale deployment bss-core --replicas=0 -n bss-oss

# Perform maintenance
psql -h localhost -U bss_admin -d bss_oss -c "VACUUM FULL;"

# Restore service
kubectl scale deployment bss-core --replicas=3 -n bss-oss
```

---

## 7. Data Migration

### Run Migration Script
```bash
psql -h localhost -U bss_admin -d bss_oss \
  -f /opt/bss-oss/deployment/scripts/data-migration.sql
```

### Verify Migration
```sql
SELECT 'Customers' as entity, COUNT(*) FROM customers
UNION ALL
SELECT 'Accounts', COUNT(*) FROM accounts
UNION ALL
SELECT 'Subscriptions', COUNT(*) FROM subscriptions;
```

---

## 8. Contacts

| Role | Contact |
|------|---------|
| NOC | noc@yemen-telecom.ye |
| DBA | dba@yemen-telecom.ye |
| DevOps | devops@yemen-telecom.ye |
| Emergency | +967-1-234567 |

---

## 9. Maintenance Windows

| Activity | Schedule |
|----------|----------|
| Database Backup | Daily 2:00 AM |
| Log Rotation | Daily 3:00 AM |
| Performance Check | Weekly Sunday 6:00 AM |
| Security Scan | Monthly 1st Monday |

---

## 10. Key Metrics to Monitor

| Metric | Threshold | Action |
|--------|-----------|--------|
| API Latency (p95) | >500ms | Investigate |
| Error Rate | >5% | Alert |
| DB Connections | >90% | Scale |
| Kafka Lag | >10k | Check consumers |
| Disk Usage | >80% | Cleanup |
