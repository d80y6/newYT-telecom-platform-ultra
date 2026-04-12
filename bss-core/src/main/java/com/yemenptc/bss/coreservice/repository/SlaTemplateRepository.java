package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.SlaTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaTemplateRepository extends JpaRepository<SlaTemplate, UUID> {
    
    Page<SlaTemplate> findByStatus(SlaTemplate.SlaStatus status, Pageable pageable);
    
    Page<SlaTemplate> findByTemplateType(String templateType, Pageable pageable);
    
    List<SlaTemplate> findByIsActiveTrue();
    
    List<SlaTemplate> findByCategory(String category);
}