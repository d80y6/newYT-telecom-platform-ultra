package com.yemenptc.bss.coreservice.kafka;

import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CdrConsumer {

    private final RatingService ratingService;

    @KafkaListener(topics = "billing.cdr.raw", groupId = "rating-service-group")
    public void consumeCdr(UsageEvent event) {
        log.info("Received CDR event: {}", event.getEventId());
        try {
            ratingService.rateUsage(event);
            log.info("Rated CDR event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Failed to rate CDR event: {}", event.getEventId(), e);
        }
    }
}
