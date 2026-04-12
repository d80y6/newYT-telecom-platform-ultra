package com.yemenptc.bss.sdk.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenTelemetryConfig {

    @Value("${otel.service.name:bss-core}")
    private String serviceName;

    @Bean
    @Primary
    public OpenTelemetry openTelemetry() {
        return OpenTelemetry.noop();
    }

    @Bean
    @ConditionalOnMissingBean(Tracer.class)
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName, "1.0.0");
    }
}
