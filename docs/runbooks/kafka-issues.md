# Runbook: Kafka Issues

## Symptoms
- Consumer lag increasing
- Events not being published
- Schema registry errors
- Broker unavailable

## Diagnosis

1. Check broker health:
```bash
kafka-broker-api-versions.sh --bootstrap-server localhost:9092
```

2. Check topic status:
```bash
kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic party.events
```

3. Check consumer group lag:
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --all-groups
```

4. Check schema registry:
```bash
curl http://schema-registry:8081/subjects
```

## Resolution

### Consumer lag > 10K messages
1. Identify slow consumer:
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group {group_id}
```
2. Scale consumer:
```bash
kubectl scale deployment/bss-core --replicas=5 -n bss-oss
```

### Broker unavailable
1. Check pod status:
```bash
kubectl get pods -n kafka -l app=kafka
```
2. Restart failed broker:
```bash
kubectl delete pod kafka-2 -n kafka
```

### Schema registry errors
1. Check compatibility mode:
```bash
curl http://schema-registry:8081/config
```
2. Set to backward compatibility:
```bash
curl -X PUT http://schema-registry:8081/config -d '{"compatibility": "BACKWARD"}'
```

## Prevention
- Alert when consumer lag > 10K
- Alert when broker count < 3
- Maintain replication factor of 3
- Use Schema Registry with backward compatibility
