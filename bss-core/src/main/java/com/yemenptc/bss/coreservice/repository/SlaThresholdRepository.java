package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.SlaThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SlaThresholdRepository extends JpaRepository<SlaThreshold, UUID> {
    
    List<SlaThreshold> findByMetricName(String metricName);
    
    List<SlaThreshold> findBySlaName(String slaName);
    
    List<SlaThreshold> findBySeverity(String severity);
}
