package com.yemenptc.bss.sdk.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmfCloudEventBuilder {

    private final ObjectMapper objectMapper;

    public CloudEvent buildEvent(String domain, String action, UUID entityId, Object data) {
        String eventType = String.format("com.yemenptc.%s.%s", domain, action);
        String source = "urn:yemenptc:bss:" + domain;

        try {
            byte[] dataBytes = objectMapper.writeValueAsBytes(data);
            return CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withType(eventType)
                    .withSource(URI.create(source))
                    .withSubject(entityId.toString())
                    .withTime(OffsetDateTime.now())
                    .withData("application/json", dataBytes)
                    .build();
        } catch (Exception e) {
            log.error("Failed to build CloudEvent", e);
            return CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withType(eventType)
                    .withSource(URI.create(source))
                    .withSubject(entityId.toString())
                    .build();
        }
    }

    public CloudEvent buildEvent(String domain, String action, UUID entityId, Object data, String correlationId) {
        return buildEvent(domain, action, entityId, data);
    }
}
