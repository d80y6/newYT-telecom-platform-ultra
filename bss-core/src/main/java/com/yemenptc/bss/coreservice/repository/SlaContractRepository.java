package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.SlaContract;
import com.yemenptc.bss.coreservice.entity.SlaContract.SlaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SlaContractRepository extends JpaRepository<SlaContract, UUID> {
    
    List<SlaContract> findByCustomerId(String customerId);
    
    List<SlaContract> findByServiceId(String serviceId);
    
    List<SlaContract> findByStatus(SlaStatus status);
    
    @Query("SELECT s FROM SlaContract s WHERE s.status = :status AND s.endDate BETWEEN :start AND :end")
    List<SlaContract> findByStatusAndExpiringBetween(
        @Param("status") SlaStatus status,
        @Param("start") Instant start,
        @Param("end") Instant end
    );
    
    @Query("SELECT s FROM SlaContract s WHERE s.customerId = :customerId AND s.status = :status")
    List<SlaContract> findByCustomerIdAndStatus(
        @Param("customerId") String customerId,
        @Param("status") SlaStatus status
    );
}
