package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByNationalId(String nationalId);

    Optional<Customer> findByPrimaryPhone(String phone);

    Optional<Customer> findByExternalId(String externalId);

    boolean existsByNationalId(String nationalId);

    Page<Customer> findByStatus(Customer.CustomerStatus status, Pageable pageable);

    Page<Customer> findByCustomerType(Customer.CustomerType customerType, Pageable pageable);

    Page<Customer> findByGovernorate(String governorate, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "c.nationalId LIKE CONCAT('%', :query, '%') OR " +
           "c.primaryPhone LIKE CONCAT('%', :query, '%')")
    Page<Customer> searchCustomers(@Param("query") String query, Pageable pageable);

    long countByStatus(Customer.CustomerStatus status);
}
