package com.yemenptc.bss.sdk.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Intercepts requests with Idempotency-Key header.
 * Caches responses in Redis for 24 hours to prevent duplicate processing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String CACHE_KEY_PREFIX = "idempotency:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Checks if this request was already processed.
     * Returns cached response if found, null if new request.
     */
    public Optional<String> getCachedResponse(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }

        String cacheKey = CACHE_KEY_PREFIX + idempotencyKey;
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            log.debug("Idempotency cache hit for key={}", idempotencyKey);
            return Optional.of(cached);
        }

        return Optional.empty();
    }

    /**
     * Caches the response for future identical requests.
     */
    public void cacheResponse(String idempotencyKey, Object response) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        try {
            String cacheKey = CACHE_KEY_PREFIX + idempotencyKey;
            String serialized = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, serialized, CACHE_TTL);
            log.debug("Cached response for idempotency key={}", idempotencyKey);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response for idempotency cache", e);
        }
    }

    /**
     * Marks request as processing to prevent concurrent duplicate requests.
     * Returns true if lock acquired, false if already processing.
     */
    public boolean acquireLock(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return true;
        }

        String lockKey = CACHE_KEY_PREFIX + "lock:" + idempotencyKey;
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "processing", Duration.ofMinutes(5));

        return Boolean.TRUE.equals(acquired);
    }

    /**
     * Releases the processing lock.
     */
    public void releaseLock(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        String lockKey = CACHE_KEY_PREFIX + "lock:" + idempotencyKey;
        redisTemplate.delete(lockKey);
    }

    /**
     * Extracts idempotency key from request header.
     */
    public String extractKey(HttpServletRequest request) {
        return request.getHeader(IDEMPOTENCY_KEY_HEADER);
    }
}
