package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.NetworkResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<NetworkResource, UUID> {
    Optional<NetworkResource> findByResourceIdentifier(String resourceIdentifier);
    List<NetworkResource> findByResourceType(String resourceType);
    List<NetworkResource> findByResourceStatus(NetworkResource.ResourceStatus status);
    List<NetworkResource> findByParentResourceId(UUID parentResourceId);
    Page<NetworkResource> findByCategory(NetworkResource.ResourceCategory category, Pageable pageable);
    boolean existsByResourceIdentifier(String resourceIdentifier);
}
