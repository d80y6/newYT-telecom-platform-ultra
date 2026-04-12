#!/bin/bash
# Elasticsearch Index Setup for BSS Platform

echo "Setting up Elasticsearch indexes..."

# Customer Index
curl -X PUT "http://localhost:9200/customers" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "customerId": {"type": "keyword"},
      "firstName": {"type": "text"},
      "lastName": {"type": "text"},
      "email": {"type": "keyword"},
      "phone": {"type": "keyword"},
      "status": {"type": "keyword"},
      "customerType": {"type": "keyword"},
      "totalLifetimeValue": {"type": "double"},
      "creditScore": {"type": "integer"},
      "segments": {"type": "keyword"},
      "createdAt": {"type": "date"},
      "updatedAt": {"type": "date"}
    }
  }
}
'

# Product Catalog Index
curl -X PUT "http://localhost:9200/products" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "productId": {"type": "keyword"},
      "name": {"type": "text"},
      "description": {"type": "text"},
      "category": {"type": "keyword"},
      "status": {"type": "keyword"},
      "price": {"type": "double"},
      "currency": {"type": "keyword"},
      "createdAt": {"type": "date"}
    }
  }
}
'

# Orders Index
curl -X PUT "http://localhost:9200/orders" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "orderId": {"type": "keyword"},
      "customerId": {"type": "keyword"},
      "orderType": {"type": "keyword"},
      "status": {"type": "keyword"},
      "priority": {"type": "keyword"},
      "totalAmount": {"type": "double"},
      "createdAt": {"type": "date"},
      "completedAt": {"type": "date"}
    }
  }
}
'

# Usage Events Index
curl -X PUT "http://localhost:9200/usage-events" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "eventId": {"type": "keyword"},
      "customerId": {"type": "keyword"},
      "subscriptionId": {"type": "keyword"},
      "usageType": {"type": "keyword"},
      "amount": {"type": "double"},
      "unit": {"type": "keyword"},
      "timestamp": {"type": "date"}
    }
  }
}
'

# Billing Index
curl -X PUT "http://localhost:9200/billing" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "invoiceId": {"type": "keyword"},
      "customerId": {"type": "keyword"},
      "accountId": {"type": "keyword"},
      "amount": {"type": "double"},
      "status": {"type": "keyword"},
      "issueDate": {"type": "date"},
      "dueDate": {"type": "date"}
    }
  }
}
'

# Notifications Index
curl -X PUT "http://localhost:9200/notifications" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "notificationId": {"type": "keyword"},
      "customerId": {"type": "keyword"},
      "channel": {"type": "keyword"},
      "type": {"type": "keyword"},
      "status": {"type": "keyword"},
      "sentAt": {"type": "date"}
    }
  }
}
'

# Network Resources Index
curl -X PUT "http://localhost:9200/network-resources" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "resourceId": {"type": "keyword"},
      "resourceType": {"type": "keyword"},
      "name": {"type": "text"},
      "status": {"type": "keyword"},
      "location": {"type": "keyword"},
      "capacity": {"type": "double"},
      "utilization": {"type": "double"}
    }
  }
}
'

echo "Elasticsearch indexes created successfully!"
curl -s "http://localhost:9200/_cat/indices?v"