package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.entity.RatingRecord;
import com.yemenptc.bss.coreservice.repository.UsageEventRepository;
import com.yemenptc.bss.coreservice.repository.RatingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueAssuranceService {

    private final UsageEventRepository usageEventRepository;
    private final RatingRecordRepository ratingRecordRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> generateDailyReport(LocalDate date) {
        List<UsageEvent> events = usageEventRepository.findAll();
        List<RatingRecord> records = ratingRecordRepository.findAll();

        long totalEvents = events.size();
        long totalRated = records.size();
        long unrated = totalEvents - totalRated;

        BigDecimal totalRevenue = records.stream()
            .map(r -> r.getChargedAmount() != null ? r.getChargedAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        double ratingAccuracy = totalEvents > 0 ? (double) totalRated / totalEvents * 100 : 0;

        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("totalEvents", totalEvents);
        report.put("totalRated", totalRated);
        report.put("unratedEvents", unrated);
        report.put("totalRevenue", totalRevenue);
        report.put("ratingAccuracy", ratingAccuracy);
        return report;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> detectFraud(UUID subscriptionId) {
        List<UsageEvent> events = usageEventRepository.findBySubscriptionId(subscriptionId);
        List<Map<String, Object>> alerts = new ArrayList<>();

        if (events.isEmpty()) {
            return alerts;
        }

        BigDecimal totalUsage = events.stream()
            .map(e -> e.getUsageValue() != null ? e.getUsageValue() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgUsage = totalUsage.divide(BigDecimal.valueOf(events.size()), 2, BigDecimal.ROUND_HALF_UP);

        for (UsageEvent event : events) {
            BigDecimal eventValue = event.getUsageValue() != null ? event.getUsageValue() : BigDecimal.ZERO;
            if (eventValue.compareTo(avgUsage.multiply(BigDecimal.valueOf(5))) > 0) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("subscriptionId", subscriptionId);
                alert.put("eventId", event.getEventId());
                alert.put("alertType", "UNUSUAL_USAGE_SPIKE");
                alert.put("severity", "HIGH");
                alert.put("description", "Usage spike detected: " + eventValue + " vs avg " + avgUsage);
                alert.put("detectedAt", Instant.now());
                alerts.add(alert);
            }
        }

        return alerts;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> detectSimBoxFraud() {
        Map<UUID, List<UsageEvent>> eventsBySubscription = usageEventRepository.findAll().stream()
            .collect(Collectors.groupingBy(UsageEvent::getSubscriptionId));

        List<Map<String, Object>> alerts = new ArrayList<>();

        for (Map.Entry<UUID, List<UsageEvent>> entry : eventsBySubscription.entrySet()) {
            List<UsageEvent> events = entry.getValue();
            if (events.size() > 1000) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("subscriptionId", entry.getKey());
                alert.put("alertType", "SIM_BOX_FRAUD");
                alert.put("severity", "CRITICAL");
                alert.put("description", "High volume usage detected: " + events.size() + " events");
                alert.put("detectedAt", Instant.now());
                alerts.add(alert);
            }
        }

        return alerts;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateLeakage() {
        List<UsageEvent> events = usageEventRepository.findAll();
        List<RatingRecord> records = ratingRecordRepository.findAll();

        Set<String> ratedEventIds = records.stream()
            .map(RatingRecord::getEventId)
            .collect(Collectors.toSet());

        return events.stream()
            .filter(e -> !ratedEventIds.contains(e.getEventId()))
            .map(e -> e.getUsageValue() != null ? e.getUsageValue() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
