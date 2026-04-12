# Runbook: Order Processing Failures

## Symptoms
- Orders stuck in IN_PROGRESS status for > 30 minutes
- Order saga failure rate > 5%
- Customer complaints about order delays

## Diagnosis

1. Check order saga state:
```sql
SELECT id, order_number, status, characteristics->>'sagaState' as saga_state, created_at 
FROM orders 
WHERE status = 'IN_PROGRESS' 
AND created_at < NOW() - INTERVAL '30 minutes';
```

2. Check Kafka consumer lag for order events:
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group order-mgmt-group --describe
```

3. Check Redis locks:
```bash
redis-cli KEYS "order:lock:*"
```

## Resolution

### Stuck orders due to Redis lock
1. Check lock TTL:
```bash
redis-cli TTL "order:lock:{order_id}"
```
2. If lock is stale (> 5 minutes old), manually release:
```bash
redis-cli DEL "order:lock:{order_id}"
```
3. Restart order processing via API:
```bash
curl -X POST http://bss-core:8080/tmf-api/productOrderingManagement/v5/productOrder/{id}/execute
```

### Kafka consumer lag
1. Check consumer group health:
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group order-mgmt-group --describe
```
2. If consumer is down, restart the service:
```bash
kubectl rollout restart deployment/bss-core -n bss-oss
```

### Database connection pool exhaustion
1. Check active connections:
```sql
SELECT count(*) FROM pg_stat_activity WHERE state = 'active';
```
2. Kill long-running queries:
```sql
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE state = 'active' AND query_start < NOW() - INTERVAL '5 minutes';
```

## Prevention
- Monitor Kafka consumer lag alerting threshold: 10K messages
- Set Redis lock TTL to 5 minutes maximum
- Configure HPA to scale on CPU > 70%
