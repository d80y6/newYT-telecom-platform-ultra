package com.yemenptc.bss.coreservice.charging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yemenptc.bss.coreservice.entity.BillingOutboxEntity;
import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.kafka.EventProducer;
import com.yemenptc.bss.coreservice.repository.BillingOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingOutboxPoller {

    private final BillingOutboxRepository billingOutboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String BILLING_EVENTS_TOPIC = "billing.events";
    private static final int MAX_RETRIES = 3;
    private static final long POLL_INTERVAL_MS = 5000;
    private static final long SEND_TIMEOUT_MS = 10000;

    @Scheduled(fixedDelay = POLL_INTERVAL_MS)
    @Transactional
    public void pollOutbox() {
        List<BillingOutboxEntity> pendingItems = billingOutboxRepository
            .findByStatusAndRetryCountLessThan(BillingOutboxEntity.OutboxStatus.PENDING, MAX_RETRIES);

        if (pendingItems.isEmpty()) {
            return;
        }

        log.info("Polling outbox: found {} pending items", pendingItems.size());

        for (BillingOutboxEntity outboxItem : pendingItems) {
            processOutboxItem(outboxItem);
        }
    }

    private void processOutboxItem(BillingOutboxEntity outboxItem) {
        try {
            log.debug("Processing outbox item: {}", outboxItem.getId());

            Object payload = deserializePayload(outboxItem.getPayload());

            SendResult<String, Object> result = kafkaTemplate
                .send(outboxItem.getTopic(), outboxItem.getEventId(), payload)
                .get(SEND_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            markAsCompleted(outboxItem);

            log.info("Successfully sent outbox item {} to Kafka topic {} partition {} offset {}",
                outboxItem.getId(),
                outboxItem.getTopic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending outbox item: {}", outboxItem.getId());
            markAsFailed(outboxItem, "Interrupted: " + e.getMessage());
        } catch (ExecutionException e) {
            log.error("Kafka send failed for outbox item {}: {}", outboxItem.getId(), e.getCause().getMessage());
            incrementRetry(outboxItem, "Execution failed: " + e.getCause().getMessage());
        } catch (TimeoutException e) {
            log.error("Kafka send timed out for outbox item {}: {}", outboxItem.getId(), e.getMessage());
            incrementRetry(outboxItem, "Timeout: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing outbox item {}: {}", outboxItem.getId(), e.getMessage(), e);
            incrementRetry(outboxItem, "Unexpected error: " + e.getMessage());
        }
    }

    private Object deserializePayload(String payload) throws Exception {
        return objectMapper.readValue(payload, Object.class);
    }

    private void markAsCompleted(BillingOutboxEntity outboxItem) {
        outboxItem.setStatus(BillingOutboxEntity.OutboxStatus.COMPLETED);
        outboxItem.setProcessedAt(Instant.now());
        billingOutboxRepository.save(outboxItem);
    }

    private void incrementRetry(BillingOutboxEntity outboxItem, String errorMessage) {
        outboxItem.setRetryCount(outboxItem.getRetryCount() + 1);
        outboxItem.setErrorMessage(errorMessage);

        if (outboxItem.getRetryCount() >= MAX_RETRIES) {
            outboxItem.setStatus(BillingOutboxEntity.OutboxStatus.FAILED);
            log.error("Outbox item {} exceeded max retries, marking as FAILED", outboxItem.getId());
        }

        billingOutboxRepository.save(outboxItem);
    }

    private void markAsFailed(BillingOutboxEntity outboxItem, String errorMessage) {
        outboxItem.setStatus(BillingOutboxEntity.OutboxStatus.FAILED);
        outboxItem.setErrorMessage(errorMessage);
        outboxItem.setProcessedAt(Instant.now());
        billingOutboxRepository.save(outboxItem);
    }
}
