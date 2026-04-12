#!/bin/bash

# ============================================================================
# YEMEN PTC BSS/OSS - KAFKA TOPICS SETUP SCRIPT
# Creates all required Kafka topics for the platform
# ============================================================================

KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS:-localhost:9092}

echo "Setting up Kafka topics on $KAFKA_BOOTSTRAP_SERVERS..."

# ============ CUSTOMER EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic customer.events \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=604800000 \
  --config cleanup.policy=delete

# ============ BILLING EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic billing.events \
  --partitions 12 --replication-factor 1 \
  --config retention.ms=2592000000 \
  --config cleanup.policy=compact

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic billing.cdr.raw \
  --partitions 24 --replication-factor 1 \
  --config retention.ms=604800000 \
  --config cleanup.policy=delete

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic billing.cdr.rated \
  --partitions 24 --replication-factor 1 \
  --config retention.ms=2592000000 \
  --config cleanup.policy=compact

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic billing.recharge \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=2592000000

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic billing.invoice \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=2592000000

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic billing.payment \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=2592000000

# ============ ORDER EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic order.events \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=2592000000

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic order.fulfillment \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=604800000

# ============ CATALOG EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic catalog.events \
  --partitions 3 --replication-factor 1 \
  --config retention.ms=2592000000

# ============ PROVISIONING EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic provisioning.events \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=604800000

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic provisioning.tasks \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=604800000

# ============ RATING EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic rating.events \
  --partitions 12 --replication-factor 1 \
  --config retention.ms=604800000

# ============ NETWORK EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic network.alarm \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=604800000

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic network.performance \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=259200000

# ============ NOTIFICATION EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic notification.sms \
  --partitions 3 --replication-factor 1 \
  --config retention.ms=86400000

kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic notification.email \
  --partitions 3 --replication-factor 1 \
  --config retention.ms=86400000

# ============ AUDIT EVENTS ============
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --create --if-not-exists \
  --topic audit.events \
  --partitions 6 --replication-factor 1 \
  --config retention.ms=31536000000 \
  --config cleanup.policy=compact

echo "Kafka topics setup complete!"
echo ""
echo "Listing all topics:"
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --list
