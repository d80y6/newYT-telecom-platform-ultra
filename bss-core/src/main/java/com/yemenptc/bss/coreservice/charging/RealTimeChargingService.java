package com.yemenptc.bss.coreservice.charging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yemenptc.bss.coreservice.entity.BillingOutboxEntity;
import com.yemenptc.bss.coreservice.entity.ConvergentBillingAccount;
import com.yemenptc.bss.coreservice.entity.Invoice;
import com.yemenptc.bss.coreservice.entity.RatingRecord;
import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.kafka.EventProducer;
import com.yemenptc.bss.coreservice.ocs.BalanceService;
import com.yemenptc.bss.coreservice.rating.RatingEngine;
import com.yemenptc.bss.coreservice.rating.RatingEngine.RatingResult;
import com.yemenptc.bss.coreservice.repository.BillingOutboxRepository;
import com.yemenptc.bss.coreservice.repository.ConvergentBillingAccountRepository;
import com.yemenptc.bss.coreservice.repository.RatingRecordRepository;
import com.yemenptc.bss.coreservice.repository.UsageEventRepository;
import com.yemenptc.bss.coreservice.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeChargingService {

    private final RatingEngine ratingEngine;
    private final BillingService billingService;
    private final RatingRecordRepository ratingRecordRepository;
    private final ConvergentBillingAccountRepository billingAccountRepository;
    private final BalanceService balanceService;
    private final UsageEventRepository usageEventRepository;
    private final BillingOutboxRepository billingOutboxRepository;
    private final ObjectMapper objectMapper;

    private static final String BILLING_EVENTS_TOPIC = "billing.events";
    private static final String USAGE_RECORDED_EVENT_TYPE = "usage.recorded";

    @Transactional
    public ChargingResult chargeUsageEvent(UsageEvent event) {
        log.info("Processing real-time charging for event: {}, subscription: {}",
            event.getId(), event.getSubscriptionId());

        try {
            UsageEvent savedEvent = usageEventRepository.save(event);

            RatingResult ratingResult = ratingEngine.rateUsageEvent(savedEvent);

            saveRatingRecord(ratingResult, savedEvent);

            UUID accountId = savedEvent.getCharacteristic("accountId");
            applyChargeToAccount(accountId, savedEvent.getSubscriptionId(), ratingResult);

            createBillingOutboxEntry(savedEvent, ratingResult);

            log.info("Charging complete: event={}, amount={}",
                savedEvent.getId(), ratingResult.getTotalAmount());

            return ChargingResult.success(
                savedEvent.getId().toString(),
                ratingResult.getChargedAmount(),
                ratingResult.getTotalAmount(),
                ratingResult.getCurrency()
            );

        } catch (Exception e) {
            log.error("Charging failed for event: {}, error: {}", event.getId(), e.getMessage(), e);
            return ChargingResult.failure(event.getId().toString(), e.getMessage());
        }
    }

    private void createBillingOutboxEntry(UsageEvent event, RatingResult ratingResult) {
        try {
            EventProducer.EventWrapper eventWrapper = new EventProducer.EventWrapper(
                USAGE_RECORDED_EVENT_TYPE,
                event
            );

            String payload = objectMapper.writeValueAsString(eventWrapper);

            BillingOutboxEntity outboxEntry = BillingOutboxEntity.builder()
                .eventId(event.getId().toString())
                .eventType(USAGE_RECORDED_EVENT_TYPE)
                .topic(BILLING_EVENTS_TOPIC)
                .payload(payload)
                .status(BillingOutboxEntity.OutboxStatus.PENDING)
                .build();

            billingOutboxRepository.save(outboxEntry);

            log.debug("Created billing outbox entry for event: {}", event.getId());
        } catch (Exception e) {
            log.error("Failed to create billing outbox entry for event {}: {}",
                event.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create billing outbox entry", e);
        }
    }

    private void saveRatingRecord(RatingResult result, UsageEvent event) {
        RatingRecord record = new RatingRecord();
        record.setEventId(event.getId().toString());
        record.setSubscriptionId(event.getSubscriptionId());
        record.setServiceType(event.getServiceType());
        record.setEventType(event.getEventType());
        record.setUsageValue(event.getUsageValue());
        record.setUsageUnit(event.getUsageUnit());
        record.setChargedAmount(result.getChargedAmount());
        record.setRatingStatus(RatingRecord.RatingStatus.RATED);
        record.setRatedAt(Instant.now());

        ratingRecordRepository.save(record);
        log.debug("Rating record saved for event: {}", event.getId());
    }

    private void applyChargeToAccount(UUID accountId, UUID subscriptionId, RatingResult ratingResult) {
        if (accountId == null || ratingResult.getTotalAmount() == null ||
            ratingResult.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("Skipping charge - no account or zero amount");
            return;
        }

        try {
            var billingAccountOpt = billingAccountRepository.findById(accountId);

            if (billingAccountOpt.isEmpty()) {
                log.warn("Billing account not found for id: {}, creating invoice", accountId);
                createPostpaidInvoice(accountId, ratingResult);
                return;
            }

            ConvergentBillingAccount billingAccount = billingAccountOpt.get();

            if (billingAccount.getBillingType() == ConvergentBillingAccount.BillingType.PREPAID) {
                log.info("Prepaid account {} - deducting {} {}",
                    accountId, ratingResult.getTotalAmount(), ratingResult.getCurrency());

                var balanceResult = balanceService.deductImmediate(
                    accountId.toString(),
                    ratingResult.getTotalAmount(),
                    "Usage: " + ratingResult.getServiceType()
                );

                if (!balanceResult.success()) {
                    log.error("Failed to deduct from prepaid balance: {}", balanceResult.message());
                    throw new RuntimeException("Insufficient balance or deduction failed: " + balanceResult.message());
                }

                log.info("Prepaid deduction successful: reservationId={}", balanceResult.reservationId());

            } else {
                log.info("Postpaid account {} - creating invoice for {} {}",
                    accountId, ratingResult.getTotalAmount(), ratingResult.getCurrency());

                createPostpaidInvoice(accountId, ratingResult);
            }

        } catch (Exception e) {
            log.error("Failed to apply charge to account {}: {}", accountId, e.getMessage(), e);
            throw new RuntimeException("Charge application failed: " + e.getMessage(), e);
        }
    }

    private void createPostpaidInvoice(UUID accountId, RatingResult ratingResult) {
        try {
            Invoice invoice = new Invoice();
            invoice.setAccountId(accountId);
            invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            invoice.setInvoiceDate(Instant.now());
            invoice.setDueDate(LocalDate.now().plusDays(30));
            invoice.setStatus(Invoice.InvoiceStatus.PENDING);
            invoice.setSubtotal(ratingResult.getChargedAmount());
            invoice.setTaxAmount(ratingResult.getTaxAmount());
            invoice.setTotalAmount(ratingResult.getTotalAmount());
            invoice.setCurrency(ratingResult.getCurrency());

            // Use InvoiceItem entity
            com.yemenptc.bss.coreservice.entity.InvoiceItem item = com.yemenptc.bss.coreservice.entity.InvoiceItem.builder()
                .id(java.util.UUID.randomUUID().toString())
                .description("Usage charge: " + ratingResult.getServiceType())
                .quantity(1)
                .unitPrice(ratingResult.getChargedAmount())
                .amount(ratingResult.getChargedAmount())
                .createdAt(java.time.Instant.now())
                .build();

            Invoice createdInvoice = billingService.createInvoice(invoice);
            log.info("Invoice created for usage: {}", createdInvoice.getId());

        } catch (Exception e) {
            log.error("Failed to create invoice: {}", e.getMessage(), e);
            throw new RuntimeException("Invoice creation failed: " + e.getMessage(), e);
        }
    }

    public record ChargingResult(
        boolean success,
        String eventId,
        BigDecimal chargedAmount,
        BigDecimal totalAmount,
        String currency,
        String errorMessage
    ) {
        public static ChargingResult success(String eventId, BigDecimal charged, BigDecimal total, String currency) {
            return new ChargingResult(true, eventId, charged, total, currency, null);
        }

        public static ChargingResult failure(String eventId, String error) {
            return new ChargingResult(false, eventId, null, null, null, error);
        }
    }
}
