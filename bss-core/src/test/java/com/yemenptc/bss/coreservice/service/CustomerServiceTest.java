package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Customer;
import com.yemenptc.bss.coreservice.repository.CustomerRepository;
import com.yemenptc.bss.coreservice.exception.CustomerAlreadyExistsException;
import com.yemenptc.bss.coreservice.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Ahmed");
        customer.setLastName("Ali");
        customer.setNationalId("1234567890");
        customer.setPrimaryPhone("777123456");
        customer.setCity("Sanaa");
        customer.setGovernorate("Sanaa");
        customer.setCustomerType(Customer.CustomerType.RESIDENTIAL);
        return customer;
    }

    @Test
    void createCustomer_Success() {
        Customer request = createTestCustomer();

        when(customerRepository.existsByNationalId("1234567890")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> {
            Customer c = i.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        Customer result = customerService.createCustomer(request);

        assertNotNull(result);
        assertEquals("Ahmed", result.getFirstName());
        assertEquals(Customer.CustomerStatus.ACTIVE, result.getStatus());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_AlreadyExists() {
        Customer request = createTestCustomer();
        when(customerRepository.existsByNationalId("1234567890")).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class, () -> customerService.createCustomer(request));
    }

    @Test
    void getCustomer_Success() {
        UUID id = UUID.randomUUID();
        Customer customer = createTestCustomer();
        customer.setId(id);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomer(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getCustomer_NotFound() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomer(id));
    }

    @Test
    void listCustomers_Success() {
        Page<Customer> page = new PageImpl<>(List.of());
        when(customerRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Customer> result = customerService.listCustomers(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void updateKyc_Success() {
        UUID id = UUID.randomUUID();
        Customer existing = createTestCustomer();
        existing.setId(id);
        existing.setKycLevel(Customer.KycLevel.BASIC);
        existing.setKycVerified(false);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer result = customerService.updateKyc(id, Customer.KycLevel.FULL);

        assertNotNull(result);
        assertEquals(Customer.KycLevel.FULL, result.getKycLevel());
        assertTrue(result.getKycVerified());
    }
}
