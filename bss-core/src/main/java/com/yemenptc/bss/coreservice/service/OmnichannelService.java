package com.yemenptc.bss.coreservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmnichannelService {

    @Transactional
    public Map<String, Object> sendUnifiedNotification(String customerId, Map<String, Object> message) {
        log.info("Omnichannel: Sending unified notification to {}", customerId);

        List<String> channels = getPreferredChannels(customerId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("channels", channels);
        result.put("message", message.get("content"));
        result.put("sentAt", LocalDateTime.now().toString());
        result.put("deliveryStatus", "SENT");
        result.put("channelResults", channels.stream().map(ch -> 
            Map.of("channel", ch, "status", "DELIVERED")
        ).toList());

        return result;
    }

    @Transactional
    public Map<String, Object> getChannelPreferences(String customerId) {
        log.info("Omnichannel: Getting channel preferences for {}", customerId);

        return Map.of(
            "customerId", customerId,
            "preferredChannels", Arrays.asList("SMS", "EMAIL", "APP_PUSH"),
            "optIn", Map.of(
                "sms", true,
                "email", true,
                "appPush", true,
                "voiceCall", false,
                "whatsapp", true
            ),
            "doNotDisturb", Map.of(
                "enabled", false,
                "quietHoursStart", "22:00",
                "quietHoursEnd", "08:00"
            ),
            "language", "ar"
        );
    }

    @Transactional
    public Map<String, Object> routeInteraction(String customerId, String channel, String type) {
        log.info("Omnichannel: Routing interaction from {} via {}", customerId, channel);

        String bestChannel = determineBestChannel(customerId, type);
        String agentSkill = determineRequiredSkill(type);

        return Map.of(
            "customerId", customerId,
            "requestedChannel", channel,
            "routedChannel", bestChannel,
            "agentSkill", agentSkill,
            "estimatedWaitTime", new Random().nextInt(120) + 30,
            "ticketId", "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
    }

    @Transactional
    public Map<String, Object> getInteractionHistory(String customerId) {
        log.info("Omnichannel: Fetching interaction history for {}", customerId);

        return Map.of(
            "customerId", customerId,
            "totalInteractions", 45,
            "byChannel", Map.of(
                "SMS", 15,
                "EMAIL", 12,
                "APP_PUSH", 8,
                "VOICE", 6,
                "WHATSAPP", 4
            ),
            "recentInteractions", List.of(
                Map.of("channel", "SMS", "date", LocalDateTime.now().minusDays(1).toString(), "type", "NOTIFICATION"),
                Map.of("channel", "EMAIL", "date", LocalDateTime.now().minusDays(5).toString(), "type", "BILLING")
            )
        );
    }

    @Transactional
    public Map<String, Object> handleCrossChannelHandoff(String interactionId, String fromChannel, String toChannel) {
        log.info("Omnichannel: Handoff interaction {} from {} to {}", interactionId, fromChannel, toChannel);

        return Map.of(
            "interactionId", interactionId,
            "fromChannel", fromChannel,
            "toChannel", toChannel,
            "status", "HANDOFF_COMPLETE",
            "contextPreserved", true,
            "timestamp", LocalDateTime.now().toString()
        );
    }

    private List<String> getPreferredChannels(String customerId) {
        return Arrays.asList("SMS", "EMAIL", "APP_PUSH");
    }

    private String determineBestChannel(String customerId, String type) {
        return switch (type.toUpperCase()) {
            case "URGENT" -> "VOICE";
            case "BILLING" -> "EMAIL";
            case "PROMOTIONAL" -> "SMS";
            default -> "APP_PUSH";
        };
    }

    private String determineRequiredSkill(String type) {
        return switch (type.toUpperCase()) {
            case "TECHNICAL" -> "TECHNICAL_SUPPORT";
            case "BILLING" -> "BILLING_SPECIALIST";
            case "SALES" -> "SALES_AGENT";
            default -> "GENERAL_SUPPORT";
        };
    }
}