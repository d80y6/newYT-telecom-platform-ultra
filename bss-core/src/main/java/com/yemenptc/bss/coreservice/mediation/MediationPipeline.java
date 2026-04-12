package com.yemenptc.bss.coreservice.mediation;

import com.yemenptc.bss.coreservice.entity.UsageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediationPipeline {

    private final CdrParser cdrParser;
    private final CdrDeduplicator cdrDeduplicator;
    private final CdrEnricher cdrEnricher;
    private final KafkaTemplate<String, UsageEvent> kafkaTemplate;
    
    private static final String USAGE_EVENTS_TOPIC = "usage.events";
    private static final String CDR_DEAD_LETTER_TOPIC = "events.dlq";

    public MediationResult processCdr(String rawCdr) {
        MediationResult result = new MediationResult();
        
        if (!cdrParser.validateCdr(rawCdr)) {
            log.warn("Invalid CDR format, sending to DLQ");
            result.setValid(false);
            result.setErrorMessage("Invalid CDR format");
            sendToDeadLetterQueue(rawCdr, "INVALID_FORMAT");
            return result;
        }
        
        List<UsageEvent> events = new ArrayList<>();
        
        if (rawCdr.trim().startsWith("{")) {
            cdrParser.parseJsonCdr(rawCdr).ifPresent(events::add);
        } else if (rawCdr.contains(",")) {
            events = cdrParser.parseCsvCdr(rawCdr);
        }
        
        if (events.isEmpty()) {
            log.warn("No events parsed from CDR");
            result.setValid(false);
            result.setErrorMessage("No events parsed");
            sendToDeadLetterQueue(rawCdr, "PARSE_ERROR");
            return result;
        }
        
        result.setTotalParsed(events.size());
        
        events = cdrDeduplicator.deduplicate(events);
        result.setAfterDeduplication(events.size());
        result.setDuplicatesRemoved(result.getTotalParsed() - events.size());
        
        List<UsageEvent> enrichedEvents = new ArrayList<>();
        for (UsageEvent event : events) {
            try {
                UsageEvent enriched = cdrEnricher.enrich(event);
                enrichedEvents.add(enriched);
            } catch (Exception e) {
                log.error("Failed to enrich event {}: {}", event.getEventId(), e.getMessage());
                result.incrementFailed();
            }
        }
        
        result.setAfterEnrichment(enrichedEvents.size());
        
        int published = 0;
        int failed = 0;
        
        for (UsageEvent event : enrichedEvents) {
            try {
                publishToKafka(event);
                published++;
            } catch (Exception e) {
                log.error("Failed to publish event {}: {}", event.getEventId(), e.getMessage());
                failed++;
                result.incrementFailed();
            }
        }
        
        result.setPublishedToKafka(published);
        result.setValid(true);
        
        log.info("Mediation complete: parsed={}, deduped={}, enriched={}, published={}, failed={}", 
                result.getTotalParsed(),
                result.getTotalParsed() - result.getAfterDeduplication(),
                result.getAfterEnrichment(),
                result.getPublishedToKafka(),
                result.getFailed());
        
        return result;
    }

    public List<MediationResult> processBatch(List<String> rawCdrs) {
        List<MediationResult> results = new ArrayList<>();
        
        for (String rawCdr : rawCdrs) {
            try {
                results.add(processCdr(rawCdr));
            } catch (Exception e) {
                log.error("Failed to process CDR in batch: {}", e.getMessage());
                MediationResult errorResult = new MediationResult();
                errorResult.setValid(false);
                errorResult.setErrorMessage("Batch processing error: " + e.getMessage());
                results.add(errorResult);
            }
        }
        
        return results;
    }

    private void publishToKafka(UsageEvent event) {
        String key = event.getSubscriptionId() != null ? 
                event.getSubscriptionId().toString() : event.getEventId();
        
        CompletableFuture<SendResult<String, UsageEvent>> future = 
                kafkaTemplate.send(USAGE_EVENTS_TOPIC, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send event {} to Kafka: {}", 
                        event.getEventId(), ex.getMessage());
            } else {
                log.debug("Event {} sent to partition {} offset {}", 
                        event.getEventId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    private void sendToDeadLetterQueue(String rawCdr, String reason) {
        try {
            kafkaTemplate.send(CDR_DEAD_LETTER_TOPIC, "dlq:" + reason, rawCdr);
        } catch (Exception e) {
            log.error("Failed to send to DLQ: {}", e.getMessage());
        }
    }

    public static class MediationResult {
        private boolean valid;
        private String errorMessage;
        private int totalParsed;
        private int afterDeduplication;
        private int duplicatesRemoved;
        private int afterEnrichment;
        private int publishedToKafka;
        private int failed;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public int getTotalParsed() { return totalParsed; }
        public void setTotalParsed(int totalParsed) { this.totalParsed = totalParsed; }
        public int getAfterDeduplication() { return afterDeduplication; }
        public void setAfterDeduplication(int afterDeduplication) { this.afterDeduplication = afterDeduplication; }
        public int getDuplicatesRemoved() { return duplicatesRemoved; }
        public void setDuplicatesRemoved(int duplicatesRemoved) { this.duplicatesRemoved = duplicatesRemoved; }
        public int getAfterEnrichment() { return afterEnrichment; }
        public void setAfterEnrichment(int afterEnrichment) { this.afterEnrichment = afterEnrichment; }
        public int getPublishedToKafka() { return publishedToKafka; }
        public void setPublishedToKafka(int publishedToKafka) { this.publishedToKafka = publishedToKafka; }
        public int getFailed() { return failed; }
        public void setFailed(int failed) { this.failed = failed; }
        public void incrementFailed() { this.failed++; }
    }
}
