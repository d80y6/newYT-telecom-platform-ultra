#!/usr/bin/env python3
"""
Kafka Producer Module - Publishes enriched CDR events to 'usage-events' topic
Implements proper partitioning, serialization, error handling with retries/backoff
"""

import logging
import json
import time
from typing import Dict, Any, Optional
from kafka import KafkaProducer
from kafka.errors import KafkaError, NoBrokersAvailable, KafkaTimeoutError
from datetime import datetime
import backoff
import ssl

logger = logging.getLogger('CDRPipelineKafkaProducer')

class CDRPipelineProducer:
    """Kafka producer for publishing CDR events with production-grade reliability"""
    
    def __init__(self, config: Dict[str, Any]):
        """
        Initialize Kafka producer with production-hardened configuration
        
        Args:
            config: Configuration dict containing:
                - 'bootstrap_servers': List of Kafka broker addresses
                - 'topic': Target topic name (default: 'usage-events')
                - 'partition_key_strategy': Strategy for event partitioning
                - 'security_settings': SSL/credential configuration
                - 'retries': Max retry attempts
                - 'retry_backoff_factor': Exponential backoff multiplier
        """
        self.bootstrap_servers = config.get('bootstrap_servers', ['localhost:9092'])
        self.topic = config.get('topic', 'usage-events')
        self.partition_key_strategy = config.get('partition_key_strategy', 'hash')
        self.retries = config.get('retries', 5)
        self.retry_backoff_factor = config.get('retry_backoff_factor', 2)
        self.ssl_enabled = config.get('ssl_enabled', False)
        self.security_protocol = config.get('security_protocol', 'PLAINTEXT')
        self.acks_setting = config.get('acks_setting', 'all')
        
        # SSL Configuration
        self.ssl_context = None
        if self.ssl_enabled:
            self.ssl_context = self._create_ssl_context()
        
        # Initialize producer with production settings
        self.producer = self._create_producer()
    
    def _create_ssl_context(self) -> ssl.SSLContext:
        """Create SSL context for secure Kafka connections"""
        ctx = ssl.create_default_context()
        try:
            # Configure SSL certificate verification
            ctx.load_verify_locations(
                cafile=self.config.get('ca_cert_path', '/etc/ssl/certs/ca-certificates.crt')
            )
            if self.config.get('certfile'):
                ctx.load_cert_chain(
                    certfile=self.config['certfile'],
                    keyfile=self.config['keyfile']
                )
        except Exception as e:
            logger.warning(f"SSL context configuration failed: {str(e)}")
        return ctx
    
    def _safe_disconnect(self):
        """Safely close producer connection"""
        try:
            if self.producer:
                self.producer.flush()
                self.producer.close()
                logger.info("Kafka producer disconnected successfully")
        except Exception as e:
            logger.error(f"Error closing Kafka producer: {str(e)}")
        finally:
            self.producer = None
    
    def _create_producer(self) -> KafkaProducer:
        """Create and configure Kafka producer with production settings"""
        try:
            producer = KafkaProducer(
                bootstrap_servers=self.bootstrap_servers,
                topic=self.topic,
                retries=self.retries,
                request_timeout_ms=30000,  # 30 second timeout
                max_inflight_requests_per_connection=5,
                acks=self.acks_setting,  # Full acknowledgement from all replicas
                compression_type='snappy',  # Enable compression for efficiency
                value_serializer=self._serialize_event,
                key_serializer=self._serialize_key,
                security_protocol=self.security_protocol,
                ssl_context=self.ssl_context,
                # Enable idempotent producer to prevent duplicates
                enable_idempotence=True,
                # Configure linger time for better throughput
                linger_ms=5,
                # Configure buffer memory
                buffer_memory=32768
            )
            logger.info(f"Kafka producer initialized with {len(self.bootstrap_servers)} brokers")
            return producer
        except NoBrokersAvailable as e:
            logger.critical(f"Kafka brokers unavailable: {str(e)}")
            raise
        except Exception as e:
            logger.critical(f"Producer initialization failed: {str(e)}")
            raise
    
    def _serialize_key(self, key: Optional[str]) -> bytes:
        """Serialize key for partitioning"""
        if key is None:
            return json.dumps({"default_key": "none"}).encode('utf-8')
        return key.encode('utf-8')
    
    def _serialize_event(self, event: Dict[str, Any]) -> bytes:
        """Serialize event payload for transmission"""
        try:
            # Add timestamp for auditability
            event_with_timestamp = event.copy()
            event_with_timestamp['_event_timestamp'] = datetime.utcnow().isoformat()
            
            # Ensure consistent JSON serialization
            return json.dumps(event_with_timestamp, default=str).encode('utf-8')
        except Exception as e:
            logger.error(f"Event serialization failed: {str(e)}")
            # Fallback to basic serialization for error handling
            return json.dumps({"error": "serialization_failed", "original_error": str(e)}).encode('utf-8')
    
    @backoff.on_exception(
        backoff.expo,
        (KafkaError,),
        max_tries=5,
        factor=2,
        base_delay=1,
        jitter=None,
        logger='backoff'
    )
    def send_event(self, event: Dict[str, Any], callback=None) -> None:
        """
        Send a CDR event to Kafka with exponential backoff retry
        
        Args:
            event: CDR event structure to publish
            callback: Optional delivery callback for async processing
        """
        try:
            # Use event ID as partition key for consistent routing
            partition_key = event.get('event_id', 'default')
            
            # Enable idempotent producer settings via producer config
            future = self.producer.send(
                topic=self.topic,
                key=self._serialize_key(partition_key),
                value=self._serialize_event(event)
            )
            
            # Async delivery callback
            if callback:
                future.add_callback(callback)
            
            logger.debug(f"Event queued for delivery: {event.get('event_id', 'unknown')}")
            return future
            
        except KafkaTimeoutError as e:
            logger.warning(f"Kafka timeout for event {event.get('event_id')}: {str(e)}")
            raise
        except KafkaError as e:
            logger.error(f"Kafka broker error for event {event.get('event_id')}: {str(e)}")
            raise
        except Exception as e:
            logger.error(f"Unexpected error sending event {event.get('event_id')}: {str(e)}")
            raise
    
    def send_batch_events(self, events: list, batch_size: int = 1000) -> Dict[str, Any]:
        """Send multiple events in batches with progress tracking"""
        results = {
            'total_events': len(events),
            'successful': 0,
            'failed': 0,
            'failed_events': [],
            'delivery_reports': {}
        }
        
        try:
            # Process events in batches
            for i in range(0, len(events), batch_size):
                batch = events[i:i + batch_size]
                delivery_reports = []
                
                for event in batch:
                    try:
                        future = self.send_event(event)
                        delivery_report = future.get(timeout=30)
                        delivery_reports.append({
                            'event_id': event.get('event_id'),
                            'partition': delivery_report.partition,
                            'offset': delivery_report.offset,
                            'timestamp': delivery_report.timestamp
                        })
                        results['successful'] += 1
                    except Exception as e:
                        results['failed'] += 1
                        results['failed_events'].append({
                            'event_id': event.get('event_id'),
                            'error': str(e)
                        })
                
                results['delivery_reports'].update({
                    f'batch_{i//batch_size}': delivery_reports
                })
            
            logger.info(f"Batch delivery completed: {results['successful']} successful, {results['failed']} failed")
            return results
            
        except Exception as e:
            logger.error(f"Batch processing failed: {str(e)}", exc_info=True)
            results['failed'] = len(events)
            results['failed_events'] = [{'event_id': e.get('event_id', 'unknown'), 'error': str(e)} for e in events]
            return results
        
        finally:
            # Ensure all pending sends are flushed
            try:
                self.producer.flush(timeout=10)
            except Exception as flush_error:
                logger.error(f"Failed to flush producer: {str(flush_error)}")
    
    def shutdown(self, timeout: int = 30) -> None:
        """Graceful shutdown sequence"""
        logger.info("Initiating graceful shutdown...")
        try:
            self._safe_disconnect()
            logger.info("Shutdown completed successfully")
        except Exception as e:
            logger.error(f"Error during shutdown: {str(e)}")
            raise
    
    def health_check(self) -> Dict[str, Any]:
        """Perform health check on Kafka connection"""
        try:
            # Test with a lightweight metadata request
            metadata = self.producer.cluster.broker_metadata()
            broker_count = len(metadata.brokers)
            return {
                'status': 'healthy',
                'connected_brokers': broker_count,
                'topic_partitions': len(self.producer.partitions_for_topic(self.topic)),
                'last_error': None,
                'timestamp': datetime.utcnow().isoformat()
            }
        except Exception as e:
            return {
                'status': 'unhealthy',
                'connected_brokers': 0,
                'topic_partitions': 0,
                'last_error': str(e),
                'timestamp': datetime.utcnow().isoformat()
            }
    
    def get_metrics(self) -> Dict[str, Any]:
        """Retrieve producer performance metrics"""
        try:
            return {
                'buffer_size': self.producer.buffer_total,
                'buffer_usage': self.producer.buffer_log,
                'pending_records': len(self.producer RecordsPending),
                'open_connections': len(self.bootstrap_servers),
                'queued_events': self.producer._queue.get_queue_length() if hasattr(self.producer, '_queue') else 0
            }
        except Exception as e:
            logger.warning(f"Metrics retrieval failed: {str(e)}")
            return {'metrics_error': str(e)}

# Example usage for testing
if __name__ == "__main__":
    # Configuration for Kafka producer
    config = {
        'bootstrap_servers': ['localhost:9092'],
        'topic': 'usage-events',
        'partition_key_strategy': 'hash',
        'ssl_enabled': False,
        'security_protocol': 'PLAINTEXT',
        'retries': 5,
        'retry_backoff_factor': 2,
        'ca_cert_path': '/etc/ssl/certs/ca-certificates.crt',
        'security_protocol': 'PLAINTEXT'
    }
    
    # Initialize producer
    producer = CDRPipelineProducer(config)
    
    # Sample event for testing
    sample_event = {
        'event_id': 'CDR-123456',
        'user_id': 'USER-7890',
        'service_type': 'voice',
        'event_type': 'cdr_processed',
        'processing_stage': 'enrichment',
        'timestamp': '2023-05-01T10:00:00Z'
    }
    
    try:
        # Send test event
        producer.send_event(sample_event)
        print("Test event sent successfully!")
        
        # Get health status
        health = producer.health_check()
        print(f"Health check: {health}")
        
        # Get metrics
        metrics = producer.get_metrics()
        print(f"Current metrics: {metrics}")
        
    finally:
        # Clean shutdown
        producer.shutdown()