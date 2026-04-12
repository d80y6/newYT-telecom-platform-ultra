package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.NetworkResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NetworkResourceRepository extends JpaRepository<NetworkResource, UUID> {
    List<NetworkResource> findByResourceType(String resourceType);
    List<NetworkResource> findByResourceStatus(NetworkResource.ResourceStatus status);
    Optional<NetworkResource> findByResourceIdentifier(String resourceIdentifier);
    List<NetworkResource> findByParentResourceId(UUID parentResourceId);
}
