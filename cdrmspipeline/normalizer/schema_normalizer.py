#!/usr/bin/env python3
"""
Schema Normalizer Module - Standardizes CDR event schemas across telecom domains
Ensures consistent data structure for downstream processing
"""

import logging
from typing import Dict, Any, Optional
from dataclasses import dataclass
from datetime import datetime

logger = logging.getLogger("SchemaNormalizer")


@dataclass
class NormalizedCDR:
    """Standardized CDR structure after normalization"""

    event_id: str
    normalized_timestamp: datetime
    user_id: str
    service_type: str
    standardized_fields: Dict[str, Any]
    metadata: Dict[str, Any]
    normalization_status: str = "success"


class SchemaNormalizer:
    """Normalizes CDR events to a unified schema format"""

    # Standard field mappings by service type
    FIELD_MAPPINGS = {
        "voice": {
            "duration": "call_duration_ms",
            "caller_id": "caller_number",
            "callee_id": "called_number",
            "call_type": "call_classification",
            "timestamp": "event_timestamp",
        },
        "sms": {
            "message_length": "sms_length",
            "sender_id": "originator",
            "recipients": "destination_numbers",
            "timestamp": "event_timestamp",
        },
        "data": {
            "bytes_used": "data_volume_bytes",
            "plan_type": "subscription_plan",
            "device_id": "device_identifier",
            "timestamp": "event_timestamp",
        },
        "roaming": {
            "data_used": "roaming_data_mb",
            "roaming_network": "visited_network",
            "roaming_type": "roaming_category",
            "timestamp": "event_timestamp",
        },
    }

    def __init__(self, config: Dict):
        self.config = config
        self.field_mapping = self.FIELD_MAPPINGS

    def normalize_cdr(self, parsed_cdr: Dict[str, Any]) -> NormalizedCDR:
        """Normalize parsed CDR event to standard schema"""
        service_type = parsed_cdr.get("service_type", "unknown")
        raw_data = parsed_cdr.get("raw_data", {})

        # Map raw fields to standardized names
        standardized_fields = {}
        metadata = {}

        # Apply field mappings
        if service_type in self.field_mapping:
            mappings = self.field_mapping[service_type]
            for raw_field, std_field in mappings.items():
                standardized_fields[std_field] = raw_data.get(raw_field)

        # Add universal fields
        standardized_fields["service_type"] = service_type
        standardized_fields["original_source"] = parsed_cdr.get(
            "original_source", "unknown"
        )
        standardized_fields["raw_event"] = parsed_cdr.get("raw_event", {})

        # Generate metadata
        metadata["normalization_version"] = "1.2.0"
        metadata["processing_timestamp"] = datetime.utcnow().isoformat()
        metadata["source_system"] = parsed_cdr.get("source_system", "CDR_INGEST")

        normalized_event = NormalizedCDR(
            event_id=parsed_cdr.get("event_id", ""),
            normalized_timestamp=datetime.fromisoformat(
                parsed_cdr.get("normalized_timestamp", datetime.utcnow().isoformat())
            ),
            user_id=parsed_cdr.get("user_id", ""),
            service_type=service_type,
            standardized_fields=standardized_fields,
            metadata=metadata,
            normalization_status="success",
        )

        logger.debug(f"Normalized CDR event {normalized_event.event_id} to schema v1.2")
        return normalized_event

    def validate_normalized_cdr(self, normalized_cdr: NormalizedCDR) -> bool:
        """Validate normalized CDR event for completeness and consistency"""
        # Check required fields
        required_fields = [
            "event_id",
            "normalized_timestamp",
            "user_id",
            "service_type",
            "standardized_fields",
        ]

        for field in required_fields:
            if not getattr(normalized_cdr, field, None):
                logger.error(f"Missing required field in normalized CDR: {field}")
                return False

        # Check standardized fields contain expected data types
        expected_types = {
            "call_duration_ms": (int, float),
            "caller_number": str,
            "called_number": str,
            "call_classification": str,
            "data_volume_bytes": int,
            "subscription_plan": str,
            "roaming_data_mb": float,
        }

        for field_name, expected_type in expected_types.items():
            if field_name in normalized_cdr.standardized_fields:
                field_value = normalized_cdr.standardized_fields[field_name]
                if not isinstance(field_value, expected_type):
                    logger.error(
                        f"Field {field_name} has incorrect type {type(field_value)}, expected {expected_type}"
                    )
                    return False

        return True

    def batch_normalize(self, parsed_cdrs: List[Dict[str, Any]]) -> List[NormalizedCDR]:
        """Normalize multiple CDR events in batch mode"""
        normalized_cdrs = []
        validation_results = []

        for parsed_cdr in parsed_cdrs:
            try:
                normalized_cdr = self.normalize_cdr(parsed_cdr)
                is_valid = self.validate_normalized_cdr(normalized_cdr)
                validation_results.append(is_valid)

                if is_valid:
                    normalized_cdrs.append(normalized_cdr)
                else:
                    logger.warning(
                        f"Invalid normalized CDR rejected: {normalized_cdr.event_id}"
                    )

            except Exception as e:
                logger.error(
                    f"Normalization failed for CDR {parsed_cdr.get('event_id')}: {str(e)}",
                    exc_info=True,
                )

        logger.info(
            f"Batch normalization completed. {len(normalized_cdrs)} valid events out of {len(parsed_cdrs)} total"
        )
        return normalized_cdrs


# Example usage for testing
if __name__ == "__main__":
    # Configuration for normalization
    config = {"support_functions": ["error_handling", "retry_mechanisms"]}

    # Initialize normalizer
    normalizer = SchemaNormalizer(config)

    # Sample parsed CDR for testing
    sample_parsed_cdr = {
        "event_id": "CDR-123456",
        "timestamp": "2023-05-01T10:00:00Z",
        "user_id": "USER-7890",
        "service_type": "voice",
        "raw_data": {
            "duration": "125",
            "caller_id": "1234567890",
            "callee_id": "0987654321",
            "call_type": "OUTGOING",
        },
        "source_system": "CDR_INGEST_SYSTEM",
    }

    # Normalize the sample CDR
    normalized = normalizer.normalize_cdr(sample_parsed_cdr)
    is_valid = normalizer.validate_normalized_cdr(normalized)

    if is_valid:
        print("Normalization successful!")
        print(f"Normalized CDR: {normalized.standardized_fields}")
    else:
        print("Normalization failed validation!")
