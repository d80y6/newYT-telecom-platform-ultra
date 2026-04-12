# Runbook: Charging Engine Issues

## Symptoms
- Charging latency p99 > 50ms
- CDRs not being processed
- Balance deduction failures
- Prepaid customers unable to use services

## Diagnosis

1. Check Go service health:
```bash
curl http://charging-engine:8081/health
```

2. Check CDR processing rate:
```bash
curl http://charging-engine:8081/metrics | grep cdrs_processed_total
```

3. Check Redis connectivity:
```bash
redis-cli -h redis-cluster PING
```

4. Check Kafka consumer status:
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group charging-engine-group --describe
```

## Resolution

### High latency (> 50ms p99)
1. Check Redis memory:
```bash
redis-cli -h redis-cluster INFO memory | grep used_memory_human
```
2. If Redis memory > 80%, evict old keys:
```bash
redis-cli -h redis-cluster CONFIG SET maxmemory-policy allkeys-lru
```

### CDR processing stalled
1. Check Kafka consumer lag:
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group charging-engine-group --describe
```
2. If consumer is stuck, restart:
```bash
kubectl rollout restart deployment/charging-engine -n bss-oss
```

### Balance query failures
1. Verify Redis cluster health:
```bash
redis-cli -h redis-cluster CLUSTER INFO | grep cluster_state
```
2. If cluster is down, failover:
```bash
redis-cli -h redis-cluster CLUSTER FAILOVER
```

## Prevention
- Alert when charging latency p99 > 50ms
- Alert when Kafka consumer lag > 10K
- Alert when Redis memory > 75%
- Maintain Redis cluster with 3 masters + 3 replicas
