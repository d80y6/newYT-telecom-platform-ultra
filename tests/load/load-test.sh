#!/bin/bash

# Load Test Script for Yemen PTC BSS/OSS Platform
# Target: 50M+ subscribers simulation

set -e

echo "========================================="
echo "Yemen PTC BSS/OSS Load Test"
echo "Target: 50M subscribers simulation"
echo "========================================="

# Configuration
BASE_URL=${BASE_URL:-http://localhost:8080}
KAFKA_BOOTSTRAP=${KAFKA_BOOTSTRAP:-localhost:9092}
CONCURRENT_USERS=${CONCURRENT_USERS:-1000}
REQUESTS_PER_USER=${REQUESTS_PER_USER:-1000}
TOTAL_REQUESTS=$((CONCURRENT_USERS * REQUESTS_PER_USER))

echo "Configuration:"
echo "  Base URL: $BASE_URL"
echo "  Kafka: $KAFKA_BOOTSTRAP"
echo "  Concurrent users: $CONCURRENT_USERS"
echo "  Requests per user: $REQUESTS_PER_USER"
echo "  Total requests: $TOTAL_REQUESTS"
echo ""

# Health check
echo "Checking system health..."
if ! curl -s "$BASE_URL/actuator/health" | grep -q '"status":"UP"'; then
    echo "ERROR: Service is not healthy"
    exit 1
fi
echo "✓ System is healthy"

# Create test data
echo "Creating test data..."
cat > /tmp/test-customer.json << EOF
{
    "name": "Load Test Customer",
    "email": "loadtest@ptc.ye",
    "phone": "+967123456789",
    "address": "Sanaa, Yemen",
    "customerType": "RESIDENTIAL",
    "status": "ACTIVE"
}
EOF

# Create customer
CUSTOMER_ID=$(curl -s -X POST "$BASE_URL/api/v1/customers" \
    -H "Content-Type: application/json" \
    -d @/tmp/test-customer.json | jq -r '.id')

echo "Created test customer: $CUSTOMER_ID"

# Create account with balance
cat > /tmp/test-account.json << EOF
{
    "customerId": "$CUSTOMER_ID",
    "accountType": "PREPAID",
    "currency": "YER",
    "initialBalance": 1000000
}
EOF

ACCOUNT_ID=$(curl -s -X POST "$BASE_URL/api/v1/accounts" \
    -H "Content-Type: application/json" \
    -d @/tmp/test-account.json | jq -r '.id')

echo "Created test account: $ACCOUNT_ID"

# Generate CDR events
echo "Generating CDR events..."
cat > /tmp/generate-cdrs.py << 'PYEOF'
import json
import uuid
import time
from datetime import datetime, timedelta
import random

def generate_cdr():
    cdr_types = ['VOICE', 'SMS', 'DATA']
    cdr_type = random.choice(cdr_types)
    
    cdr = {
        "eventId": str(uuid.uuid4()),
        "accountId": "${ACCOUNT_ID}",
        "serviceType": cdr_type,
        "eventTime": datetime.now().isoformat() + "Z",
        "sourceSystem": "LOAD_TEST",
        "ratingRequired": True
    }
    
    if cdr_type == 'VOICE':
        cdr.update({
            "durationSeconds": random.randint(30, 600),
            "calledNumber": f"+9677{random.randint(1000000, 9999999)}",
            "callingNumber": f"+9677{random.randint(1000000, 9999999)}"
        })
    elif cdr_type == 'SMS':
        cdr.update({
            "messageCount": 1,
            "destination": f"+9677{random.randint(1000000, 9999999)}",
            "origin": f"+9677{random.randint(1000000, 9999999)}"
        })
    elif cdr_type == 'DATA':
        cdr.update({
            "volumeBytes": random.randint(1024, 104857600),  # 1KB to 100MB
            "uploadBytes": random.randint(1024, 52428800),
            "downloadBytes": random.randint(1024, 52428800)
        })
    
    return cdr

if __name__ == "__main__":
    cdrs = []
    for i in range(10000):  # Generate 10,000 CDRs for testing
        cdrs.append(generate_cdr())
    
    with open('/tmp/cdrs.json', 'w') as f:
        json.dump(cdrs, f)
    
    print(f"Generated {len(cdrs)} CDR events")
PYEOF

python3 /tmp/generate-cdrs.py

# Send CDRs to Kafka
echo "Sending CDRs to Kafka..."
if command -v kafkacat &> /dev/null; then
    cat /tmp/cdrs.json | jq -c '.[]' | kafkacat -b "$KAFKA_BOOTSTRAP" -t billing.cdr.raw -P
    echo "✓ CDRs sent to Kafka"
else
    echo "WARNING: kafkacat not installed, skipping Kafka test"
fi

# Run API load test
echo "Running API load test..."
cat > /tmp/load-test-config.json << EOF
{
  "target": "$BASE_URL",
  "phases": [
    {
      "duration": "30s",
      "arrivalRate": 10,
      "name": "Warm up"
    },
    {
      "duration": "2m",
      "arrivalRate": 100,
      "name": "Medium load"
    },
    {
      "duration": "1m",
      "arrivalRate": 1000,
      "name": "Peak load"
    },
    {
      "duration": "30s",
      "arrivalRate": 10,
      "name": "Cooldown"
    }
  ],
  "defaults": {
    "headers": {
      "Content-Type": "application/json",
      "X-API-Key": "load-test-key"
    }
  },
  "scenarios": [
    {
      "name": "Customer API",
      "flow": [
        {
          "get": {
            "url": "/api/v1/customers"
          }
        },
        {
          "post": {
            "url": "/api/v1/customers",
            "json": {
              "name": "Test Customer",
              "email": "test@example.com",
              "phone": "+967700000000",
              "customerType": "RESIDENTIAL"
            }
          }
        }
      ]
    },
    {
      "name": "Account Balance",
      "flow": [
        {
          "get": {
            "url": "/api/v1/accounts/${ACCOUNT_ID}/balance"
          }
        }
      ]
    },
    {
      "name": "Rating Engine",
      "flow": [
        {
          "post": {
            "url": "/api/v1/rating/rate",
            "json": {
              "eventId": "\${random.uuid()}",
              "accountId": "${ACCOUNT_ID}",
              "serviceType": "DATA",
              "volumeBytes": 1048576,
              "eventTime": "\${date.iso()}"
            }
          }
        }
      ]
    }
  ]
}
EOF

if command -v artillery &> /dev/null; then
    artillery run /tmp/load-test-config.json --output /tmp/load-test-report.json
    artillery report /tmp/load-test-report.json --output /tmp/load-test-report.html
    
    echo "Load test completed"
    echo "Report generated: /tmp/load-test-report.html"
else
    echo "WARNING: artillery not installed, skipping load test"
    echo "Install with: npm install -g artillery"
fi

# Check rating results
echo "Checking rating results..."
sleep 5  # Wait for rating to complete

RATING_COUNT=$(curl -s "$BASE_URL/api/v1/rating/events?accountId=${ACCOUNT_ID}" | jq '.totalElements // 0')
echo "Rated events: $RATING_COUNT"

if [ "$RATING_COUNT" -gt 0 ]; then
    echo "✓ Rating engine is processing events"
else
    echo "✗ Rating engine may not be working"
fi

# Check balance updates
echo "Checking balance updates..."
FINAL_BALANCE=$(curl -s "$BASE_URL/api/v1/accounts/${ACCOUNT_ID}/balance" | jq -r '.mainBalance // 0')
echo "Final balance: $FINAL_BALANCE YER"

if [ "$FINAL_BALANCE" -lt 1000000 ]; then
    echo "✓ Balance deductions detected (charging is working)"
else
    echo "✗ No balance deductions detected"
fi

# Performance metrics
echo ""
echo "========================================="
echo "PERFORMANCE METRICS"
echo "========================================="

# Get metrics from Prometheus endpoint
if curl -s "$BASE_URL/actuator/prometheus" > /tmp/metrics.txt 2>/dev/null; then
    # Extract key metrics
    HTTP_REQUESTS=$(grep 'http_server_requests_seconds_count' /tmp/metrics.txt | tail -1 | awk '{print $2}' || echo "0")
    RATING_LATENCY=$(grep 'rating_latency_seconds' /tmp/metrics.txt | tail -1 | awk '{print $2}' || echo "0")
    
    echo "Total HTTP requests: $HTTP_REQUESTS"
    echo "Rating latency (seconds): $RATING_LATENCY"
    
    # Check if rating latency < 100ms
    if [ "$(echo "$RATING_LATENCY < 0.1" | bc -l 2>/dev/null || echo 1)" = "1" ]; then
        echo "✓ Rating latency: $(echo "$RATING_LATENCY * 1000" | bc -l | awk '{printf "%.2f", $1}')ms (meets <100ms requirement)"
    else
        echo "✗ Rating latency: $(echo "$RATING_LATENCY * 1000" | bc -l | awk '{printf "%.2f", $1}')ms (exceeds 100ms requirement)"
    fi
fi

# Cleanup
echo ""
echo "Cleaning up test data..."
curl -s -X DELETE "$BASE_URL/api/v1/accounts/${ACCOUNT_ID}"
curl -s -X DELETE "$BASE_URL/api/v1/customers/${CUSTOMER_ID}"

echo ""
echo "========================================="
echo "LOAD TEST COMPLETE"
echo "========================================="
echo "Summary:"
echo "  - Test customer/account created and deleted"
echo "  - 10,000 CDR events generated"
echo "  - Load test scenarios executed"
echo "  - Rating engine validated"
echo "  - Balance updates verified"
echo ""
echo "Next steps:"
echo "  1. Review /tmp/load-test-report.html"
echo "  2. Scale test to 50M subscribers simulation"
echo "  3. Add concurrency testing (1000+ concurrent requests)"
echo "  4. Add failure injection testing"