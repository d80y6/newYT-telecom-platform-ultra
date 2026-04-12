package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Customer;
import com.yemenptc.bss.coreservice.exception.CustomerAlreadyExistsException;
import com.yemenptc.bss.coreservice.exception.CustomerNotFoundException;
import com.yemenptc.bss.coreservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Customer Service - TMF632 Party Management API
 * Manages customer lifecycle and party information
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(Customer request) {
        log.info("Creating customer: {} {}", request.getFirstName(), request.getLastName());
        
        if (request.getNationalId() != null && customerRepository.existsByNationalId(request.getNationalId())) {
            throw new CustomerAlreadyExistsException("Customer with national ID " + request.getNationalId() + " already exists");
        }
        
        Customer customer = Customer.builder()
            .externalId("CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerType(request.getCustomerType())
            .status(Customer.CustomerStatus.ACTIVE)
            .nationalId(request.getNationalId())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .primaryPhone(request.getPrimaryPhone())
            .email(request.getEmail())
            .city(request.getCity())
            .governorate(request.getGovernorate())
            .street(request.getStreet())
            .postalCode(request.getPostalCode())
            .country("YE")
            .kycLevel(Customer.KycLevel.BASIC)
            .kycVerified(false)
            .build();
        
        Customer saved = customerRepository.save(customer);
        log.info("Customer created: {} with ID: {}", saved.getExternalId(), saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(UUID id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + id));
    }

    @Transactional(readOnly = true)
    public Customer getCustomerByNationalId(String nationalId) {
        return customerRepository.findByNationalId(nationalId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with national ID: " + nationalId));
    }

    @Transactional(readOnly = true)
    public Customer getCustomerByPhone(String phone) {
        return customerRepository.findByPrimaryPhone(phone)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with phone: " + phone));
    }

    @Transactional(readOnly = true)
    public Page<Customer> listCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Customer> searchCustomers(String query, Pageable pageable) {
        return customerRepository.searchCustomers(query, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Customer> getCustomersByStatus(Customer.CustomerStatus status, Pageable pageable) {
        return customerRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Customer> getCustomersByType(Customer.CustomerType type, Pageable pageable) {
        return customerRepository.findByCustomerType(type, pageable);
    }

    @Transactional
    public Customer updateCustomer(UUID id, Customer request) {
        log.info("Updating customer: {}", id);
        
        Customer existing = getCustomer(id);
        
        Customer updated = Customer.builder()
            .externalId(existing.getExternalId())
            .customerType(request.getCustomerType() != null ? request.getCustomerType() : existing.getCustomerType())
            .status(existing.getStatus())
            .nationalId(request.getNationalId() != null ? request.getNationalId() : existing.getNationalId())
            .firstName(request.getFirstName() != null ? request.getFirstName() : existing.getFirstName())
            .lastName(request.getLastName() != null ? request.getLastName() : existing.getLastName())
            .primaryPhone(request.getPrimaryPhone() != null ? request.getPrimaryPhone() : existing.getPrimaryPhone())
            .email(request.getEmail() != null ? request.getEmail() : existing.getEmail())
            .city(request.getCity() != null ? request.getCity() : existing.getCity())
            .governorate(request.getGovernorate() != null ? request.getGovernorate() : existing.getGovernorate())
            .street(request.getStreet() != null ? request.getStreet() : existing.getStreet())
            .postalCode(request.getPostalCode() != null ? request.getPostalCode() : existing.getPostalCode())
            .country(existing.getCountry())
            .kycLevel(request.getKycLevel() != null ? request.getKycLevel() : existing.getKycLevel())
            .kycVerified(request.getKycVerified() != null ? request.getKycVerified() : existing.getKycVerified())
            .churnRiskScore(request.getChurnRiskScore() != null ? request.getChurnRiskScore() : existing.getChurnRiskScore())
            .lifetimeValue(request.getLifetimeValue() != null ? request.getLifetimeValue() : existing.getLifetimeValue())
            .build();
        
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(Instant.now());
        updated.setVersion(existing.getVersion() + 1);
        
        return customerRepository.save(updated);
    }

    @Transactional
    public Customer updateKyc(UUID id, Customer.KycLevel level) {
        log.info("Updating KYC for customer: {} to level: {}", id, level);
        
        Customer customer = getCustomer(id);
        customer.setKycLevel(level);
        customer.setKycVerified(true);
        customer.setUpdatedAt(Instant.now());
        
        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        log.info("Deleting customer: {}", id);
        Customer customer = getCustomer(id);
        customerRepository.delete(customer);
    }

    @Transactional(readOnly = true)
    public long countActiveCustomers() {
        return customerRepository.countByStatus(Customer.CustomerStatus.ACTIVE);
    }
}
