package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ServiceCatalog;
import com.yemenptc.bss.coreservice.entity.ServiceSpecification;
import com.yemenptc.bss.coreservice.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/serviceCatalogManagement/v5")
@RequiredArgsConstructor
@Tag(name = "Service Catalog Management", description = "TMF633 Service Catalog Management API")
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @PostMapping("/serviceCatalog")
    @Operation(summary = "Create service catalog", description = "Creates a new service catalog")
    public ResponseEntity<ServiceCatalog> createCatalog(@RequestBody ServiceCatalog request) {
        ServiceCatalog catalog = serviceCatalogService.createCatalog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(catalog);
    }

    @GetMapping("/serviceCatalog")
    @Operation(summary = "List service catalogs", description = "Retrieves all service catalogs with pagination")
    public ResponseEntity<Page<ServiceCatalog>> listCatalogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<ServiceCatalog> catalogs = serviceCatalogService.listCatalogs(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(catalogs);
    }

    @GetMapping("/serviceCatalog/{id}")
    @Operation(summary = "Get service catalog", description = "Retrieves a service catalog by ID")
    public ResponseEntity<ServiceCatalog> getCatalog(@PathVariable UUID id) {
        ServiceCatalog catalog = serviceCatalogService.getCatalog(id);
        return ResponseEntity.ok(catalog);
    }

    @PutMapping("/serviceCatalog/{id}")
    @Operation(summary = "Update service catalog", description = "Updates an existing service catalog")
    public ResponseEntity<ServiceCatalog> updateCatalog(
            @PathVariable UUID id, 
            @RequestBody ServiceCatalog request) {
        ServiceCatalog catalog = serviceCatalogService.updateCatalog(id, request);
        return ResponseEntity.ok(catalog);
    }

    @DeleteMapping("/serviceCatalog/{id}")
    @Operation(summary = "Delete service catalog", description = "Deletes a service catalog")
    public ResponseEntity<Void> deleteCatalog(@PathVariable UUID id) {
        serviceCatalogService.deleteCatalog(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/serviceCatalog/{id}/activate")
    @Operation(summary = "Activate service catalog", description = "Activates a service catalog")
    public ResponseEntity<ServiceCatalog> activateCatalog(@PathVariable UUID id) {
        ServiceCatalog catalog = serviceCatalogService.activateCatalog(id);
        return ResponseEntity.ok(catalog);
    }

    @PatchMapping("/serviceCatalog/{id}/deprecate")
    @Operation(summary = "Deprecate service catalog", description = "Deprecates a service catalog")
    public ResponseEntity<ServiceCatalog> deprecateCatalog(@PathVariable UUID id) {
        ServiceCatalog catalog = serviceCatalogService.deprecateCatalog(id);
        return ResponseEntity.ok(catalog);
    }

    @PostMapping("/serviceSpecification")
    @Operation(summary = "Create service specification", description = "Creates a new service specification")
    public ResponseEntity<ServiceSpecification> createSpecification(@RequestBody ServiceSpecification request) {
        ServiceSpecification specification = serviceCatalogService.createSpecification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(specification);
    }

    @GetMapping("/serviceSpecification")
    @Operation(summary = "List service specifications", description = "Retrieves all service specifications")
    public ResponseEntity<Page<ServiceSpecification>> listSpecifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ServiceSpecification> specifications = serviceCatalogService.listSpecifications(PageRequest.of(page, size));
        return ResponseEntity.ok(specifications);
    }

    @GetMapping("/serviceSpecification/serviceType/{serviceType}")
    @Operation(summary = "Get specifications by service type", description = "Retrieves service specifications by service type")
    public ResponseEntity<List<ServiceSpecification>> getSpecificationsByType(@PathVariable String serviceType) {
        List<ServiceSpecification> specifications = serviceCatalogService.getSpecificationsByType(serviceType);
        return ResponseEntity.ok(specifications);
    }

    @PatchMapping("/serviceSpecification/{id}/activate")
    @Operation(summary = "Activate service specification", description = "Activates a service specification")
    public ResponseEntity<ServiceSpecification> activateSpecification(@PathVariable UUID id) {
        ServiceSpecification specification = serviceCatalogService.activateSpecification(id);
        return ResponseEntity.ok(specification);
    }
}
