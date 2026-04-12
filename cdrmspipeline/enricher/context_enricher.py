#!/usr/bin/env python3
"""
Context Enricher Module - Adds additional context to normalized CDR events
Enriches events with subscription details, rate plans, customer metadata using existing APIs
"""

import logging
import json
import time
from typing import Dict, Any, Optional
from dataclasses import dataclass
from datetime import datetime
import requests

logger = logging.getLogger('ContextEnricher')

@dataclass
class EnrichedCDR:
    """CDR structure after enrichment"""
    normalized_cdr: Dict[str, Any]
    enrichment_metadata: Dict[str, Any]
    enrichment_status: str = "success"
    enrichment_timestamp: datetime = datetime.utcnow()

class ContextEnricher:
    """Enriches normalized CDR events with contextual data from existing services"""
    
    def __init__(self, config: Dict[str, Any]):
        self.config = config
        self.enrichment_endpoints = config.get('enrichment_endpoints', {})
        self.timeout = config.get('request_timeout', 5)
        self.retry_attempts = config.get('retry_attempts', 3)
        self.retry_delay = config.get('retry_delay', 1)
    
    def enrich_cdr(self, normalized_cdr: Dict[str, Any]) -> EnrichedCDR:
        """Main enrichment method - adds contextual data to CDR"""
        enriched_fields = {}
        metadata = {}
        
        try:
            # 1. Add Subscription Details
            enriched_fields['subscription_details'] = self._get_subscription_details(
                normalized_cdr.get('user_id'))
            
            # 2. Add Rate Plan Information
            enriched_fields['rate_plan'] = self._get_rate_plan_details(
                enriched_fields['subscription_details'])
            
            # 3. Add Customer Metadata
            enriched_fields['customer_metadata'] = self._get_customer_metadata(
                normalized_cdr.get('user_id'))
            
            # 4. Add Service Context
            enriched_fields['service_context'] = self._get_service_context(
                enriched_fields)
            
            # 5. Add Geographic Context
            enriched_fields['geographic_context'] = self._get_geographic_context(
                enriched_fields)
            
            metadata['enrichment_success'] = True
            
        except Exception as e:
            logger.error(f"Enrichment failed: {str(e)}", exc_info=True)
            enriched_fields['enrichment_error'] = str(e)
            enriched_fields['subscription_details'] = {}
            enriched_fields['rate_plan'] = {}
            enriched_fields['customer_metadata'] = {}
            enriched_fields['service_context'] = {}
            enriched_fields['geographic_context'] = {}
            enriched_fields['enrichment_success'] = False
            enriched_fields['enrichment_status'] = f"failed: {str(e)}"
        
        enrichment_metadata = {
            'retry_count': 0,
            'last_retry_delay': self.retry_delay,
            'enrichment_endpoints_used': list(self.enrichment_endpoints.keys()),
            'final_enrichment_status': 'success' if all([
                enriched_fields.get('subscription_details'),
                enriched_fields.get('rate_plan'),
                enriched_fields.get('customer_metadata')
            ]) else 'partial'
        }
        
        return EnrichedCDR(
            normalized_cdr=normalized_cdr,
            enrichment_metadata=enrichment_metadata,
            enrichment_status=enrichment_metadata['final_enrichment_status'],
            enrichment_timestamp=datetime.utcnow()
        }
    
    def _get_subscription_details(self, user_id: str) -> Dict[str, Any]:
        """Retrieve subscription plan details for user"""
        try:
            # Integration with existing Subscription Service
            endpoint = self.enrichment_endpoints.get('subscription')
            if endpoint:
                response = requests.get(
                    f"{endpoint}/{user_id}",
                    timeout=self.timeout
                )
                if response.status_code == 200:
                    return response.json()
        except Exception as e:
            logger.warning(f"Subscription lookup failed for {user_id}: {str(e)}")
        
        # Return default values if lookup fails
        return {
            'subscription_id': 'DEFAULT',
            'plan_type': 'BASE',
            'effective_date': '2023-01-01',
            'expiry_date': '2024-12-31',
            'features': ['voice', 'sms', 'data'],
            'monthly_allocated': {
                'voice_minutes': 500,
                'sms_count': 100,
                'data_gb': 5
            }
        }
    
    def _get_rate_plan_details(self, subscription_details: Dict[str, Any]) -> Dict[str, Any]:
        """Retrieve rate plan pricing details"""
        try:
            endpoint = self.enrichment_endpoints.get('rate_plan')
            if endpoint:
                # In production, this would use rate plan lookup
                rate_plan = subscription_details.get('rate_plan', 'BASE')
                # Mock rate plan lookup based on plan type
                rate_plan_map = {
                    'PREMIUM': {'base_rate': 0.08, 'currency': 'USD'},
                    'STANDARD': {'base_rate': 0.05, 'currency': 'USD'},
                    'BASE': {'base_rate': 0.03, 'currency': 'USD'}
                }
                plan_info = rate_plan_map.get(rate_plan)
                if plan_info:
                    return {
                        'plan_type': rate_plan,
                        'base_rate': rate_info['base_rate'],
                        'currency': rate_info['currency'],
                        'tier': 'MEDIUM' if rate_plan == 'STANDARD' else 'LOW'
                    }
        except Exception as e:
            logger.warning(f"Rate plan lookup failed: {str(e)}")
        
        return {
            'plan_type': 'BASE',
            'base_rate': 0.03,
            'currency': 'USD',
            'tier': 'LOW'
        })
    
    def _get_customer_metadata(self, user_id: str) -> Dict[str, Any]:
        """Retrieve customer demographic and usage metadata"""
        try:
            endpoint = self.enrichment_endpoints.get('customer_metadata')
            if endpoint:
                response = requests.get(
                    f"{endpoint}/metadata/{user_id}",
                    timeout=self.timeout
                )
                if response.status_code == 200:
                    return response.json()
        except Exception as e:
            logger.warning(f"Customer metadata lookup failed for {user_id}: {str(e)}")
        
        # Return default demographic data
        birth_year = 1990  # Default for testing
        return {
            'user_id': user_id,
            'demographics': {
                'age': 30,  # Default age calculation: 2023 - birth_year
                'gender': 'unknown',
                'location': 'UNKNOWN',
                'device_type': 'smartphone'
            },
            'usage_pattern': {
                'average_calls_per_month': 120,
                'average_data_usage_gb': 3.5,
                'peak_usage_hours': '18-22'
            }
        }
    
    def _get_service_context(self, normalized_cdr: Dict[str, Any]) -> Dict[str, Any]:
        """Add service-specific context to CDR"""
        service_type = normalized_cdr.get('service_type', 'unknown')
        
        # Service-specific context additions
        context_map = {
            'voice': {
                'call_direction': 'OUTGOING',
                'call_duration_seconds': int(normalized_cdr.get('call_duration_ms', 0) / 1000),
                'is_international': normalized_cdr.get('is_international', False)
            },
            'sms': {
                'message_count': 1,
                'is_mms': False
            },
            'data': {
                'is_streaming': normalized_cdr.get('is_streaming', False),
                'content_type': normalized_cdr.get('content_type', 'generic')
            },
            'roaming': {
                'roaming_country': normalized_cdr.get('country'),
                'visited_network': normalized_cdr.get('visited_network')
            }
        }
        
        return context_map.get(service_type, {})
    
    def _get_geographic_context(self, enriched_fields: Dict[str, Any]) -> Dict[str, Any]:
        """Add geographic context based on service usage"""
        # In production, this would use geo-IP lookup or billing address
        return {
            'country': enriched_fields.get('subscription_details', {}).get('location', 'UNKNOWN'),
            'region': 'REGION_UNKNOWN',
            'timezone': 'UTC'
        }
    
    def process_batch_enrichment(self, normalized_cdrs: List[Dict[str, Any]]) -> List[EnrichedCDR]:
        """Process batch of normalized CDRs for enrichment"""
        enriched_cdrs = []
        batch_retry_count = 0
        
        for cdr in normalized_cdrs:
            enriched_cdr = self.enrich_cdr(cdr)
            enriched_cdrs.append(enriched_cdr)
            
            # Track batch retry status for metrics
            if enriched_cdr.enrichment_status == 'success':
                batch_retry_count += 1
        
        logger.info(f"Batch enrichment completed. {batch_retry_count}/{len(normalized_cdrs)} enriched successfully")
        return enriched_cdrs
    
    def health_check(self) -> Dict[str, Any]:
        """Health check for enrichment service"""
        return {
            'status': 'healthy',
            'timestamp': datetime.utcnow().isoformat(),
            'service': 'context_enricher',
            'dependencies': list(self.enrichment_endpoints.keys()),
            'uptime_seconds': int(time.time() - self.start_time) if hasattr(self, 'start_time') else 0
        }

# Example usage for testing
if __name__ == "__main__":
    # Configuration for enrichment service
    config = {
        'enrichment_endpoints': {
            'subscription': 'http://subscription-service:8080/api/subscriptions',
            'rate_plan': 'http://rateplan-service:8081/api/rates',
            'customer_metadata': 'http://customer-service:8082/api/metadata'
        },
        'request_timeout': 3,
        'retry_attempts': 3,
        'retry_delay': 1
    }
    
    # Initialize enrichment service
    enricher = ContextEnricher(config)
    
    # Sample normalized CDR for testing
    sample_normalized_cdr = {
        'event_id': 'CDR-123456',
        'normalized_timestamp': '2023-05-01T10:00:00',
        'user_id': 'USER-7890',
        'service_type': 'voice',
        'standardized_fields': {
            'call_duration_ms': '125',
            'caller_number': '1234567890',
            'called_number': '0987654321',
            'call_classification': 'OUTGOING'
        },
        'metadata': {}
    }
    
    # Enrich the sample CDR
    enriched_cdr = enricher.enrich_cdr(sample_normalized_cdr)
    
    if enriched_cdr.enrichment_status == 'success':
        print("Enrichment successful!")
        print(f"Enriched CDR: {enriched_cdr}")
    else:
        print(f"Enrichment failed: {enriched_cdr.enrichment_status}")