package com.yemenptc.bss.sdk.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OpenTelemetry tracing helper for TMF services.
 * Creates custom spans for business operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TmfTracer {

    private final Tracer tracer;

    /**
     * Creates a span for a TMF business operation.
     */
    public Span startSpan(String operationName, String domain, String entityId) {
        return tracer.spanBuilder("tmf." + domain + "." + operationName)
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute("tmf.domain", domain)
                .setAttribute("tmf.operation", operationName)
                .setAttribute("tmf.entity.id", entityId)
                .startSpan();
    }

    /**
     * Creates a span for Kafka message processing.
     */
    public Span startKafkaSpan(String topic, String operation, String messageId) {
        return tracer.spanBuilder("kafka." + operation)
                .setSpanKind(SpanKind.CONSUMER)
                .setAttribute("messaging.system", "kafka")
                .setAttribute("messaging.destination", topic)
                .setAttribute("messaging.message_id", messageId)
                .startSpan();
    }

    /**
     * Creates a span for database operations.
     */
    public Span startDbSpan(String operation, String table) {
        return tracer.spanBuilder("db." + operation)
                .setSpanKind(SpanKind.CLIENT)
                .setAttribute("db.system", "postgresql")
                .setAttribute("db.operation", operation)
                .setAttribute("db.sql.table", table)
                .startSpan();
    }

    /**
     * Creates a span for Redis operations.
     */
    public Span startCacheSpan(String operation, String key) {
        return tracer.spanBuilder("cache." + operation)
                .setSpanKind(SpanKind.CLIENT)
                .setAttribute("db.system", "redis")
                .setAttribute("db.operation", operation)
                .setAttribute("db.redis.key", key)
                .startSpan();
    }

    /**
     * Ends a span successfully.
     */
    public void endSpan(Span span) {
        if (span != null) {
            span.setStatus(StatusCode.OK);
            span.end();
        }
    }

    /**
     * Ends a span with an error.
     */
    public void endSpanWithError(Span span, Throwable error) {
        if (span != null) {
            span.setStatus(StatusCode.ERROR, error.getMessage());
            span.recordException(error);
            span.end();
        }
    }
}
