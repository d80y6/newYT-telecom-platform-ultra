package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ResourceCatalog;
import com.yemenptc.bss.coreservice.entity.ResourceSpecification;
import com.yemenptc.bss.coreservice.repository.ResourceCatalogRepository;
import com.yemenptc.bss.coreservice.repository.ResourceSpecificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceCatalogService {

    private final ResourceCatalogRepository resourceCatalogRepository;
    private final ResourceSpecificationRepository resourceSpecificationRepository;

    @Transactional
    public ResourceCatalog createCatalog(ResourceCatalog request) {
        log.info("Creating resource catalog: {}", request.getName());
        
        ResourceCatalog catalog = ResourceCatalog.builder()
            .resourceCatalogId(request.getResourceCatalogId() != null ? 
                request.getResourceCatalogId() : "RC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .name(request.getName())
            .description(request.getDescription())
            .version(request.getVersion() != null ? request.getVersion() : "1.0")
            .state(ResourceCatalog.CatalogState.DRAFT)
            .validFrom(Instant.now())
            .category(request.getCategory())
            .resourceType(request.getResourceType())
            .build();

        return resourceCatalogRepository.save(catalog);
    }

    @Transactional(readOnly = true)
    public ResourceCatalog getCatalog(UUID id) {
        return resourceCatalogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resource catalog not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ResourceCatalog> listCatalogs(Pageable pageable) {
        return resourceCatalogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<ResourceCatalog> getCatalogsByState(ResourceCatalog.CatalogState state) {
        return resourceCatalogRepository.findByState(state);
    }

    @Transactional(readOnly = true)
    public List<ResourceCatalog> getCatalogsByType(String resourceType) {
        return resourceCatalogRepository.findByResourceType(resourceType);
    }

    @Transactional
    public ResourceCatalog activateCatalog(UUID id) {
        ResourceCatalog catalog = getCatalog(id);
        catalog.setState(ResourceCatalog.CatalogState.ACTIVE);
        catalog.setUpdatedAt(Instant.now());
        log.info("Activated resource catalog: {}", catalog.getResourceCatalogId());
        return resourceCatalogRepository.save(catalog);
    }

    @Transactional
    public ResourceCatalog deprecateCatalog(UUID id) {
        ResourceCatalog catalog = getCatalog(id);
        catalog.setState(ResourceCatalog.CatalogState.DEPRECATED);
        catalog.setUpdatedAt(Instant.now());
        log.info("Deprecated resource catalog: {}", catalog.getResourceCatalogId());
        return resourceCatalogRepository.save(catalog);
    }

    @Transactional
    public void deleteCatalog(UUID id) {
        ResourceCatalog catalog = getCatalog(id);
        resourceCatalogRepository.delete(catalog);
        log.info("Deleted resource catalog: {}", catalog.getResourceCatalogId());
    }

    @Transactional
    public ResourceSpecification createSpecification(ResourceSpecification request) {
        log.info("Creating resource specification: {}", request.getName());
        
        ResourceSpecification spec = ResourceSpecification.builder()
            .resourceSpecificationId(request.getResourceSpecificationId() != null ? 
                request.getResourceSpecificationId() : "RS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .name(request.getName())
            .description(request.getDescription())
            .version(request.getVersion() != null ? request.getVersion() : "1.0")
            .state(ResourceSpecification.SpecState.DRAFT)
            .resourceType(request.getResourceType())
            .category(request.getCategory())
            .validFrom(Instant.now())
            .build();

        return resourceSpecificationRepository.save(spec);
    }

    @Transactional(readOnly = true)
    public Page<ResourceSpecification> listSpecifications(Pageable pageable) {
        return resourceSpecificationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<ResourceSpecification> getSpecificationsByType(String resourceType) {
        return resourceSpecificationRepository.findByResourceType(resourceType);
    }

    @Transactional
    public ResourceSpecification activateSpecification(UUID id) {
        ResourceSpecification spec = resourceSpecificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resource specification not found: " + id));
        spec.setState(ResourceSpecification.SpecState.ACTIVE);
        spec.setUpdatedAt(Instant.now());
        log.info("Activated resource specification: {}", spec.getResourceSpecificationId());
        return resourceSpecificationRepository.save(spec);
    }

    @Transactional
    public ResourceCatalog updateCatalog(UUID id, ResourceCatalog request) {
        ResourceCatalog catalog = getCatalog(id);
        if (request.getName() != null) catalog.setName(request.getName());
        if (request.getDescription() != null) catalog.setDescription(request.getDescription());
        if (request.getVersion() != null) catalog.setVersion(request.getVersion());
        catalog.setUpdatedAt(Instant.now());
        return resourceCatalogRepository.save(catalog);
    }
}
