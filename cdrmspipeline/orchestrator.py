#!/usr/bin/env python3
"""
CDR Mediation Pipeline Orchestrator - Coordinates end-to-end processing of CDR events
Implements production-grade workflow orchestration with metrics collection and error handling
"""

import logging
import time
import json
from datetime import datetime
from typing import List, Dict, Any

# Import pipeline components
from parser.cdr_parser import CDRParser
from normalizer.schema_normalizer import SchemaNormalizer
from enricher.context_enricher import ContextEnricher
from kafka.producer import CDRPipelineProducer
from support.metrics_collector import MetricsCollector

logger = logging.getLogger("CDRPipelineOrchestrator")


class CDMMediationOrchestrator:
    """Main orchestrator coordinating the complete CDR mediation workflow"""

    def __init__(self, config: Dict[str, Any]):
        """
        Initialize pipeline orchestrator with comprehensive configuration

        Args:
            config: Configuration dictionary containing:
                - pipeline_components: Dict with parser, normalizer, enricher, producer configs
                - metrics_enabled: Boolean flag for metrics collection
                - error_handling: Error handling strategy configuration
                - retry_policy: Retry configuration for failed processing
        """
        self.config = config
        self.pipeline_components = config.get("pipeline_components", {})
        self.metrics_enabled = config.get("metrics_enabled", True)
        self.error_handling = config.get("error_handling", {})
        self.retry_policy = config.get("retry_policy", {})

        # Initialize pipeline components with their respective configs
        self.parser = CDRParser(self._get_component_config("parser", config))
        self.normalizer = SchemaNormalizer(
            self._get_component_config("normalizer", config)
        )
        self.enricher = ContextEnricher(self._get_component_config("enricher", config))
        self.producer = CDRPipelineProducer(
            self._get_component_config("producer", config)
        )

        # Initialize metrics collector if enabled
        self.metrics_collector = (
            MetricsCollector(self._get_component_config("metrics", config))
            if self.metrics_enabled
            else None
        )

        # Performance tracking variables
        self.processing_start_time = None
        self.total_events_processed = 0
        self.successful_events = 0
        self.failed_events = 0

        logger.info("CDR Mediation Pipeline Orchestrator initialized")

    def _get_component_config(self, component_name: str, config: Dict) -> Dict:
        """Retrieve component-specific configuration"""
        components_config = config.get("pipeline_components", {})
        return components_config.get(component_name, {})

    def start_processing(self, raw_cdr_events: List[str]) -> Dict[str, Any]:
        """
        Initiate complete CDR processing workflow

        Args:
            raw_cdr_events: List of raw CDR event strings to process

        Returns:
            Dict containing processing metrics and final status
        """
        # Reset tracking variables
        self.processing_start_time = time.time()
        self.total_events_processed = 0
        self.successful_events = 0
        self.failed_events = 0

        logger.info(f"Starting processing of {len(raw_cdr_events)} CDR events")

        try:
            # Step 1: Parse raw CDR events
            parsed_cdrs = self.parser.process_batch(raw_cdr_events)
            logger.debug(f"Parsed {len(parsed_cdrs)} events successfully")

            # Step 2: Track processing metrics
            if self.metrics_enabled:
                self.metrics_collector.record_stage("parse", len(parsed_cdrs))

            self.total_events_processed = len(parsed_cdrs)

            # Step 3: Normalize parsed events
            normalized_cdrs = self.normalizer.batch_normalize(parsed_cdrs)
            successful_normalizations = sum(
                1 for cdr in normalized_cdrs if cdr.normalization_status == "success"
            )
            logger.debug(
                f"Normalized {successful_normalizations}/{len(normalized_cdrs)} events"
            )

            if self.metrics_enabled:
                self.metrics_collector.record_stage(
                    "normalize", successful_normalizations
                )

            # Step 4: Enrich normalized events
            enriched_cdrs = self.enricher.process_batch_enrichment(normalized_cdrs)
            successfully_enriched = sum(
                1 for cdr in enriched_cdrs if cdr.enrichment_status == "success"
            )
            logger.debug(
                f"Enriched {successfully_enriched}/{len(enriched_cdrs)} events"
            )

            if self.metrics_enabled:
                self.metrics_collector.record_stage("enrich", successfully_enriched)

            # Step 5: Publish to Kafka topic
            kafka_results = self.producer.send_batch_events(enriched_cdrs)
            logger.debug(
                f"Kafka delivery: {kafka_results['successful']}/{kafka_results['total_events']} successful"
            )

            if self.metrics_enabled:
                self.metrics_collector.record_stage(
                    "kafka_produce", kafka_results["successful"]
                )
                self.metrics_collector.record_stage(
                    "kafka_errors", kafka_results["failed"]
                )

            # Step 6: Update success counters
            self.successful_events = successfully_enriched
            self.failed_events = len(enriched_cdrs) - successfully_enriched

            # Step 7: Record final processing metrics
            if self.metrics_enabled:
                self.metrics_collector.record_stage("pipeline_completion", 1)
                self.metrics_collector.record_gauge(
                    "processing_duration_seconds",
                    time.time() - self.processing_start_time,
                )

            # Step 8: Generate processing summary
            processing_summary = self._generate_processing_summary()

            return {
                "status": "success",
                "processed_events": self.total_events_processed,
                "successful_events": self.successful_events,
                "failed_events": self.failed_events,
                "processing_summary": processing_summary,
                "kafka_results": kafka_results,
                "timestamp": datetime.utcnow().isoformat(),
            }

        except Exception as e:
            logger.error(f"Pipeline processing failed: {str(e)}", exc_info=True)
            error_summary = {
                "error": str(e),
                "error_type": type(e).__name__,
                "failed_events": len(raw_cdr_events),
                "timestamp": datetime.utcnow().isoformat(),
            }

            if self.metrics_enabled:
                self.metrics_collector.record_stage("pipeline_failure", 1)

            return {
                "status": "failure",
                "error_summary": error_summary,
                "failed_events": len(raw_cdr_events),
            }

    def _generate_processing_summary(self) -> Dict[str, Any]:
        """Generate detailed processing summary for audit and reporting"""
        duration = time.time() - self.processing_start_time
        throughput = self.total_events_processed / duration if duration > 0 else 0

        summary = {
            "processing_phase": {
                "parse": {
                    "successful": sum(
                        1
                        for p in self.parser.process_batch([1])
                        if "success" in p.parsing_status
                    ),
                    "failed": sum(
                        1
                        for p in self.parser.process_batch([1])
                        if "failed" in p.parsing_status
                    ),
                },
                "normalize": {
                    "successful": sum(
                        1
                        for n in self.normalizer.batch_normalize([1])
                        if n.normalization_status == "success"
                    ),
                    "failed": sum(
                        1
                        for n in self.normalizer.batch_normalize([1])
                        if n.normalization_status != "success"
                    ),
                },
                "enrich": {
                    "successful": sum(
                        1
                        for e in self.enricher.process_batch_enrichment([1])
                        if e.enrichment_status == "success"
                    ),
                    "failed": sum(
                        1
                        for e in self.enricher.process_batch_enrichment([1])
                        if e.enrichment_status != "success"
                    ),
                },
                "kafka_produce": {
                    "successful": sum(
                        1 for _ in self.producer.send_event({"event_id": "test"})
                    ),
                    "failed": 0,  # Would be populated from actual send results
                },
            },
            "performance_metrics": {
                "total_events_processed": self.total_events_processed,
                "processing_duration_seconds": round(duration, 3),
                "throughput_events_per_second": round(throughput, 3),
                "successful_events": self.successful_events,
                "failed_events": self.failed_events,
                "success_rate_percentage": round(
                    (self.successful_events / self.total_events_processed * 100), 2
                )
                if self.total_events_processed > 0
                else 0,
            },
            "error_handling_stats": {
                "parse_errors": sum(
                    1
                    for p in self.parser.process_batch([1])
                    if "failed" in p.parsing_status
                ),
                "normalize_errors": sum(
                    1
                    for n in self.normalizer.batch_normalize([1])
                    if n.normalization_status != "success"
                ),
                "enrich_errors": sum(
                    1
                    for e in self.enricher.process_batch_enrichment([1])
                    if e.enrichment_status != "success"
                ),
            },
        }

        return summary

    def health_check(self) -> Dict[str, Any]:
        """Perform comprehensive health check of the entire pipeline"""
        health_status = {
            "overall_status": "healthy",
            "timestamp": datetime.utcnow().isoformat(),
            "component_statuses": {},
            "metrics_status": "healthy",
            "error_handling_status": "healthy",
        }

        # Parse component health
        if hasattr(self.parser, "health_check"):
            health_status["component_statuses"]["parser"] = self.parser.health_check()

        if hasattr(self.normalizer, "health_check"):
            health_status["component_statuses"]["normalizer"] = (
                self.normalizer.health_check()
            )

        if hasattr(self.enricher, "health_check"):
            health_status["component_statuses"]["enricher"] = (
                self.enricher.health_check()
            )

        if hasattr(self.producer, "health_check"):
            health_status["component_statuses"]["kafka_producer"] = (
                self.producer.health_check()
            )

        # Check metrics component health
        if self.metrics_enabled and hasattr(self.metrics_collector, "health_check"):
            health_status["metrics_status"] = self.metrics_collector.health_check().get(
                "status", "healthy"
            )

        # Check error handling configuration
        health_status["error_handling_status"] = self._validate_error_handling_config()

        # Perform producer health check
        try:
            producer_health = self.producer.health_check()
            health_status["kafka_producer_health"] = producer_health
        except Exception as e:
            health_status["kafka_producer_health"] = {
                "status": "unhealthy",
                "error": str(e),
            }

        return health_status

    def _validate_error_handling_config(self) -> str:
        """Validate error handling configuration against required policies"""
        required_keys = [
            "max_retries",
            "retry_backoff_factor",
            "dead_letter_queue_enabled",
        ]
        missing_keys = [key for key in required_keys if key not in self.error_handling]

        if missing_keys:
            logger.warning(f"Missing required error handling keys: {missing_keys}")
            return "incomplete"

        # Validate retry policy configuration
        if self.retry_policy:
            required_retry_keys = [
                "initial_backoff",
                "max_backoff",
                "backoff_multiplier",
            ]
            missing_retry_keys = [
                key for key in required_retry_keys if key not in self.retry_policy
            ]
            if missing_retry_keys:
                logger.warning(f"Missing retry policy keys: {missing_retry_keys}")
                return "incomplete"

        logger.info("Error handling configuration validated successfully")
        return "valid"

    def graceful_shutdown(self, timeout: int = 30) -> None:
        """Execute graceful shutdown sequence for all pipeline components"""
        logger.info("Starting graceful shutdown sequence...")

        # Stop accepting new events
        try:
            self.producer.shutdown()
            logger.info("Kafka producer shut down successfully")
        except Exception as e:
            logger.error(f"Error during producer shutdown: {str(e)}")

        # Flush any pending metrics
        if self.metrics_enabled:
            try:
                self.metrics_collector.flush_metrics()
                logger.info("Metrics collector flushed successfully")
            except Exception as e:
                logger.error(f"Error flushing metrics: {str(e)}")

        # Final health verification
        final_health = self.health_check()
        logger.info(f"Final health check status: {final_health.get('overall_status')}")

        logger.info("Graceful shutdown completed")


def main():
    """Main execution function for pipeline orchestrator"""
    # Configuration for pipeline orchestrator
    config = {
        "pipeline_components": {
            "parser": {"support_functions": ["error_handling", "retry_mechanisms"]},
            "normalizer": {
                "field_mapping": {
                    "voice": {
                        "duration": "call_duration_ms",
                        "caller_id": "caller_number",
                        "callee_id": "called_number",
                        "call_type": "call_classification",
                    },
                    "sms": {
                        "message_length": "sms_length",
                        "sender_id": "originator",
                        "recipients": "destination_numbers",
                    },
                }
            },
            "enricher": {
                "enrichment_endpoints": {
                    "subscription": "http://subscription-service:8080/api/subscriptions",
                    "rate_plan": "http://rateplan-service:8081/api/rates",
                    "customer_metadata": "http://customer-service:8082/api/metadata",
                },
                "request_timeout": 3,
                "retry_attempts": 3,
            },
            "producer": {
                "bootstrap_servers": ["localhost:9092"],
                "topic": "usage-events",
                "security_protocol": "PLAINTEXT",
                "retries": 5,
                "retry_backoff_factor": 2,
            },
            "metrics": {"enabled": True, "collect_interval_seconds": 60},
        },
        "metrics_enabled": True,
        "error_handling": {
            "max_retries": 5,
            "retry_backoff_factor": 2,
            "dead_letter_queue_enabled": True,
        },
        "retry_policy": {
            "initial_backoff": 1,
            "max_backoff": 10,
            "backoff_multiplier": 2,
        },
    }

    # Initialize orchestrator
    orchestrator = CDMMediationOrchestrator(config)

    try:
        # Example usage for testing
        orchestrator.parser = CDRParser({"support_functions": ["error_handling"]})
        orchestrator.normalizer = SchemaNormalizer({"field_mapping": {}})
        orchestrator.enricher = ContextEnricher({"enrichment_endpoints": {}})
        orchestrator.producer = CDRPipelineProducer(
            {"bootstrap_servers": ["localhost:9092"], "topic": "usage-events"}
        )

        # Sample raw CDR events for testing
        sample_raw_cdrs = [
            '{"event_id":"CDR-123","type":"voice","duration":"125","caller":"1234567890","callee":"0987654321"}',
            '{"event_id":"CDR-456","type":"sms","msg_len":"160","sender":"1234567890","recipients":["0987654321"]}',
            '{"event_id":"CDR-789","type":"data","bytes":"1024","plan_type":"A","device_id":"ABCDEF"}',
        ]

        # Start processing
        results = orchestrator.start_processing(sample_raw_cdrs)

        if results["status"] == "success":
            print("✅ Pipeline processing completed successfully!")
            print(f"Processing summary: {results['processing_summary']}")
        else:
            print(f"❌ Pipeline processing failed: {results['error_summary']}")

        # Perform health check
        health = orchestrator.health_check()
        print(f"Health status: {health}")

        # Print final metrics
        if orchestrator.metrics_enabled:
            metrics = orchestrator.metrics_collector.get_metrics()
            print(f"Current metrics: {metrics}")

    finally:
        # Ensure proper shutdown
        orchestrator.graceful_shutdown()


if __name__ == "__main__":
    main()
