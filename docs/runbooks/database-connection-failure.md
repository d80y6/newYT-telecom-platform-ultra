# Runbook: Database Connection Failure

## Symptoms
- HTTP 503 Service Unavailable errors
- "Connection refused" errors in logs
- Database connection pool exhausted errors
- High latency on all endpoints

## Immediate Actions (0-2 minutes)

### 1. Verify database is running
```bash
kubectl get pods -n bss-oss | grep postgres
```

### 2. Check database pod logs
```bash
kubectl logs <postgres-pod> -n bss-oss
```

### 3. Test connectivity from application pod
```bash
kubectl exec -it <bss-core-pod> -n bss-oss -- \
  nc -zv postgres-primary 5432
```

## Diagnostic Steps (2-10 minutes)

### 1. Check connection pool status
```bash
kubectl exec -it <bss-core-pod> -n bss-oss -- \
  curl localhost:8080/api/v1/actuator/metrics/hikaricp.connections.active
```

### 2. Check database replication status
```bash
kubectl exec -it <postgres-pod> -n bss-oss -- \
  psql -U postgres -c "SELECT * FROM pg_stat_replication;"
```

### 3. Check for long-running queries
```bash
kubectl exec -it <postgres-pod> -n bss-oss -- \
  psql -U postgres -c "SELECT pid, usename, query, state, wait_event FROM pg_stat_activity WHERE state = 'active';"
```

## Resolution Steps (10-30 minutes)

### 1. Restart stuck connections
```bash
kubectl exec -it <postgres-pod> -n bss-oss -- \
  psql -U postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE state = 'idle in transaction';"
```

### 2. Scale database read replicas
```bash
kubectl scale statefulset postgres-replica --replicas=3 -n bss-oss
```

### 3. Failover to standby database
```bash
kubectl exec -it <bss-core-pod> -n bss-oss -- \
  ./trigger-failover.sh
```

### 4. Increase connection pool temporarily
```bash
kubectl set env deployment/bss-core \
  SPRING_DATASOURCE_HIKARI_MAXIMUM-POOL-SIZE=100 -n bss-oss
```

## Verification
- Verify connection pool metrics normalized
- Check error rate dropped to 0
- Confirm p99 latency <500ms

## Prevention
- Set up pgBouncer connection pooling
- Implement read/write splitting
- Add database connection health checks
- Monitor connection pool utilization with alerts

## RTO Target
- Detection: <30 seconds
- Response: <2 minutes
- Resolution: <30 minutes
