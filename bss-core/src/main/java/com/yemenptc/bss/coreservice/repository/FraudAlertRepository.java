package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.FraudAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, UUID> {

    Page<FraudAlert> findByStatus(FraudAlert.AlertStatus status, Pageable pageable);

    Page<FraudAlert> findBySeverity(FraudAlert.FraudSeverity severity, Pageable pageable);

    Page<FraudAlert> findByAccountId(String accountId, Pageable pageable);

    Page<FraudAlert> findByAlertType(FraudAlert.FraudAlertType alertType, Pageable pageable);
}