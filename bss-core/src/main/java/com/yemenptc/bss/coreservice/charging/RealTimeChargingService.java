package com.yemenptc.bss.coreservice.charging;

import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.rating.RatingEngine;
import com.yemenptc.bss.coreservice.rating.RatingEngine.RatingResult;
import com.yemenptc.bss.coreservice.repository.RatingRecordRepository;
import com.yemenptc.bss.coreservice.service.BillingService;
import com.yemenptc.bss.coreservice.entity.RatingRecord;
import com.yemenptc.bss.coreservice.entity.Invoice;
import com.yemenptc.bss.coreservice.entity.ConvergentBillingAccount;
import com.yemenptc.bss.coreservice.repository.ConvergentBillingAccountRepository;
import com.yemenptc.bss.coreservice.ocs.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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

    @Transactional
    public ChargingResult chargeUsageEvent(UsageEvent event) {
        log.info("Processing real-time charging for event: {}, subscription: {}", 
            event.getId(), event.getSubscriptionId());
        
        try {
            RatingResult ratingResult = ratingEngine.rateUsageEvent(event);
            
            saveRatingRecord(ratingResult, event);
            
            applyChargeToAccount(event.getAccountId(), event.getSubscriptionId(), ratingResult);
            
            log.info("Charging complete: event={}, amount={}", 
                event.getId(), ratingResult.getTotalAmount());
            
            return ChargingResult.success(
                event.getId(),
                ratingResult.getChargedAmount(),
                ratingResult.getTotalAmount(),
                ratingResult.getCurrency()
            );
            
        } catch (Exception e) {
            log.error("Charging failed for event: {}, error: {}", event.getId(), e.getMessage(), e);
            return ChargingResult.failure(event.getId(), e.getMessage());
        }
    }

    private void saveRatingRecord(RatingResult result, UsageEvent event) {
        RatingRecord record = RatingRecord.builder()
            .eventId(event.getId())
            .subscriptionId(event.getSubscriptionId())
            .serviceType(event.getServiceType())
            .chargingType(RatingRecord.ChargingType.USAGE)
            .ratedAt(Instant.now())
            .status(RatingRecord.RatingStatus.RATED)
            .build();
        
        record.setChargedAmount(result.getChargedAmount());
        record.setTaxAmount(result.getTaxAmount());
        record.setTotalAmount(result.getTotalAmount());
        record.setCurrency(result.getCurrency());
        
        ratingRecordRepository.save(record);
        log.debug("Rating record saved for event: {}", event.getId());
    }

    private void applyChargeToAccount(UUID accountId, String subscriptionId, RatingResult ratingResult) {
        if (accountId == null || ratingResult.getTotalAmount() == null || 
            ratingResult.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("Skipping charge - no account or zero amount");
            return;
        }
        
        try {
            // Check billing type (prepaid vs postpaid)
            var billingAccountOpt = billingAccountRepository.findById(accountId);
            
            if (billingAccountOpt.isEmpty()) {
                log.warn("Billing account not found for id: {}, creating invoice", accountId);
                createPostpaidInvoice(accountId, ratingResult);
                return;
            }
            
            ConvergentBillingAccount billingAccount = billingAccountOpt.get();
            
            if (billingAccount.getBillingType() == ConvergentBillingAccount.BillingType.PREPAID) {
                // Prepaid: deduct from balance immediately
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
                // Postpaid/Hybrid: create invoice for later payment
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
            Invoice invoice = Invoice.builder()
                .accountId(accountId)
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .invoiceDate(Instant.now())
                .dueDate(Instant.now().plusSeconds(30 * 24 * 60 * 60)) // 30 days
                .status(Invoice.InvoiceStatus.PENDING)
                .build();
            
            invoice.setSubtotal(ratingResult.getChargedAmount());
            invoice.setTaxAmount(ratingResult.getTaxAmount());
            invoice.setTotalAmount(ratingResult.getTotalAmount());
            invoice.setCurrency(ratingResult.getCurrency());
            
            BillingService.InvoiceItem item = new BillingService.InvoiceItem();
            item.setDescription("Usage charge: " + ratingResult.getServiceType());
            item.setQuantity(1);
            item.setUnitPrice(ratingResult.getChargedAmount());
            item.setAmount(ratingResult.getChargedAmount());
            
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