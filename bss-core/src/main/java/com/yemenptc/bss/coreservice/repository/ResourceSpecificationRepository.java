package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ResourceSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceSpecificationRepository extends JpaRepository<ResourceSpecification, UUID> {
    
    List<ResourceSpecification> findByResourceType(String resourceType);
    
    List<ResourceSpecification> findByState(ResourceSpecification.SpecState state);
    
    List<ResourceSpecification> findByCategory(String category);
}
