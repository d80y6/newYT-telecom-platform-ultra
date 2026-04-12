package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Agreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, UUID> {
    Page<Agreement> findByStatus(Agreement.AgreementStatus status, Pageable pageable);
    Page<Agreement> findByCustomerId(UUID customerId, Pageable pageable);
}