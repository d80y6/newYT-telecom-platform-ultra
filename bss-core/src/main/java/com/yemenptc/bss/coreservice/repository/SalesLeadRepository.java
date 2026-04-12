package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.SalesLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalesLeadRepository extends JpaRepository<SalesLead, UUID> {
    
    List<SalesLead> findByStatus(SalesLead.LeadStatus status);
    
    List<SalesLead> findByAssignedTo(UUID assignedTo);
    
    List<SalesLead> findBySource(String source);
}
