package com.yemenptc.bss.coreservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Charging Engine Client
 * 
 * REST client for Go-based charging service
 * Provides balance management, reservations, and CDR processing
 * 
 * Base URL: http://charging-engine:8081
 * 
 * @author Yemen PTC BSS Team
 * @version 1.0.0
 */
@Component
@Slf4j
public class ChargingEngineClient {

    @Value("${charging-engine.url:http://charging-engine:8081}")
    private String baseUrl;

    @Value("${charging-engine.timeout:10000}")
    private int timeout;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ChargingEngineClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Get account balance from charging engine
     * 
     * @param accountId Account identifier
     * @return Balance information
     */
    public BalanceResponse getBalance(String accountId) {
        log.debug("Fetching balance for account: {}", accountId);
        
        try {
            ResponseEntity<BalanceResponse> response = restTemplate.exchange(
                baseUrl + "/api/v1/balance/{accountId}",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                BalanceResponse.class,
                accountId
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new ChargingEngineException("Failed to fetch balance: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error fetching balance for account {}: {}", accountId, e.getMessage());
            throw new ChargingEngineException("Balance fetch failed", e);
        }
    }

    /**
     * Reserve balance for charging
     * 
     * @param accountId Account identifier
     * @param amount Amount to reserve
     * @param currency Currency code
     * @return Reservation response with reservation ID
     */
    public ReservationResponse reserveBalance(String accountId, BigDecimal amount, String currency) {
        log.info("Reserving balance for account: {}, amount: {} {}", accountId, amount, currency);
        
        Map<String, Object> request = Map.of(
            "amount", amount.doubleValue(),
            "currency", currency
        );
        
        try {
            ResponseEntity<ReservationResponse> response = restTemplate.exchange(
                baseUrl + "/api/v1/balance/{accountId}/reserve",
                HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()),
                ReservationResponse.class,
                accountId
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ChargingEngineException("Failed to reserve balance: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error reserving balance for account {}: {}", accountId, e.getMessage());
            throw new ChargingEngineException("Balance reservation failed", e);
        }
    }

    /**
     * Confirm a reservation (finalize charging)
     * 
     * @param accountId Account identifier
     * @param reservationId Reservation ID
     * @param amount Amount to confirm
     * @return Confirmation response
     */
    public ConfirmationResponse confirmReservation(String accountId, String reservationId, BigDecimal amount) {
        log.info("Confirming reservation: {} for account: {}", reservationId, accountId);
        
        Map<String, Object> request = Map.of(
            "reservationId", reservationId,
            "amount", amount.doubleValue()
        );
        
        try {
            ResponseEntity<ConfirmationResponse> response = restTemplate.exchange(
                baseUrl + "/api/v1/balance/{accountId}/confirm",
                HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()),
                ConfirmationResponse.class,
                accountId
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new ChargingEngineException("Failed to confirm reservation: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error confirming reservation {}: {}", reservationId, e.getMessage());
            throw new ChargingEngineException("Reservation confirmation failed", e);
        }
    }

    /**
     * Health check for charging engine
     * 
     * @return true if service is healthy
     */
    public boolean isHealthy() {
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/health",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                Map.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Charging engine health check failed: {}", e.getMessage());
            return false;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Service-Name", "bss-core");
        return headers;
    }

    // Response DTOs
    public record BalanceResponse(
        String accountId,
        Double mainBalance,
        Double bonusBalance,
        String currency,
        String lastUpdated,
        Double reservedTotal
    ) {}

    public record ReservationResponse(
        String reservationId,
        String status
    ) {}

    public record ConfirmationResponse(
        String status,
        String message
    ) {}

    public static class ChargingEngineException extends RuntimeException {
        public ChargingEngineException(String message) {
            super(message);
        }
        
        public ChargingEngineException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
