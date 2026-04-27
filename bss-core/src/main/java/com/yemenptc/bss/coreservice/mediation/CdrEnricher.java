package com.yemenptc.bss.coreservice.mediation;

import com.yemenptc.bss.coreservice.entity.Subscription;
import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.entity.Customer;
import com.yemenptc.bss.coreservice.repository.SubscriptionRepository;
import com.yemenptc.bss.coreservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CdrEnricher {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;

    public UsageEvent enrich(UsageEvent event) {
        if (event.getSubscriptionId() == null && event.getSourceSystem() != null) {
            enrichFromSubscriberIdentifier(event);
        }
        
        if (event.getSubscriptionId() != null) {
            enrichFromSubscription(event);
        }
        
        enrichWithDefaults(event);
        
        return event;
    }

    private void enrichFromSubscriberIdentifier(UsageEvent event) {
        String identifier = event.getSourceSystem();
        
        List<Subscription> subscriptions = subscriptionRepository
                .findByServiceIdentifier(identifier);
        
        if (subscriptions.isEmpty()) {
            Optional<Customer> customer = customerRepository
                    .findByPrimaryPhone(identifier);
            
            if (customer.isPresent()) {
                var customerObj = customer.get();
                log.info("Found customer {} for identifier {}", 
                        customerObj.getExternalId(), identifier);
            }
        } else {
            Subscription subscription = subscriptions.get(0);
            event.setSubscriptionId(UUID.fromString(subscription.getId()));
            event.setCustomerId(subscription.getCustomerId());
        }
    }

    private void enrichFromSubscription(UsageEvent event) {
        String subId = event.getSubscriptionId().toString();
        
        Optional<Subscription> subscription = subscriptionRepository.findById(subId);
        
        if (subscription.isPresent()) {
            Subscription sub = subscription.get();
            
            if (sub.getServiceType() != null && 
                (event.getServiceType() == null || event.getServiceType().equals("USAGE"))) {
                event.setServiceType(mapServiceType(sub.getServiceType()));
            }
            
            if (sub.getStatus() == Subscription.SubscriptionStatus.SUSPENDED) {
                log.warn("CDR for suspended subscription {}: {}", 
                        subId, event.getEventId());
            }
            
            if (sub.getStatus() == Subscription.SubscriptionStatus.TERMINATED) {
                log.warn("CDR for terminated subscription {}: {}", 
                        subId, event.getEventId());
            }
        } else {
            log.warn("Subscription not found for CDR: {}", subId);
        }
    }

    private void enrichWithDefaults(UsageEvent event) {
        if (event.getUsageValue() == null) {
            event.setUsageValue(BigDecimal.ONE);
        }
        
        if (event.getEventTime() == null) {
            event.setEventTime(Instant.now());
        }
        
        if (event.getServiceType() == null) {
            event.setServiceType("USAGE");
        }
        
        if (event.getEventType() == null) {
            event.setEventType("USAGE");
        }
        
        if (event.getSourceSystem() == null) {
            event.setSourceSystem("CDR_ENRICHER");
        }
    }

    private String mapServiceType(String subscriptionServiceType) {
        if (subscriptionServiceType == null) {
            return "USAGE";
        }
        
        switch (subscriptionServiceType.toUpperCase()) {
            case "VOICE": case "TEL": case "PSTN":
                return "VOICE";
            case "SMS": case "MESSAGING":
                return "SMS";
            case "DATA": case "INTERNET": case "BROADBAND":
                return "DATA";
            case "MOBILE": case "CELLULAR":
                return "DATA";
            case "ADSL": case "VDSL":
                return "ADSL";
            case "FTTH": case "FIBER":
                return "FTTH";
            case "LTE": case "4G": case "5G":
                return "DATA";
            default:
                return "USAGE";
        }
    }

    public Optional<String> resolveCustomerId(UsageEvent event) {
        if (event.getSubscriptionId() == null) {
            return Optional.empty();
        }
        
        return subscriptionRepository.findById(event.getSubscriptionId().toString())
                .map(Subscription::getCustomerId);
    }

    public Optional<String> resolveAccountId(UsageEvent event) {
        if (event.getSubscriptionId() == null) {
            return Optional.empty();
        }
        
        return subscriptionRepository.findById(event.getSubscriptionId().toString())
                .map(Subscription::getAccountId);
    }
}
