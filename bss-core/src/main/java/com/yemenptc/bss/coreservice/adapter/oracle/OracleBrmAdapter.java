package com.yemenptc.bss.coreservice.adapter.oracle;

import com.yemenptc.bss.coreservice.adapter.ExternalAdapter;
import com.yemenptc.bss.coreservice.adapter.oracle.OracleBrmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings("unchecked")

/**
 * Oracle BRM Adapter - Real Implementation for 4G/LTE Billing System
 * 
 * Connects to Oracle BRM via REST API wrapper for:
 * - Account management (create, query, update)
 * - Balance operations (query, recharge, adjust)
 * - Rating operations (real-time charging, batch rating)
 * - Service provisioning (activate, suspend, resume, terminate)
 */
@Component
@Slf4j
public class OracleBrmAdapter implements ExternalAdapter {

    @Value("${oracle-brm.api.base-url}")
    private String baseUrl;

    @Value("${oracle-brm.api.username}")
    private String username;

    @Value("${oracle-brm.api.password}")
    private String password;
    
    @Value("${oracle-brm.api.timeout:30000}")
    private int apiTimeout;

    private final RestTemplate restTemplate;

    public OracleBrmAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean healthCheck() {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/health",
                HttpMethod.GET,
            new HttpEntity<>(createAuthHeaders()),
                String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Oracle BRM health check failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getAdapterId() {
        return "oracle-brm";
    }

    /**
     * Create account in Oracle BRM
     * 
     * API: POST /account
     * Body: { accountNo, firstName, lastName, email, phone, serviceType, planCode }
     */
    public Map<String, Object> createAccount(String accountNo, String firstName, String lastName, 
                                              String email, String phone, String serviceType, String planCode) {
        log.info("Creating account in Oracle BRM: {}", accountNo);
        
        Map<String, Object> body = Map.of(
            "accountNo", accountNo,
            "firstName", firstName,
            "lastName", lastName,
            "email", email,
            "phone", phone,
            "serviceType", serviceType,
            "planCode", planCode
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/account",
            HttpMethod.POST,
            new HttpEntity<Map<String, Object>>(body, createAuthHeaders()),
            Map.class
        );
        
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new OracleBrmException("Failed to create account in Oracle BRM");
        }
    }

    /**
     * Get account balance from Oracle BRM
     * 
     * API: GET /account/{accountNo}/balance
     */
    public Map<String, Object> getBalance(String accountNo) {
        log.info("Getting balance for account: {}", accountNo);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/account/{accountNo}/balance",
            HttpMethod.GET,
            new HttpEntity<>(createAuthHeaders()),
            Map.class,
            accountNo
        );
        
        return response.getBody();
    }

    /**
     * Recharge account in Oracle BRM
     * 
     * API: POST /account/recharge
     * Body: { accountNo, amount, paymentMethod, referenceId }
     */
    public Map<String, Object> recharge(String accountNo, BigDecimal amount, String paymentMethod, String referenceId) {
        log.info("Recharging account: {} amount: {}", accountNo, amount);
        
        Map<String, Object> body = Map.of(
            "accountNo", accountNo,
            "amount", amount,
            "paymentMethod", paymentMethod,
            "referenceId", referenceId
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/account/recharge",
            HttpMethod.POST,
            new HttpEntity<Map<String, Object>>(body, createAuthHeaders()),
            Map.class
        );
        
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new OracleBrmException("Failed to recharge account in Oracle BRM");
        }
    }

    /**
     * Rate usage event in Oracle BRM
     * 
     * API: POST /rating/charge
     * Body: { eventId, subscriptionId, serviceType, eventType, usageValue, usageUnit }
     */
    public Map<String, Object> rateUsage(String eventId, String subscriptionId, String serviceType, 
                                          String eventType, BigDecimal usageValue, String usageUnit) {
        log.debug("Rating usage event: {}", eventId);
        
        Map<String, Object> body = Map.of(
            "eventId", eventId,
            "subscriptionId", subscriptionId,
            "serviceType", serviceType,
            "eventType", eventType,
            "usageValue", usageValue,
            "usageUnit", usageUnit
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/rating/charge",
            HttpMethod.POST,
            new HttpEntity<Map<String, Object>>(body, createAuthHeaders()),
            Map.class
        );
        
        return response.getBody();
    }

    /**
     * Activate service in Oracle BRM
     * 
     * API: POST /service/activate
     * Body: { accountNo, serviceType }
     */
    public Map<String, Object> activateService(String accountNo, String serviceType) {
        log.info("Activating service in Oracle BRM: {} type: {}", accountNo, serviceType);
        
        Map<String, Object> body = Map.of(
            "accountNo", accountNo,
            "serviceType", serviceType,
            "action", "ACTIVATE"
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/service/activate",
            HttpMethod.POST,
            new HttpEntity<Map<String, Object>>(body, createAuthHeaders()),
            Map.class
        );
        
        return response.getBody();
    }

    /**
     * Suspend service in Oracle BRM
     * 
     * API: POST /service/suspend
     * Body: { accountNo, reason }
     */
    public Map<String, Object> suspendService(String accountNo, String reason) {
        log.info("Suspending service in Oracle BRM: {}", accountNo);
        
        Map<String, Object> body = Map.of(
            "accountNo", accountNo,
            "action", "SUSPEND",
            "reason", reason
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/service/suspend",
            HttpMethod.POST,
            new HttpEntity<Map<String, Object>>(body, createAuthHeaders()),
            Map.class
        );
        
        return response.getBody();
    }

    /**
     * Resume service in Oracle BRM
     * 
     * API: POST /service/resume
     * Body: { accountNo }
     */
    public Map<String, Object> resumeService(String accountNo) {
        log.info("Resuming service in Oracle BRM: {}", accountNo);
        
        Map<String, Object> body = Map.of(
            "accountNo", accountNo,
            "action", "RESUME"
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/service/resume",
            HttpMethod.POST,
            new HttpEntity<Map<String, Object>>(body, createAuthHeaders()),
            Map.class
        );
        
        return response.getBody();
    }

    /**
     * Terminate service in Oracle BRM
     * 
     * API: POST /service/terminate
     * Body: { accountNo, reason }
     */
    public Map<String, Object> terminateService(String accountNo, String reason) {
        log.info("Terminating service in Oracle BRM: {}", accountNo);
        
        Map<String, Object> body = Map.of(
            "accountNo", accountNo,
            "action", "TERMINATE",
            "reason", reason
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/service/terminate",
            HttpMethod.POST,
            new HttpEntity<Map<String, Object>>(body, createAuthHeaders()),
            Map.class
        );
        
        return response.getBody();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);
        return headers;
    }
}
