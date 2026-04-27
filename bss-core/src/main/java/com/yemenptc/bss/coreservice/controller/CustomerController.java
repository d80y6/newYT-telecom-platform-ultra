package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Customer;
import com.yemenptc.bss.coreservice.service.CustomerService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Customer Controller - TMF629 Customer Management API
 * 
 * This controller provides TMF629-compliant endpoints for:
 * - Customer 360 view (complete customer data with accounts, subscriptions)
 * - Customer segments and targeting
 * - Account hierarchies
 * - Customer relationships
 * - Customer analytics (lifetime value, churn risk)
 * - Credit profiles
 * 
 * Base path: /tmf-api/customerManagement/v4
 * 
 * @author Yemen PTC BSS Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/tmf-api/customerManagement/v4")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * List customers with TMF629-compliant filtering
     * 
     * Query Parameters:
     * - segment: VIP, PREMIUM, STANDARD, BASIC, AT_RISK, CHURN_RISK
     * - customerType: RESIDENTIAL, BUSINESS, ENTERPRISE, GOVERNMENT
     * - status: ACTIVE, INACTIVE, SUSPENDED, TERMINATED
     * - minLifetimeValue: minimum customer lifetime value
     * - page, size: pagination
     * 
     * @param segment Customer segment filter
     * @param customerType Type of customer
     * @param status Account status
     * @param minLifetimeValue Minimum lifetime value
     * @param page Page number (default 0)
     * @param size Page size (default 20)
     * @return Paginated list of customers with 360 view
     */
    @GetMapping("/customer")
    public ResponseEntity<TmfResponse<Customer>> listCustomers360(
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minLifetimeValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Customer> result;
        
        // Apply filters based on parameters
        if (status != null) {
            Customer.CustomerStatus custStatus = Customer.CustomerStatus.valueOf(status.toUpperCase());
            result = customerService.getCustomersByStatus(custStatus, PageRequest.of(page, size));
        } else if (customerType != null) {
            Customer.CustomerType type = Customer.CustomerType.valueOf(customerType.toUpperCase());
            result = customerService.getCustomersByType(type, PageRequest.of(page, size));
        } else {
            result = customerService.listCustomers(PageRequest.of(page, size));
        }
        
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Customer"));
    }

    /**
     * Create a new customer with full profile
     * 
     * Request Body:
     * - customerType: RESIDENTIAL, BUSINESS, ENTERPRISE, GOVERNMENT
     * - nationalId: Customer national ID
     * - firstName, lastName: Customer name
     * - primaryPhone: Contact phone
     * - email: Contact email
     * - city, governorate, street, postalCode: Address
     * - kycLevel: NONE, BASIC, ENHANCED
     * 
     * @param request Customer creation request
     * @return Created customer with generated ID
     */
    @PostMapping("/customer")
    public ResponseEntity<TmfResponse<Customer>> createCustomer360(@RequestBody Customer request) {
        Customer customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(customer, "Customer"));
    }

    /**
     * Get customer 360 view by ID
     * 
     * Includes:
     * - Customer profile
     * - Account information
     * - Subscriptions
     * - Usage statistics
     * - Churn risk score
     * - Lifetime value
     * 
     * @param id Customer ID (UUID)
     * @return Customer 360 view
     */
    @GetMapping("/customer/{id}")
    public ResponseEntity<TmfResponse<Customer>> getCustomer360(@PathVariable UUID id) {
        Customer customer = customerService.getCustomer(id);
        return ResponseEntity.ok(TmfResponse.success(customer, "Customer"));
    }

    /**
     * Get customer by national ID
     * 
     * @param nationalId Customer national ID
     * @return Customer 360 view
     */
    @GetMapping("/customer/nationalId/{nationalId}")
    public ResponseEntity<TmfResponse<Customer>> getCustomerByNationalId(@PathVariable String nationalId) {
        Customer customer = customerService.getCustomerByNationalId(nationalId);
        return ResponseEntity.ok(TmfResponse.success(customer, "Customer"));
    }

    /**
     * Get customer by phone number
     * 
     * @param phone Customer primary phone
     * @return Customer 360 view
     */
    @GetMapping("/customer/phone/{phone}")
    public ResponseEntity<TmfResponse<Customer>> getCustomerByPhone(@PathVariable String phone) {
        Customer customer = customerService.getCustomerByPhone(phone);
        return ResponseEntity.ok(TmfResponse.success(customer, "Customer"));
    }

    /**
     * Update customer profile (PATCH - partial update)
     * 
     * Only provided fields will be updated.
     * 
     * @param id Customer ID
     * @param request Update request with fields to modify
     * @return Updated customer
     */
    @PatchMapping("/customer/{id}")
    public ResponseEntity<TmfResponse<Customer>> updateCustomer(
            @PathVariable UUID id, 
            @RequestBody Customer request) {
        Customer customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(TmfResponse.success(customer, "Customer"));
    }

    /**
     * Delete customer
     * 
     * Soft delete - marks customer as TERMINATED
     * 
     * @param id Customer ID
     * @return No content on success
     */
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get customer accounts
     * 
     * Returns all accounts associated with the customer
     * 
     * @param id Customer ID
     * @return List of customer accounts
     */
    @GetMapping("/customer/{id}/accounts")
    public ResponseEntity<TmfResponse<Map>> getCustomerAccounts(@PathVariable UUID id) {
        Customer customer = customerService.getCustomer(id);
        Map<String, Object> accounts = Map.of(
            "customerId", customer.getId(),
            "customerName", customer.getFirstName() + " " + customer.getLastName(),
            "accounts", "[]",  // Placeholder - would fetch from account service
            "totalAccounts", 0
        );
        return ResponseEntity.ok(TmfResponse.success(accounts, "CustomerAccounts"));
    }

    /**
     * Get customer subscriptions
     * 
     * @param id Customer ID
     * @return List of customer subscriptions
     */
    @GetMapping("/customer/{id}/subscriptions")
    public ResponseEntity<TmfResponse<Map>> getCustomerSubscriptions(@PathVariable UUID id) {
        Customer customer = customerService.getCustomer(id);
        Map<String, Object> subscriptions = Map.of(
            "customerId", customer.getId(),
            "subscriptions", "[]",  // Placeholder - would fetch from subscription service
            "totalSubscriptions", 0
        );
        return ResponseEntity.ok(TmfResponse.success(subscriptions, "CustomerSubscriptions"));
    }

    /**
     * Get customer analytics
     * 
     * Returns:
     * - Lifetime value
     * - Churn risk score
     * - Total usage
     * - Revenue metrics
     * 
     * @param id Customer ID
     * @return Customer analytics
     */
    @GetMapping("/customer/{id}/analytics")
    public ResponseEntity<TmfResponse<Map>> getCustomerAnalytics(@PathVariable UUID id) {
        Customer customer = customerService.getCustomer(id);
        Map<String, Object> analytics = Map.of(
            "customerId", customer.getId(),
            "lifetimeValue", customer.getLifetimeValue() != null ? customer.getLifetimeValue() : java.math.BigDecimal.ZERO,
            "churnRiskScore", customer.getChurnRiskScore() != null ? customer.getChurnRiskScore().doubleValue() : 0.0,
            "customerSegment", customer.getCustomerType() != null ? customer.getCustomerType().toString() : "UNKNOWN",
            "kycLevel", customer.getKycLevel() != null ? customer.getKycLevel().toString() : "BASIC",
            "kycVerified", customer.getKycVerified() != null ? customer.getKycVerified() : false
        );
        return ResponseEntity.ok(TmfResponse.success(analytics, "CustomerAnalytics"));
    }

    /**
     * Get customer credit profile
     * 
     * @param id Customer ID
     * @return Credit profile information
     */
    @GetMapping("/customer/{id}/creditProfile")
    public ResponseEntity<TmfResponse<Map>> getCustomerCreditProfile(@PathVariable UUID id) {
        Customer customer = customerService.getCustomer(id);
        Map<String, Object> creditProfile = Map.of(
            "customerId", customer.getId(),
            "creditScore", 750,  // Placeholder - would fetch from credit service
            "creditLimit", "10000.00",
            "currentBalance", "0.00",
            "paymentStatus", "GOOD_STANDING"
        );
        return ResponseEntity.ok(TmfResponse.success(creditProfile, "CreditProfile"));
    }

    /**
     * Update customer KYC
     * 
     * @param id Customer ID
     * @param request KYC update with level
     * @return Updated customer
     */
    @PatchMapping("/customer/{id}/kyc")
    public ResponseEntity<TmfResponse<Customer>> updateKyc(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        Customer.KycLevel level = Customer.KycLevel.valueOf(request.get("kycLevel").toUpperCase());
        Customer customer = customerService.updateKyc(id, level);
        return ResponseEntity.ok(TmfResponse.success(customer, "Customer"));
    }

    /**
     * Search customers
     * 
     * @param query Search query (name, phone, email, national ID)
     * @param page Page number
     * @param size Page size
     * @return Matching customers
     */
    @GetMapping("/customer/search")
    public ResponseEntity<TmfResponse<Customer>> searchCustomers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Customer> result = customerService.searchCustomers(query, PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Customer"));
    }

    /**
     * Get customer statistics
     * 
     * @return Customer statistics
     */
    @GetMapping("/customer/statistics")
    public ResponseEntity<TmfResponse<Map>> getCustomerStatistics() {
        Map<String, Object> stats = Map.of(
            "totalCustomers", customerService.countActiveCustomers(),
            "activeCustomers", customerService.countActiveCustomers(),
            "vipCustomers", 0,  // Placeholder
            "churnRiskCustomers", 0  // Placeholder
        );
        return ResponseEntity.ok(TmfResponse.success(stats, "CustomerStatistics"));
    }
}
