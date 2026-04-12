# Runbook: Service Down / Availability Failure

## Symptoms
- HTTP 502/503/504 errors
- Health check endpoint returns unhealthy
- No response from API endpoints
- Alert: ServiceDown triggered

## Immediate Actions (0-1 minutes)

### 1. Verify service status
```bash
kubectl get pods -n bss-oss -l app=bss-core
kubectl get endpoints bss-core -n bss-oss
```

### 2. Check recent events
```bash
kubectl get events -n bss-oss --sort-by='.lastTimestamp' | tail -20
```

### 3. Check node resources
```bash
kubectl top nodes
kubectl describe nodes | grep -A 5 "Conditions"
```

## Diagnostic Steps (1-10 minutes)

### 1. Check pod logs
```bash
kubectl logs <pod> -n bss-oss --previous
kubectl logs <pod> -n bss-oss --tail=100
```

### 2. Describe problematic pod
```bash
kubectl describe pod <pod> -n bss-oss
```

### 3. Check for OOMKilled status
```bash
kubectl get pod <pod> -n bss-oss -o jsonpath='{.status.containerStatuses[*].lastState.terminated}'
```

### 4. Check Istio/Network policies
```bash
kubectl get virtualservice -n bss-oss
kubectl get destinationrule -n bss-oss
```

## Resolution Steps (10-30 minutes)

### 1. Restart affected service
```bash
kubectl rollout restart deployment/bss-core -n bss-oss
```

### 2. If restart fails - check image
```bash
kubectl get deployment bss-core -n bss-oss -o jsonpath='{.spec.template.spec.containers[0].image}'
```

### 3. Rollback to previous version
```bash
kubectl rollout undo deployment/bss-core -n bss-oss
```

### 4. Force delete stuck pods
```bash
kubectl delete pod <pod> -n bss-oss --grace-period=0 --force
```

### 5. Scale up if needed
```bash
kubectl scale deployment bss-core --replicas=3 -n bss-oss
```

## Verification
- Health endpoint returns 200
- Pods in Running state
- Endpoints have ready addresses
- Error rate at 0%

## Escalation
- P1: Notify on-call SRE immediately
- P2: Create JIRA ticket
- P3: Document in team channel

## RTO Target
- Detection: <30 seconds
- Response: <1 minute
- Resolution: <30 minutes

## Prevention
- Implement readiness/liveness probes
- Set up PodDisruptionBudgets
- Configure HPA for auto-scaling
- Regular chaos engineering
