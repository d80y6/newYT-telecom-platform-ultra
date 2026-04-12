package com.yemenptc.bss.coreservice.mediation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yemenptc.bss.coreservice.entity.UsageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CdrParser {

    private final ObjectMapper objectMapper;
    
    private static final DateTimeFormatter CDR_DATE_FORMAT = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static final Set<String> VALID_SERVICE_TYPES = Set.of(
            "VOICE", "SMS", "DATA", "MMS", "GPRS", "LTE", "ADSL", "FTTH", "VAS"
    );
    
    private static final Set<String> VALID_EVENT_TYPES = Set.of(
            "CALL_START", "CALL_END", "CALL_CONNECTED", "CALL_FAILED",
            "SMS_SENT", "SMS_RECEIVED", "DATA_SESSION_START", "DATA_SESSION_END",
            "DATA_USAGE", "SUBSCRIPTION_FEE", "RECHARGE", "ADJUSTMENT"
    );

    public Optional<UsageEvent> parseJsonCdr(String jsonCdr) {
        try {
            ObjectNode node = objectMapper.readValue(jsonCdr, ObjectNode.class);
            return parseJsonObject(node);
        } catch (Exception e) {
            log.error("Failed to parse JSON CDR: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<UsageEvent> parseCsvCdr(String csvContent) {
        List<UsageEvent> events = new ArrayList<>();
        String[] lines = csvContent.split("\n");
        
        if (lines.length < 2) {
            log.warn("CSV file has no data rows");
            return events;
        }
        
        String[] headers = lines[0].toLowerCase().split(",");
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim(), i);
        }
        
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().isEmpty()) continue;
            
            try {
                String[] values = parseCsvLine(line);
                UsageEvent event = parseCsvRow(values, headerMap);
                if (event != null) {
                    events.add(event);
                }
            } catch (Exception e) {
                log.warn("Failed to parse CSV line {}: {}", i, e.getMessage());
            }
        }
        
        return events;
    }

    private Optional<UsageEvent> parseJsonObject(ObjectNode node) {
        try {
            String eventId = getStringValue(node, "event_id", "cdr_id", "id");
            String msisdn = getStringValue(node, "msisdn", "subscriber_id", "subscriberNumber");
            String serviceType = normalizeServiceType(getStringValue(node, "service_type", "serviceType", "type"));
            String eventType = normalizeEventType(getStringValue(node, "event_type", "eventType", "action"));
            
            if (eventId == null || msisdn == null) {
                log.warn("Missing required fields in CDR: eventId={}, msisdn={}", eventId, msisdn);
                return Optional.empty();
            }
            
            BigDecimal usageValue = getDecimalValue(node, "usage_value", "usageValue", "duration", "volume", "bytes");
            if (usageValue == null) {
                usageValue = BigDecimal.ONE;
            }
            
            String usageUnit = getStringValue(node, "usage_unit", "usageUnit", "unit");
            if (usageUnit == null) {
                usageUnit = inferUsageUnit(serviceType, eventType);
            }
            
            Instant eventTime = getInstantValue(node, "event_time", "eventTime", "timestamp", "start_time", "startTime");
            if (eventTime == null) {
                eventTime = Instant.now();
            }
            
            String sourceSystem = getStringValue(node, "source_system", "sourceSystem", "network");
            if (sourceSystem == null) {
                sourceSystem = "CDR_PARSER";
            }
            
            UUID subscriptionId = extractSubscriptionId(node);
            
            UsageEvent event = UsageEvent.builder()
                    .eventId(eventId)
                    .subscriptionId(subscriptionId)
                    .serviceType(serviceType)
                    .eventType(eventType)
                    .usageValue(usageValue)
                    .usageUnit(usageUnit)
                    .eventTime(eventTime)
                    .sourceSystem(sourceSystem)
                    .build();
            
            return Optional.of(event);
            
        } catch (Exception e) {
            log.error("Error parsing JSON CDR: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private UsageEvent parseCsvRow(String[] values, Map<String, Integer> headerMap) {
        String eventId = getCsvValue(values, headerMap, "event_id", "cdr_id", "id");
        String msisdn = getCsvValue(values, headerMap, "msisdn", "subscriber_id");
        
        if (eventId == null || msisdn == null) {
            return null;
        }
        
        String serviceType = normalizeServiceType(
                getCsvValue(values, headerMap, "service_type", "type"));
        String eventType = normalizeEventType(
                getCsvValue(values, headerMap, "event_type", "action"));
        
        BigDecimal usageValue = getCsvDecimalValue(values, headerMap, 
                "usage_value", "duration", "volume", "bytes");
        if (usageValue == null) {
            usageValue = BigDecimal.ONE;
        }
        
        String usageUnit = inferUsageUnit(serviceType, eventType);
        
        Instant eventTime = getCsvInstantValue(values, headerMap, 
                "event_time", "timestamp", "start_time");
        if (eventTime == null) {
            eventTime = Instant.now();
        }
        
        String sourceSystem = getCsvValue(values, headerMap, "source_system", "network");
        
        UsageEvent event = UsageEvent.builder()
                .eventId(eventId)
                .subscriptionId(null)
                .serviceType(serviceType)
                .eventType(eventType)
                .usageValue(usageValue)
                .usageUnit(usageUnit)
                .eventTime(eventTime)
                .sourceSystem(sourceSystem != null ? sourceSystem : "CDR_PARSER")
                .build();
        
        return event;
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());
        
        return values.toArray(new String[0]);
    }

    private String getStringValue(ObjectNode node, String... keys) {
        for (String key : keys) {
            if (node.has(key) && !node.get(key).isNull()) {
                String value = node.get(key).asText();
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            }
        }
        return null;
    }

    private BigDecimal getDecimalValue(ObjectNode node, String... keys) {
        for (String key : keys) {
            if (node.has(key) && !node.get(key).isNull()) {
                try {
                    if (node.get(key).isNumber()) {
                        return new BigDecimal(node.get(key).asText());
                    } else {
                        String value = node.get(key).asText();
                        if (value != null && !value.isEmpty()) {
                            return new BigDecimal(value);
                        }
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        return null;
    }

    private Instant getInstantValue(ObjectNode node, String... keys) {
        for (String key : keys) {
            if (node.has(key) && !node.get(key).isNull()) {
                try {
                    String value = node.get(key).asText();
                    return parseTimestamp(value);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return null;
    }

    private UUID extractSubscriptionId(ObjectNode node) {
        String subId = getStringValue(node, "subscription_id", "subscriptionId", "account_id", "accountId");
        if (subId == null) {
            return null;
        }
        try {
            return UUID.fromString(subId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String getCsvValue(String[] values, Map<String, Integer> headerMap, String... keys) {
        for (String key : keys) {
            Integer idx = headerMap.get(key);
            if (idx != null && idx < values.length) {
                String value = values[idx];
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
        }
        return null;
    }

    private BigDecimal getCsvDecimalValue(String[] values, Map<String, Integer> headerMap, String... keys) {
        String value = getCsvValue(values, headerMap, keys);
        if (value == null) return null;
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Instant getCsvInstantValue(String[] values, Map<String, Integer> headerMap, String... keys) {
        String value = getCsvValue(values, headerMap, keys);
        if (value == null) return null;
        return parseTimestamp(value);
    }

    private Instant parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }
        
        try {
            if (timestamp.matches("\\d+")) {
                long millis = Long.parseLong(timestamp);
                if (timestamp.length() == 10) {
                    millis *= 1000;
                }
                return Instant.ofEpochMilli(millis);
            }
            
            try {
                return Instant.parse(timestamp);
            } catch (Exception e) {
            }
            
            try {
                return Instant.from(CDR_DATE_FORMAT.parse(timestamp));
            } catch (DateTimeParseException e) {
            }
            
            return Instant.now();
            
        } catch (Exception e) {
            log.warn("Failed to parse timestamp: {}", timestamp);
            return Instant.now();
        }
    }

    private String normalizeServiceType(String serviceType) {
        if (serviceType == null) {
            return "USAGE";
        }
        
        String normalized = serviceType.toUpperCase().trim();
        
        switch (normalized) {
            case "VOICE": case "CALL": case "TEL": case "TELEPHONY":
                return "VOICE";
            case "SMS": case "TEXT": case "MESSAGE":
                return "SMS";
            case "DATA": case "INTERNET": case "BROADBAND": case "GPRS": case "LTE":
            case "4G": case "5G":
                return "DATA";
            case "ADSL": case "VDSL":
                return "ADSL";
            case "FTTH": case "FIBER":
                return "FTTH";
            case "MMS": case "MULTIMEDIA":
                return "MMS";
            case "VAS": case "VALUE_ADDED":
                return "VAS";
            case "ROAMING":
                return "ROAMING";
            default:
                return "USAGE";
        }
    }

    private String normalizeEventType(String eventType) {
        if (eventType == null) {
            return "USAGE";
        }
        
        String normalized = eventType.toUpperCase().trim().replace(" ", "_");
        
        if (VALID_EVENT_TYPES.contains(normalized)) {
            return normalized;
        }
        
        switch (normalized) {
            case "START": case "INITIATE": case "CONNECT":
                return "CALL_START";
            case "END": case "TERMINATE": case "DISCONNECT":
                return "CALL_END";
            case "SEND": case "SUBMIT":
                return "SMS_SENT";
            case "RECEIVE": case "DELIVER":
                return "SMS_RECEIVED";
            case "DATA_START": case "SESSION_START":
                return "DATA_SESSION_START";
            case "DATA_END": case "SESSION_END":
                return "DATA_SESSION_END";
            case "RECURRING": case "MONTHLY": case "SUBSCRIPTION":
                return "SUBSCRIPTION_FEE";
            default:
                return "USAGE";
        }
    }

    private String inferUsageUnit(String serviceType, String eventType) {
        switch (serviceType) {
            case "VOICE":
                return "SEC";
            case "SMS":
            case "MMS":
                return "SMS";
            case "DATA":
            case "LTE":
            case "4G":
            case "5G":
                return "MB";
            case "ADSL":
            case "FTTH":
                return "MB";
            default:
                return "UNIT";
        }
    }

    public boolean validateCdr(String cdr) {
        if (cdr == null || cdr.trim().isEmpty()) {
            return false;
        }
        
        if (cdr.startsWith("{")) {
            try {
                ObjectNode node = objectMapper.readValue(cdr, ObjectNode.class);
                return node.has("event_id") || node.has("cdr_id") || node.has("id");
            } catch (Exception e) {
                return false;
            }
        }
        
        if (cdr.contains(",")) {
            String[] lines = cdr.split("\n");
            return lines.length >= 2;
        }
        
        return false;
    }
}
