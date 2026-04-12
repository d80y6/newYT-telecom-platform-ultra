package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ConvergentBillingAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConvergentBillingService {

    @Transactional
    public ConvergentBillingAccount createAccount(ConvergentBillingAccount request) {
        log.info("Creating convergent billing account for customer: {}", request.getCustomerId());

        ConvergentBillingAccount account = ConvergentBillingAccount.builder()
            .accountId("CBA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerId(request.getCustomerId())
            .billingType(request.getBillingType())
            .status(ConvergentBillingAccount.AccountStatus.ACTIVE)
            .currentBalance(BigDecimal.ZERO)
            .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.valueOf(50000))
            .availableCredit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.valueOf(50000))
            .billingCycleDay(1)
            .totalRevenueYtd(BigDecimal.ZERO)
            .prepaidBalance(BigDecimal.ZERO)
            .postpaidBalance(BigDecimal.ZERO)
            .build();

        updateBillingCycle(account);
        
        log.info("Convergent billing account created: {}", account.getAccountId());
        return account;
    }

    @Transactional(readOnly = true)
    public ConvergentBillingAccount getAccount(String accountId) {
        log.info("Retrieving convergent billing account: {}", accountId);
        return ConvergentBillingAccount.builder()
            .accountId(accountId)
            .customerId("CUST-001")
            .billingType(ConvergentBillingAccount.BillingType.HYBRID)
            .status(ConvergentBillingAccount.AccountStatus.ACTIVE)
            .currentBalance(BigDecimal.valueOf(15000))
            .creditLimit(BigDecimal.valueOf(50000))
            .availableCredit(BigDecimal.valueOf(35000))
            .prepaidBalance(BigDecimal.valueOf(5000))
            .postpaidBalance(BigDecimal.valueOf(10000))
            .billingCycleDay(1)
            .build();
    }

    @Transactional
    public Map<String, Object> recharge(String accountId, BigDecimal amount) {
        log.info("Processing recharge for {}: {}", accountId, amount);
        
        return Map.of(
            "accountId", accountId,
            "amount", amount,
            "newBalance", BigDecimal.valueOf(20000),
            "transactionId", "TXN-" + UUID.randomUUID().toString().substring(0, 8),
            "timestamp", LocalDateTime.now().toString()
        );
    }

    @Transactional
    public Map<String, Object> charge(String accountId, BigDecimal amount, String serviceType) {
        log.info("Processing charge for {}: {} ({})", accountId, amount, serviceType);
        
        return Map.of(
            "accountId", accountId,
            "amount", amount,
            "serviceType", serviceType,
            "remainingBalance", BigDecimal.valueOf(10000),
            "transactionId", "TXN-" + UUID.randomUUID().toString().substring(0, 8),
            "timestamp", LocalDateTime.now().toString()
        );
    }

    @Transactional
    public Map<String, Object> getAccountSummary(String accountId) {
        ConvergentBillingAccount account = getAccount(accountId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("accountId", account.getAccountId());
        summary.put("customerId", account.getCustomerId());
        summary.put("billingType", account.getBillingType().name());
        summary.put("currentBalance", account.getCurrentBalance());
        summary.put("creditLimit", account.getCreditLimit());
        summary.put("availableCredit", account.getAvailableCredit());
        summary.put("prepaidBalance", account.getPrepaidBalance());
        summary.put("postpaidBalance", account.getPostpaidBalance());
        summary.put("billingCycleStart", account.getBillingCycleStart() != null ? account.getBillingCycleStart().toString() : "");
        summary.put("billingCycleEnd", account.getBillingCycleEnd() != null ? account.getBillingCycleEnd().toString() : "");
        summary.put("totalRevenueYtd", account.getTotalRevenueYtd());
        return summary;
    }

    @Transactional
    public Map<String, Object> processPayment(String accountId, BigDecimal amount) {
        log.info("Processing payment for {}: {}", accountId, amount);
        
        return Map.of(
            "accountId", accountId,
            "amount", amount,
            "newBalance", BigDecimal.valueOf(5000),
            "paymentId", "PAY-" + UUID.randomUUID().toString().substring(0, 8),
            "timestamp", LocalDateTime.now().toString()
        );
    }

    private void updateBillingCycle(ConvergentBillingAccount account) {
        LocalDateTime now = LocalDateTime.now();
        account.setBillingCycleStart(now.withDayOfMonth(1));
        
        LocalDateTime nextMonth = now.plusMonths(1);
        account.setBillingCycleEnd(nextMonth.withDayOfMonth(1).minusDays(1));
    }
}