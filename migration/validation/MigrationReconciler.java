package com.yemenptc.bss.migration.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Reconciliation reports for legacy vs TMF data validation.
 * Ensures data integrity during migration.
 */
@Slf4j
@Component
public class MigrationReconciler {

    private final JdbcTemplate legacyDb;
    private final JdbcTemplate tmfDb;

    public MigrationReconciler(JdbcTemplate legacyDb, JdbcTemplate tmfDb) {
        this.legacyDb = legacyDb;
        this.tmfDb = tmfDb;
    }

    /**
     * Reconciles customer counts between legacy and TMF.
     */
    public ReconciliationResult reconcileCustomerCount(String exchangeCode) {
        Long legacyCount = legacyDb.queryForObject(
                "SELECT COUNT(*) FROM titan.customers WHERE exchange_code = ? AND status = 'ACTIVE'",
                Long.class, exchangeCode);

        Long tmfCount = tmfDb.queryForObject(
                "SELECT COUNT(*) FROM customers WHERE characteristics->>'legacySystem' = 'TITAN'",
                Long.class);

        boolean match = legacyCount != null && legacyCount.equals(tmfCount);

        log.info("Customer reconciliation for {}: legacy={}, tmf={}, match={}",
                exchangeCode, legacyCount, tmfCount, match);

        return new ReconciliationResult("CUSTOMER_COUNT", exchangeCode,
                legacyCount, tmfCount, match);
    }

    /**
     * Reconciles account balances between legacy and TMF.
     */
    public ReconciliationResult reconcileAccountBalances(String exchangeCode) {
        BigDecimal legacyTotal = legacyDb.queryForObject(
                "SELECT COALESCE(SUM(balance), 0) FROM titan.accounts WHERE exchange_code = ?",
                BigDecimal.class, exchangeCode);

        BigDecimal tmfTotal = tmfDb.queryForObject(
                "SELECT COALESCE(SUM(balance), 0) FROM accounts",
                BigDecimal.class);

        boolean match = legacyTotal != null && legacyTotal.compareTo(tmfTotal) == 0;

        log.info("Balance reconciliation for {}: legacy={}, tmf={}, match={}",
                exchangeCode, legacyTotal, tmfTotal, match);

        return new ReconciliationResult("ACCOUNT_BALANCES", exchangeCode,
                legacyTotal, tmfTotal, match);
    }

    /**
     * Reconciles active services between legacy and TMF.
     */
    public ReconciliationResult reconcileActiveServices(String exchangeCode) {
        Long legacyCount = legacyDb.queryForObject(
                "SELECT COUNT(*) FROM titan.lines WHERE exchange_code = ? AND status = 'ACTIVE'",
                Long.class, exchangeCode);

        Long tmfCount = tmfDb.queryForObject(
                "SELECT COUNT(*) FROM subscriptions WHERE status = 'ACTIVE'",
                Long.class);

        boolean match = legacyCount != null && legacyCount.equals(tmfCount);

        log.info("Service reconciliation for {}: legacy={}, tmf={}, match={}",
                exchangeCode, legacyCount, tmfCount, match);

        return new ReconciliationResult("ACTIVE_SERVICES", exchangeCode,
                legacyCount, tmfCount, match);
    }

    /**
     * Generates full reconciliation report.
     */
    public Map<String, Object> generateReport(String exchangeCode) {
        Map<String, Object> report = new HashMap<>();
        report.put("exchangeCode", exchangeCode);
        report.put("generatedAt", new Date().toString());

        List<ReconciliationResult> results = new ArrayList<>();
        results.add(reconcileCustomerCount(exchangeCode));
        results.add(reconcileAccountBalances(exchangeCode));
        results.add(reconcileActiveServices(exchangeCode));

        report.put("results", results);
        report.put("allMatched", results.stream().allMatch(ReconciliationResult::matched));
        report.put("failedChecks", results.stream()
                .filter(r -> !r.matched())
                .map(ReconciliationResult::checkType)
                .toList());

        return report;
    }

    public record ReconciliationResult(
            String checkType,
            String scope,
            Object legacyValue,
            Object tmfValue,
            boolean matched
    ) {}
}
