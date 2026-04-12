package com.yemenptc.bss.coreservice.mediation;

import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.repository.UsageEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CdrDeduplicator {

    private final UsageEventRepository usageEventRepository;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String DEDUP_KEY_PREFIX = "cdr:dedup:";
    private static final Duration DEDUP_WINDOW = Duration.ofHours(24);

    public List<UsageEvent> deduplicate(List<UsageEvent> events) {
        return events.stream()
                .filter(this::isNotDuplicate)
                .collect(Collectors.toList());
    }

    public boolean isNotDuplicate(UsageEvent event) {
        if (event.getEventId() == null) {
            log.warn("CDR has no event ID, generating one");
            event.setEventId(generateFallbackEventId(event));
        }
        
        String dedupKey = buildDeduplicationKey(event);
        
        try {
            Boolean wasSet = redisTemplate.opsForValue()
                    .setIfAbsent(dedupKey, "1", DEDUP_WINDOW);
            
            if (Boolean.TRUE.equals(wasSet)) {
                if (existsInDatabase(event.getEventId())) {
                    log.info("Duplicate CDR found in database: {}", event.getEventId());
                    redisTemplate.delete(dedupKey);
                    return false;
                }
                return true;
            } else {
                log.info("Duplicate CDR found in cache: {}", event.getEventId());
                return false;
            }
        } catch (Exception e) {
            log.warn("Redis unavailable, falling back to database check: {}", e.getMessage());
            return !existsInDatabase(event.getEventId());
        }
    }

    private String buildDeduplicationKey(UsageEvent event) {
        StringBuilder key = new StringBuilder(DEDUP_KEY_PREFIX);
        key.append(event.getEventId());
        
        if (event.getSubscriptionId() != null) {
            key.append(":").append(event.getSubscriptionId());
        }
        
        if (event.getServiceType() != null) {
            key.append(":").append(event.getServiceType());
        }
        
        if (event.getEventTime() != null) {
            key.append(":").append(event.getEventTime().toEpochMilli());
        }
        
        return key.toString();
    }

    private boolean existsInDatabase(String eventId) {
        if (eventId == null) {
            return false;
        }
        
        try {
            List<?> existing = usageEventRepository.findByEventId(eventId);
            return existing != null && !existing.isEmpty();
        } catch (Exception e) {
            log.error("Error checking database for duplicate: {}", e.getMessage());
            return false;
        }
    }

    private String generateFallbackEventId(UsageEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getServiceType() != null ? event.getServiceType().toUpperCase() : "USAGE");
        sb.append("_");
        
        if (event.getSubscriptionId() != null) {
            sb.append(event.getSubscriptionId().toString().substring(0, 8));
        } else {
            sb.append("SUB");
        }
        sb.append("_");
        
        if (event.getEventTime() != null) {
            sb.append(event.getEventTime().toEpochMilli());
        } else {
            sb.append(System.currentTimeMillis());
        }
        sb.append("_");
        sb.append(event.hashCode() & 0xFFFF);
        
        return sb.toString();
    }

    public void markAsProcessed(String eventId) {
        if (eventId == null) return;
        
        String key = DEDUP_KEY_PREFIX + eventId;
        try {
            redisTemplate.expire(key, DEDUP_WINDOW);
        } catch (Exception e) {
            log.warn("Failed to extend dedup key expiry: {}", e.getMessage());
        }
    }

    public void clearProcessedEvents(Duration olderThan) {
        try {
            Set<String> keys = redisTemplate.keys(DEDUP_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    Long ttl = redisTemplate.getExpire(key);
                    if (ttl != null && ttl > 0 && ttl < 0) {
                        redisTemplate.delete(key);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to clear old dedup keys: {}", e.getMessage());
        }
    }
}
