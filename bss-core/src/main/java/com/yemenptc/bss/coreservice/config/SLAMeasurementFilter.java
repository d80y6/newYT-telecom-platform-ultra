package com.yemenptc.bss.coreservice.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SLAMeasurementFilter implements Filter {

    private final MeterRegistry meterRegistry;
    
    private final Counter orderSlaSuccessCounter;
    private final Counter orderSlaBreachCounter;
    private final Timer orderProcessingTimer;
    private final Counter paymentSlaSuccessCounter;
    private final Counter paymentSlaBreachCounter;
    private final Timer paymentProcessingTimer;
    private final Counter provisioningSlaSuccessCounter;
    private final Counter provisioningSlaBreachCounter;
    private final Timer provisioningTimer;

    public SLAMeasurementFilter(MeterRegistry registry) {
        this.meterRegistry = registry;
        
        this.orderSlaSuccessCounter = Counter.builder("bss.sla.order.success")
            .description("Orders completed within SLA").register(registry);
        this.orderSlaBreachCounter = Counter.builder("bss.sla.order.breach")
            .description("Orders exceeding SLA").register(registry);
        this.orderProcessingTimer = Timer.builder("bss.sla.order.time")
            .description("Order processing time").register(registry);
        
        this.paymentSlaSuccessCounter = Counter.builder("bss.sla.payment.success")
            .description("Payments processed within SLA").register(registry);
        this.paymentSlaBreachCounter = Counter.builder("bss.sla.payment.breach")
            .description("Payments exceeding SLA").register(registry);
        this.paymentProcessingTimer = Timer.builder("bss.sla.payment.time")
            .description("Payment processing time").register(registry);
        
        this.provisioningSlaSuccessCounter = Counter.builder("bss.sla.provisioning.success")
            .description("Provisioning within SLA").register(registry);
        this.provisioningSlaBreachCounter = Counter.builder("bss.sla.provisioning.breach")
            .description("Provisioning exceeding SLA").register(registry);
        this.provisioningTimer = Timer.builder("bss.sla.provisioning.time")
            .description("Provisioning time").register(registry);
    }

    private static final long ORDER_SLA_MS = 1000;
    private static final long PAYMENT_SLA_MS = 2000;
    private static final long PROVISIONING_SLA_MS = 5000;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = httpRequest.getRequestURI();
        String correlationId = httpRequest.getHeader("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        long startTime = System.currentTimeMillis();
        String startTimestamp = Instant.now().toString();
        
        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            if (requestPath.contains("/orders")) {
                recordOrderSLA(duration);
            } else if (requestPath.contains("/billing") || requestPath.contains("/payments")) {
                recordPaymentSLA(duration);
            } else if (requestPath.contains("/provisioning")) {
                recordProvisioningSLA(duration);
            }
            
            httpResponse.setHeader("X-Correlation-ID", correlationId);
            httpResponse.setHeader("X-Response-Time-Ms", String.valueOf(duration));
            httpResponse.setHeader("X-Request-Start", startTimestamp);
            
            log.debug("Request {} completed in {}ms", correlationId, duration);
        }
    }

    private void recordOrderSLA(long durationMs) {
        orderProcessingTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        if (durationMs <= ORDER_SLA_MS) {
            orderSlaSuccessCounter.increment();
        } else {
            orderSlaBreachCounter.increment();
            log.warn("Order SLA breach: {}ms > {}ms", durationMs, ORDER_SLA_MS);
        }
    }

    private void recordPaymentSLA(long durationMs) {
        paymentProcessingTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        if (durationMs <= PAYMENT_SLA_MS) {
            paymentSlaSuccessCounter.increment();
        } else {
            paymentSlaBreachCounter.increment();
            log.warn("Payment SLA breach: {}ms > {}ms", durationMs, PAYMENT_SLA_MS);
        }
    }

    private void recordProvisioningSLA(long durationMs) {
        provisioningTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        if (durationMs <= PROVISIONING_SLA_MS) {
            provisioningSlaSuccessCounter.increment();
        } else {
            provisioningSlaBreachCounter.increment();
            log.warn("Provisioning SLA breach: {}ms > {}ms", durationMs, PROVISIONING_SLA_MS);
        }
    }
}
