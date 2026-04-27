package com.yemenptc.bss.coreservice.adapter.oracle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Oracle BRM Adapter
 * Tests account management, balance operations, and service provisioning
 */
@ExtendWith(MockitoExtension.class)
class OracleBrmAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OracleBrmAdapter adapter;

    @BeforeEach
    void setUp() {
        // Note: In production, these would come from Kubernetes secrets
        System.setProperty("oracle-brm.api.base-url", "http://test-brm:8080");
        System.setProperty("oracle-brm.api.username", "test-user");
        System.setProperty("oracle-brm.api.password", "test-pass");
    }

    @Test
    void healthCheck_WhenServiceUp_ReturnsTrue() {
        ResponseEntity<String> response = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(response);

        boolean result = adapter.healthCheck();

        assertTrue(result);
    }

    @Test
    void healthCheck_WhenServiceDown_ReturnsFalse() {
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RuntimeException("Connection refused"));

        boolean result = adapter.healthCheck();

        assertFalse(result);
    }

    @Test
    void getAdapterId_ReturnsOracleBrm() {
        assertEquals("oracle-brm", adapter.getAdapterId());
    }

    @Test
    void createAccount_Success() {
        Map<String, Object> expectedResponse = Map.of(
            "accountNo", "ACC-001",
            "status", "ACTIVE"
        );
        
        ResponseEntity<Map> response = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        Map<String, Object> result = adapter.createAccount(
            "ACC-001", "John", "Doe", "john@example.com", "+967123456789", "MOBILE", "PLAN-001");

        assertNotNull(result);
        assertEquals("ACC-001", result.get("accountNo"));
    }

    @Test
    void createAccount_WhenServiceFails_ThrowsException() {
        ResponseEntity<Map> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        assertThrows(OracleBrmException.class, () -> {
            adapter.createAccount("ACC-001", "John", "Doe", "john@example.com", "+967123456789", "MOBILE", "PLAN-001");
        });
    }

    @Test
    void getBalance_Success() {
        Map<String, Object> expectedResponse = Map.of(
            "balance", BigDecimal.valueOf(1000.00),
            "currency", "YER"
        );
        
        ResponseEntity<Map> response = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        Map<String, Object> result = adapter.getBalance("ACC-001");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000.00), result.get("balance"));
    }

    @Test
    void recharge_Success() {
        Map<String, Object> expectedResponse = Map.of(
            "accountNo", "ACC-001",
            "amount", BigDecimal.valueOf(500.00),
            "newBalance", BigDecimal.valueOf(1500.00)
        );
        
        ResponseEntity<Map> response = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        Map<String, Object> result = adapter.recharge("ACC-001", BigDecimal.valueOf(500.00), "CASH", "REF-001");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1500.00), result.get("newBalance"));
    }

    @Test
    void activateService_Success() {
        Map<String, Object> expectedResponse = Map.of(
            "accountNo", "ACC-001",
            "serviceType", "4G",
            "status", "ACTIVE"
        );
        
        ResponseEntity<Map> response = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        Map<String, Object> result = adapter.activateService("ACC-001", "4G");

        assertNotNull(result);
        assertEquals("ACTIVE", result.get("status"));
    }

    @Test
    void suspendService_Success() {
        Map<String, Object> expectedResponse = Map.of(
            "accountNo", "ACC-001",
            "action", "SUSPEND",
            "status", "SUSPENDED"
        );
        
        ResponseEntity<Map> response = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        Map<String, Object> result = adapter.suspendService("ACC-001", "PAYMENT_OVERDUE");

        assertNotNull(result);
        assertEquals("SUSPENDED", result.get("status"));
    }

    @Test
    void resumeService_Success() {
        Map<String, Object> expectedResponse = Map.of(
            "accountNo", "ACC-001",
            "action", "RESUME",
            "status", "ACTIVE"
        );
        
        ResponseEntity<Map> response = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        Map<String, Object> result = adapter.resumeService("ACC-001");

        assertNotNull(result);
        assertEquals("ACTIVE", result.get("status"));
    }

    @Test
    void terminateService_Success() {
        Map<String, Object> expectedResponse = Map.of(
            "accountNo", "ACC-001",
            "action", "TERMINATE",
            "status", "TERMINATED"
        );
        
        ResponseEntity<Map> response = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        Map<String, Object> result = adapter.terminateService("ACC-001", "CUSTOMER_REQUEST");

        assertNotNull(result);
        assertEquals("TERMINATED", result.get("status"));
    }
}
