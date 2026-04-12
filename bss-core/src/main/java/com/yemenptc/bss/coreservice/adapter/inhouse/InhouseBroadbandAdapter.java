package com.yemenptc.bss.coreservice.adapter.inhouse;

import com.yemenptc.bss.coreservice.adapter.ExternalAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings("unchecked")
@Component
@Slf4j
public class InhouseBroadbandAdapter implements ExternalAdapter {

    @Value("${inhouse-broadband.api.base-url:http://broadband.internal:8080}")
    private String baseUrl;

    @Value("${inhouse-broadband.api.key:broadband_api_key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public InhouseBroadbandAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean healthCheck() {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/api/health",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getAdapterId() {
        return "inhouse-broadband";
    }

    public Map<String, Object> createCustomer(String username, String customerName, String phone,
                                               String email, String serviceType, String speedProfile) {
        Map<String, Object> body = Map.of(
            "username", username, "customerName", customerName, "phone", phone,
            "email", email, "serviceType", serviceType, "speedProfile", speedProfile
        );
        return post("/api/customer", body);
    }

    public Map<String, Object> activateService(String username, String serviceType,
                                                String speedProfile, String vlanId, String ipAddress) {
        Map<String, Object> body = Map.of(
            "username", username, "serviceType", serviceType, "speedProfile", speedProfile,
            "vlanId", vlanId, "ipAddress", ipAddress
        );
        return post("/api/service/activate", body);
    }

    public Map<String, Object> suspendService(String username, String reason) {
        return post("/api/service/suspend", Map.of("username", username, "action", "SUSPEND", "reason", reason));
    }

    public Map<String, Object> resumeService(String username) {
        return post("/api/service/resume", Map.of("username", username, "action", "RESUME"));
    }

    public Map<String, Object> getBalance(String username) {
        return get("/api/account/balance?username=" + username);
    }

    public Map<String, Object> recharge(String username, BigDecimal amount, String voucherCode, String paymentMethod) {
        return post("/api/account/recharge", Map.of(
            "username", username, "amount", amount, "voucherCode", voucherCode, "paymentMethod", paymentMethod
        ));
    }

    public Map<String, Object> authenticateRadius(String username, String password, String nasIp, String callingStationId) {
        return post("/api/auth/radius", Map.of(
            "username", username, "password", password, "nasIp", nasIp, "callingStationId", callingStationId
        ));
    }

    public Map<String, Object> provisionDslam(String dslamId, String portNumber, String vlanId,
                                               String speedProfile, String username) {
        return post("/api/line/provision", Map.of(
            "dslamId", dslamId, "portNumber", portNumber, "vlanId", vlanId,
            "speedProfile", speedProfile, "username", username
        ));
    }

    public Map<String, Object> provisionFtth(String ontSerial, String oltId, String vlanId,
                                              String speedProfile, String username) {
        return post("/api/ont/provision", Map.of(
            "ontSerial", ontSerial, "oltId", oltId, "vlanId", vlanId,
            "speedProfile", speedProfile, "username", username
        ));
    }

    private Map<String, Object> post(String path, Map<String, Object> body) {
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + path, HttpMethod.POST,
            new HttpEntity<>(body, createHeaders()), Map.class
        );
        return response.getBody();
    }

    private Map<String, Object> get(String path) {
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + path, HttpMethod.GET,
            new HttpEntity<>(createHeaders()), Map.class
        );
        return response.getBody();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);
        return headers;
    }
}
