package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ServiceCatalog;
import com.yemenptc.bss.coreservice.entity.ServiceSpecification;
import com.yemenptc.bss.coreservice.repository.ServiceCatalogRepository;
import com.yemenptc.bss.coreservice.repository.ServiceSpecificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;
    private final ServiceSpecificationRepository serviceSpecificationRepository;

    @Transactional
    public ServiceCatalog createCatalog(ServiceCatalog request) {
        log.info("Creating service catalog: {}", request.getCatalogName());
        
        if (request.getCatalogId() != null && serviceCatalogRepository.existsByCatalogId(request.getCatalogId())) {
            throw new RuntimeException("Catalog ID already exists: " + request.getCatalogId());
        }

        ServiceCatalog catalog = ServiceCatalog.builder()
            .catalogId(request.getCatalogId() != null ? request.getCatalogId() : "SC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .catalogName(request.getCatalogName())
            .catalogDescription(request.getCatalogDescription())
            .catalogVersion(request.getCatalogVersion() != null ? request.getCatalogVersion() : "1.0")
            .status(ServiceCatalog.CatalogStatus.DRAFT)
            .validFrom(Instant.now())
            .category(request.getCategory())
            .build();

        return serviceCatalogRepository.save(catalog);
    }

    @Transactional(readOnly = true)
    public ServiceCatalog getCatalog(UUID id) {
        return serviceCatalogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service catalog not found: " + id));
    }

    @Transactional(readOnly = true)
    public ServiceCatalog getCatalogById(String catalogId) {
        return serviceCatalogRepository.findByCatalogId(catalogId)
            .orElseThrow(() -> new RuntimeException("Service catalog not found: " + catalogId));
    }

    @Transactional(readOnly = true)
    public Page<ServiceCatalog> listCatalogs(Pageable pageable) {
        return serviceCatalogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<ServiceCatalog> getCatalogsByState(ServiceCatalog.CatalogStatus state) {
        return serviceCatalogRepository.findByStatus(state);
    }

    @Transactional
    public ServiceCatalog updateCatalog(UUID id, ServiceCatalog request) {
        ServiceCatalog catalog = getCatalog(id);
        
        if (request.getCatalogName() != null) catalog.setCatalogName(request.getCatalogName());
        if (request.getCatalogDescription() != null) catalog.setCatalogDescription(request.getCatalogDescription());
        if (request.getCatalogVersion() != null) catalog.setCatalogVersion(request.getCatalogVersion());
        if (request.getCategory() != null) catalog.setCategory(request.getCategory());
        
        catalog.setUpdatedAt(Instant.now());
        return serviceCatalogRepository.save(catalog);
    }

    @Transactional
    public ServiceCatalog activateCatalog(UUID id) {
        ServiceCatalog catalog = getCatalog(id);
        catalog.setStatus(ServiceCatalog.CatalogStatus.ACTIVE);
        catalog.setUpdatedAt(Instant.now());
        log.info("Activated service catalog: {}", catalog.getCatalogId());
        return serviceCatalogRepository.save(catalog);
    }

    @Transactional
    public ServiceCatalog deprecateCatalog(UUID id) {
        ServiceCatalog catalog = getCatalog(id);
        catalog.setStatus(ServiceCatalog.CatalogStatus.RETIRED);
        catalog.setUpdatedAt(Instant.now());
        log.info("Deprecated service catalog: {}", catalog.getCatalogId());
        return serviceCatalogRepository.save(catalog);
    }

    @Transactional
    public void deleteCatalog(UUID id) {
        ServiceCatalog catalog = getCatalog(id);
        serviceCatalogRepository.delete(catalog);
        log.info("Deleted service catalog: {}", catalog.getCatalogId());
    }

    @Transactional
    public ServiceSpecification createSpecification(ServiceSpecification request) {
        log.info("Creating service specification: {}", request.getName());
        
        if (serviceSpecificationRepository.existsBySpecificationId(request.getSpecificationId())) {
            throw new RuntimeException("Specification ID already exists: " + request.getSpecificationId());
        }

        ServiceSpecification specification = ServiceSpecification.builder()
            .specificationId(request.getSpecificationId())
            .name(request.getName())
            .description(request.getDescription())
            .version(request.getVersion() != null ? request.getVersion() : "1.0")
            .state(ServiceSpecification.SpecState.DRAFT)
            .serviceType(request.getServiceType())
            .hasProvider(request.isHasProvider())
            .targetServiceSchema(request.getTargetServiceSchema())
            .validFrom(Instant.now())
            .build();

        return serviceSpecificationRepository.save(specification);
    }

    @Transactional(readOnly = true)
    public Page<ServiceSpecification> listSpecifications(Pageable pageable) {
        return serviceSpecificationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<ServiceSpecification> getSpecificationsByType(String serviceType) {
        return serviceSpecificationRepository.findByServiceType(serviceType);
    }

    @Transactional(readOnly = true)
    public Optional<ServiceSpecification> getSpecificationById(String specificationId) {
        return serviceSpecificationRepository.findBySpecificationId(specificationId);
    }

    @Transactional
    public ServiceSpecification activateSpecification(UUID id) {
        ServiceSpecification specification = serviceSpecificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Specification not found: " + id));
        specification.setState(ServiceSpecification.SpecState.ACTIVE);
        specification.setUpdatedAt(Instant.now());
        return serviceSpecificationRepository.save(specification);
    }
}
