package com.yemenptc.bss.coreservice.adapter.titan;

import com.yemenptc.bss.coreservice.adapter.ExternalAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TITAN Adapter - Real TL1 Implementation for Landline Voice System
 * 
 * TL1 (Transaction Language 1) is a man-machine language used for telecommunications equipment.
 * This adapter connects to the TITAN system via TCP socket and sends TL1 commands.
 */
@Component
@Slf4j
public class TitanAdapter implements ExternalAdapter {

    @Value("${titan.tl1.host:localhost}")
    private String tl1Host;

    @Value("${titan.tl1.port:2362}")
    private int tl1Port;

    @Value("${titan.tl1.timeout:30000}")
    private int timeout;

    private final Map<String, Socket> connectionPool = new ConcurrentHashMap<>();

    @Override
    public boolean healthCheck() {
        try (Socket socket = new Socket(tl1Host, tl1Port)) {
            socket.setSoTimeout(timeout);
            return socket.isConnected();
        } catch (IOException e) {
            log.error("TITAN health check failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getAdapterId() {
        return "titan-tl1";
    }

    /**
     * Send TL1 command to TITAN system
     * 
     * TL1 Command Format:
     * TID:OSS01;CTAG:001;COMMAND::PARAMETERS;
     * 
     * Response Format:
     * TID:TITAN01;CTAG:001;COMPLD:::;
     */
    public String sendTl1Command(String command) {
        log.debug("Sending TL1 command: {}", command);
        
        try (Socket socket = new Socket(tl1Host, tl1Port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            socket.setSoTimeout(timeout);
            
            // Send command
            out.println(command);
            out.flush();
            
            // Read response
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
                if (line.contains("COMPLD") || line.contains("DENY") || line.contains("ABORT")) {
                    break;
                }
            }
            
            log.debug("TL1 response: {}", response);
            return response.toString();
            
        } catch (IOException e) {
            log.error("TL1 command failed: {}", e.getMessage());
            throw new TitanConnectionException("Failed to execute TL1 command", e);
        }
    }

    /**
     * Create subscriber in TITAN system
     * 
     * TL1 Command: ENT-SUB::MSISDN,PLAN,CUSTID;
     */
    public Map<String, Object> createSubscriber(String msisdn, String planCode, String customerId) {
        log.info("Creating subscriber in TITAN: msisdn={}, plan={}", msisdn, planCode);
        
        String command = String.format(
            "TID:TITAN01;CTAG:%s;ENT-SUB::MSISDN=%s,PLAN=%s,CUSTID=%s;",
            generateTag(), msisdn, planCode, customerId
        );
        
        String response = sendTl1Command(command);
        
        if (response.contains("COMPLD")) {
            log.info("Subscriber created successfully: {}", msisdn);
            return Map.of(
                "msisdn", msisdn,
                "status", "CREATED",
                "planCode", planCode,
                "customerId", customerId
            );
        } else {
            log.error("Failed to create subscriber: {}", response);
            throw new TitanProvisioningException("Failed to create subscriber: " + response);
        }
    }

    /**
     * Suspend subscriber in TITAN system
     * 
     * TL1 Command: INH-SUB::MSISDN,REASON;
     */
    public Map<String, Object> suspendSubscriber(String msisdn, String reason) {
        log.info("Suspending subscriber in TITAN: msisdn={}, reason={}", msisdn, reason);
        
        String command = String.format(
            "TID:TITAN01;CTAG:%s;INH-SUB::MSISDN=%s,REASON=%s;",
            generateTag(), msisdn, reason
        );
        
        String response = sendTl1Command(command);
        
        if (response.contains("COMPLD")) {
            return Map.of("msisdn", msisdn, "status", "SUSPENDED");
        } else {
            throw new TitanProvisioningException("Failed to suspend subscriber: " + response);
        }
    }

    /**
     * Resume subscriber in TITAN system
     * 
     * TL1 Command: ACT-SUB::MSISDN;
     */
    public Map<String, Object> resumeSubscriber(String msisdn) {
        log.info("Resuming subscriber in TITAN: msisdn={}", msisdn);
        
        String command = String.format(
            "TID:TITAN01;CTAG:%s;ACT-SUB::MSISDN=%s;",
            generateTag(), msisdn
        );
        
        String response = sendTl1Command(command);
        
        if (response.contains("COMPLD")) {
            return Map.of("msisdn", msisdn, "status", "RESUMED");
        } else {
            throw new TitanProvisioningException("Failed to resume subscriber: " + response);
        }
    }

    /**
     * Terminate subscriber in TITAN system
     * 
     * TL1 Command: CANC-SUB::MSISDN;
     */
    public Map<String, Object> terminateSubscriber(String msisdn) {
        log.info("Terminating subscriber in TITAN: msisdn={}", msisdn);
        
        String command = String.format(
            "TID:TITAN01;CTAG:%s;CANC-SUB::MSISDN=%s;",
            generateTag(), msisdn
        );
        
        String response = sendTl1Command(command);
        
        if (response.contains("COMPLD")) {
            return Map.of("msisdn", msisdn, "status", "TERMINATED");
        } else {
            throw new TitanProvisioningException("Failed to terminate subscriber: " + response);
        }
    }

    /**
     * Reserve number from TITAN number pool
     * 
     * TL1 Command: RTRV-NUM::EXCHANGE,TYPE;
     */
    public Map<String, Object> reserveNumber(String exchange, String numberType) {
        log.info("Reserving number from TITAN: exchange={}, type={}", exchange, numberType);
        
        String command = String.format(
            "TID:TITAN01;CTAG:%s;RTRV-NUM::EXCHANGE=%s,TYPE=%s;",
            generateTag(), exchange, numberType
        );
        
        String response = sendTl1Command(command);
        
        // Parse response to extract number
        String number = extractNumber(response);
        
        if (number != null) {
            return Map.of("number", number, "status", "RESERVED");
        } else {
            throw new TitanProvisioningException("No available numbers: " + response);
        }
    }

    /**
     * Query subscriber status from TITAN
     * 
     * TL1 Command: RTRV-SUB::MSISDN;
     */
    public Map<String, Object> querySubscriber(String msisdn) {
        log.info("Querying subscriber in TITAN: msisdn={}", msisdn);
        
        String command = String.format(
            "TID:TITAN01;CTAG:%s;RTRV-SUB::MSISDN=%s;",
            generateTag(), msisdn
        );
        
        String response = sendTl1Command(command);
        
        return parseSubscriberResponse(response);
    }

    private String generateTag() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String extractNumber(String response) {
        // Parse TL1 response to extract number
        // Format: MSISDN=1234567890
        int start = response.indexOf("MSISDN=");
        if (start == -1) return null;
        
        start += 7;
        int end = response.indexOf(",", start);
        if (end == -1) end = response.indexOf(";", start);
        
        return response.substring(start, end);
    }

    private Map<String, Object> parseSubscriberResponse(String response) {
        return Map.of(
            "status", response.contains("ACTIVE") ? "ACTIVE" : "INACTIVE",
            "plan", extractValue(response, "PLAN"),
            "balance", extractValue(response, "BALANCE")
        );
    }

    private String extractValue(String response, String key) {
        int start = response.indexOf(key + "=");
        if (start == -1) return "";
        
        start += key.length() + 1;
        int end = response.indexOf(",", start);
        if (end == -1) end = response.indexOf(";", start);
        if (end == -1) end = response.length();
        
        return response.substring(start, end).trim();
    }
}
