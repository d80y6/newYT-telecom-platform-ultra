package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.SlaViolation;
import com.yemenptc.bss.coreservice.entity.SlaViolation.ViolationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SlaViolationRepository extends JpaRepository<SlaViolation, UUID> {
    
    List<SlaViolation> findBySlaContractId(String slaContractId);
    
    List<SlaViolation> findByStatus(ViolationStatus status);
    
    @Query("SELECT v FROM SlaViolation v WHERE v.violationDate BETWEEN :start AND :end")
    List<SlaViolation> findByViolationDateBetween(
        @Param("start") Instant start,
        @Param("end") Instant end
    );
    
    @Query("SELECT v FROM SlaViolation v WHERE v.status = 'OPEN' AND v.severity = :severity")
    List<SlaViolation> findOpenViolationsBySeverity(@Param("severity") String severity);
    
    @Query("SELECT v FROM SlaViolation v WHERE v.slaContractId = :contractId AND v.status = 'OPEN'")
    List<SlaViolation> findOpenViolationsByContract(@Param("contractId") String contractId);
    
    @Query("SELECT COUNT(v) FROM SlaViolation v WHERE v.slaContractId = :contractId AND v.status = 'OPEN'")
    int countOpenViolationsByContract(@Param("contractId") String contractId);
}
