package com.yemenptc.bss.sdk.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Prometheus metrics for TMF business KPIs.
 * Exposes metrics at /actuator/prometheus
 */
@Component
@RequiredArgsConstructor
public class TmfMetricsRegistry {

    private final MeterRegistry registry;

    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> gauges = new ConcurrentHashMap<>();

    // ==================== ORDER METRICS ====================

    public void incrementOrdersCreated(String orderType) {
        getOrCreateCounter("tmf.orders.created",
                "order.type", orderType).increment();
    }

    public void incrementOrdersCompleted(String orderType) {
        getOrCreateCounter("tmf.orders.completed",
                "order.type", orderType).increment();
    }

    public void incrementOrdersFailed(String orderType) {
        getOrCreateCounter("tmf.orders.failed",
                "order.type", orderType).increment();
    }

    public Timer.Sample startOrderTimer() {
        return Timer.start(registry);
    }

    public void stopOrderTimer(Timer.Sample sample, String orderType) {
        sample.stop(getOrCreateTimer("tmf.orders.duration",
                "order.type", orderType));
    }

    // ==================== BILLING METRICS ====================

    public void incrementInvoicesCreated() {
        getOrCreateCounter("tmf.invoices.created").increment();
    }

    public void incrementInvoicesFinalized() {
        getOrCreateCounter("tmf.invoices.finalized").increment();
    }

    public void incrementPaymentsProcessed(String method) {
        getOrCreateCounter("tmf.payments.processed",
                "payment.method", method).increment();
    }

    public void incrementPaymentsFailed(String method) {
        getOrCreateCounter("tmf.payments.failed",
                "payment.method", method).increment();
    }

    // ==================== CHARGING METRICS ====================

    public void incrementCdrsProcessed(String serviceType) {
        getOrCreateCounter("tmf.charging.cdrs.processed",
                "service.type", serviceType).increment();
    }

    public Timer.Sample startChargingTimer() {
        return Timer.start(registry);
    }

    public void stopChargingTimer(Timer.Sample sample) {
        sample.stop(getOrCreateTimer("tmf.charging.duration"));
    }

    public void updateBalanceGauge(String accountId, double balance) {
        gauges.computeIfAbsent("tmf.balance.current." + accountId,
                k -> {
                    AtomicInteger g = new AtomicInteger((int) balance);
                    Gauge.builder("tmf.balance.current", g, AtomicInteger::get)
                            .tag("account.id", accountId)
                            .register(registry);
                    return g;
                }).set((int) balance);
    }

    // ==================== PARTY METRICS ====================

    public void incrementPartiesCreated() {
        getOrCreateCounter("tmf.parties.created").increment();
    }

    public void incrementDedupMatches() {
        getOrCreateCounter("tmf.parties.dedup.matches").increment();
    }

    public void incrementGdprDeletions() {
        getOrCreateCounter("tmf.parties.gdpr.deletions").increment();
    }

    // ==================== CATALOG METRICS ====================

    public void incrementCatalogOfferingsCreated(String serviceType) {
        getOrCreateCounter("tmf.catalog.offerings.created",
                "service.type", serviceType).increment();
    }

    // ==================== INVENTORY METRICS ====================

    public void incrementResourcesAllocated(String resourceType) {
        getOrCreateCounter("tmf.inventory.resources.allocated",
                "resource.type", resourceType).increment();
    }

    public void incrementResourcesReleased(String resourceType) {
        getOrCreateCounter("tmf.inventory.resources.released",
                "resource.type", resourceType).increment();
    }

    // ==================== ALARM METRICS ====================

    public void incrementAlarmsRaised(String severity) {
        getOrCreateCounter("tmf.alarms.raised",
                "severity", severity).increment();
    }

    public void incrementAlarmsCleared(String severity) {
        getOrCreateCounter("tmf.alarms.cleared",
                "severity", severity).increment();
    }

    // ==================== HELPERS ====================

    private Counter getOrCreateCounter(String name, String... tags) {
        String key = name + ":" + String.join(":", tags);
        return counters.computeIfAbsent(key, k -> {
            Counter.Builder builder = Counter.builder(name);
            for (int i = 0; i < tags.length; i += 2) {
                builder.tag(tags[i], tags[i + 1]);
            }
            return builder.register(registry);
        });
    }

    private Timer getOrCreateTimer(String name, String... tags) {
        String key = name + ":" + String.join(":", tags);
        return timers.computeIfAbsent(key, k -> {
            Timer.Builder builder = Timer.builder(name);
            for (int i = 0; i < tags.length; i += 2) {
                builder.tag(tags[i], tags[i + 1]);
            }
            return builder.register(registry);
        });
    }
}
