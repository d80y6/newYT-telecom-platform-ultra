# Runbook: High CPU Usage

## Symptoms
- CPU usage >85% for >5 minutes on any service
- Increased response times (>2s p95)
- Possible service degradation

## Immediate Actions (0-5 minutes)

### 1. Check Grafana CPU dashboard
```
https://grafana.yemenptc.ye/d/bss-cpu
```

### 2. Identify offending service
```bash
kubectl top pods -n bss-oss
```

### 3. Check recent deployments
```bash
kubectl rollout status deploy/<service> -n bss-oss
```

## Diagnostic Steps (5-15 minutes)

### 1. Get thread dump
```bash
kubectl exec <pod> -- jstack <pid> > threaddump.txt
```

### 2. Analyze thread dump
```bash
java -jar tda.jar threaddump.txt
```

### 3. Check GC logs
```bash
kubectl logs <pod> -c <container> | grep GC
```

### 4. Check for database query issues
```bash
kubectl exec -it <pod> -- psql -U postgres -c "SELECT * FROM pg_stat_activity WHERE state = 'active';"
```

## Resolution Steps (15-60 minutes)

### 1. Memory leak - Rollback deployment
```bash
kubectl rollout undo deploy/<service> -n bss-oss
```

### 2. CPU spike - Scale horizontally
```bash
kubectl scale deploy <service> --replicas=+2 -n bss-oss
```

### 3. GC thrashing - Tune JVM flags
```yaml
env:
  - name: JAVA_OPTS
    value: "-XX:MaxGCPauseMillis=50 -XX:+UseG1GC -Xms512m -Xmx1024m"
```

### 4. Database connection exhaustion
```bash
kubectl exec -it <pod> -- psql -U postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE state = 'idle';"
```

## Verification
- Monitor CPU <70% for 15 minutes
- Check error rates in Grafana
- Verify response times normalized

## Prevention
- Add CPU alerts to Prometheus:
```yaml
- alert: HighCPUUsage
  expr: sum(rate(container_cpu_usage_seconds_total[5m])) by (pod) > 0.85
  for: 5m
```
- Implement HPA based on CPU utilization
- Regular performance testing in staging
- Database query performance reviews

## Escalation
- P1: Notify on-call SRE immediately
- P2: Create JIRA ticket, notify team lead
- P3: Document in team channel

## RTO Target
- Detection: <1 minute
- Response: <5 minutes
- Resolution: <60 minutes
