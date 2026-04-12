package com.yemenptc.bss.coreservice.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MetricsService {

    private final Counter orderCreatedCounter;
    private final Counter orderCompletedCounter;
    private final Counter orderFailedCounter;
    private final Counter paymentProcessedCounter;
    private final Counter paymentFailedCounter;
    private final Counter provisioningCompletedCounter;
    private final Counter provisioningFailedCounter;
    private final Timer orderProcessingTimer;
    private final Timer paymentProcessingTimer;
    private final Timer provisioningTimer;
    private final AtomicInteger activeOrders;
    private final AtomicInteger activePayments;
    private final AtomicInteger activeProvisionings;

    public MetricsService(MeterRegistry registry) {
        this.orderCreatedCounter = Counter.builder("bss.orders.created")
            .description("Total number of orders created")
            .register(registry);
        
        this.orderCompletedCounter = Counter.builder("bss.orders.completed")
            .description("Total number of orders completed")
            .register(registry);
        
        this.orderFailedCounter = Counter.builder("bss.orders.failed")
            .description("Total number of orders failed")
            .register(registry);
        
        this.paymentProcessedCounter = Counter.builder("bss.payments.processed")
            .description("Total number of payments processed")
            .register(registry);
        
        this.paymentFailedCounter = Counter.builder("bss.payments.failed")
            .description("Total number of payments failed")
            .register(registry);
        
        this.provisioningCompletedCounter = Counter.builder("bss.provisioning.completed")
            .description("Total number of provisioning completed")
            .register(registry);
        
        this.provisioningFailedCounter = Counter.builder("bss.provisioning.failed")
            .description("Total number of provisioning failed")
            .register(registry);
        
        this.orderProcessingTimer = Timer.builder("bss.orders.processing.time")
            .description("Time taken to process orders")
            .register(registry);
        
        this.paymentProcessingTimer = Timer.builder("bss.payments.processing.time")
            .description("Time taken to process payments")
            .register(registry);
        
        this.provisioningTimer = Timer.builder("bss.provisioning.time")
            .description("Time taken to provision services")
            .register(registry);
        
        this.activeOrders = new AtomicInteger(0);
        this.activePayments = new AtomicInteger(0);
        this.activeProvisionings = new AtomicInteger(0);
        
        Gauge.builder("bss.orders.active", activeOrders, AtomicInteger::get)
            .description("Number of currently active orders")
            .register(registry);
        
        Gauge.builder("bss.payments.active", activePayments, AtomicInteger::get)
            .description("Number of currently active payments")
            .register(registry);
        
        Gauge.builder("bss.provisioning.active", activeProvisionings, AtomicInteger::get)
            .description("Number of currently active provisioning orders")
            .register(registry);
    }

    public void recordOrderCreated() {
        orderCreatedCounter.increment();
    }

    public void recordOrderCompleted() {
        orderCompletedCounter.increment();
    }

    public void recordOrderFailed() {
        orderFailedCounter.increment();
    }

    public void recordPaymentProcessed() {
        paymentProcessedCounter.increment();
    }

    public void recordPaymentFailed() {
        paymentFailedCounter.increment();
    }

    public void recordProvisioningCompleted() {
        provisioningCompletedCounter.increment();
    }

    public void recordProvisioningFailed() {
        provisioningFailedCounter.increment();
    }

    public Timer.Sample startOrderTimer() {
        activeOrders.incrementAndGet();
        return Timer.start();
    }

    public void stopOrderTimer(Timer.Sample sample) {
        sample.stop(orderProcessingTimer);
        activeOrders.decrementAndGet();
    }

    public Timer.Sample startPaymentTimer() {
        activePayments.incrementAndGet();
        return Timer.start();
    }

    public void stopPaymentTimer(Timer.Sample sample) {
        sample.stop(paymentProcessingTimer);
        activePayments.decrementAndGet();
    }

    public Timer.Sample startProvisioningTimer() {
        activeProvisionings.incrementAndGet();
        return Timer.start();
    }

    public void stopProvisioningTimer(Timer.Sample sample) {
        sample.stop(provisioningTimer);
        activeProvisionings.decrementAndGet();
    }
}
