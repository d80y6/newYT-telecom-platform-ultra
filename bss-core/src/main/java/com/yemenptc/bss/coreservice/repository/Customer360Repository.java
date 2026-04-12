package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Customer360;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface Customer360Repository extends JpaRepository<Customer360, UUID> {

    Optional<Customer360> findByCustomerId(UUID customerId);

    @Query("SELECT c FROM Customer360 c WHERE " +
           "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Customer360> searchCustomers(@Param("query") String query, Pageable pageable);

    Page<Customer360> findByPriorityLevel(String priorityLevel, Pageable pageable);

    boolean existsByCustomerId(UUID customerId);
}