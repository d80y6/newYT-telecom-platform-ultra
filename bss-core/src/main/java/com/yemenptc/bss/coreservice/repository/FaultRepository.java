package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Fault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FaultRepository extends JpaRepository<Fault, UUID> {

    Page<Fault> findByStatus(Fault.FaultStatus status, Pageable pageable);

    Page<Fault> findBySeverity(Fault.AlarmSeverity severity, Pageable pageable);

    Page<Fault> findByResourceId(String resourceId, Pageable pageable);
}