package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ResourceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceCatalogRepository extends JpaRepository<ResourceCatalog, UUID> {

    Optional<ResourceCatalog> findByResourceCatalogId(String resourceCatalogId);

    List<ResourceCatalog> findByState(ResourceCatalog.CatalogState state);

    List<ResourceCatalog> findByCategory(String category);

    List<ResourceCatalog> findByResourceType(String resourceType);

    boolean existsByResourceCatalogId(String resourceCatalogId);
}
