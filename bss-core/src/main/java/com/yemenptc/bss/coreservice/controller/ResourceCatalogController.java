package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ResourceCatalog;
import com.yemenptc.bss.coreservice.entity.ResourceSpecification;
import com.yemenptc.bss.coreservice.service.ResourceCatalogService;
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
@RequestMapping("/tmf-api/resourceCatalogManagement/v5")
@RequiredArgsConstructor
@Tag(name = "Resource Catalog Management", description = "TMF634 Resource Catalog Management API")
public class ResourceCatalogController {

    private final ResourceCatalogService resourceCatalogService;

    @PostMapping("/resourceCatalog")
    @Operation(summary = "Create resource catalog", description = "Creates a new resource catalog")
    public ResponseEntity<ResourceCatalog> createCatalog(@RequestBody ResourceCatalog request) {
        ResourceCatalog catalog = resourceCatalogService.createCatalog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(catalog);
    }

    @GetMapping("/resourceCatalog")
    @Operation(summary = "List resource catalogs", description = "Retrieves all resource catalogs with pagination")
    public ResponseEntity<Page<ResourceCatalog>> listCatalogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<ResourceCatalog> catalogs = resourceCatalogService.listCatalogs(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(catalogs);
    }

    @GetMapping("/resourceCatalog/{id}")
    @Operation(summary = "Get resource catalog", description = "Retrieves a resource catalog by ID")
    public ResponseEntity<ResourceCatalog> getCatalog(@PathVariable UUID id) {
        ResourceCatalog catalog = resourceCatalogService.getCatalog(id);
        return ResponseEntity.ok(catalog);
    }

    @PutMapping("/resourceCatalog/{id}")
    @Operation(summary = "Update resource catalog", description = "Updates an existing resource catalog")
    public ResponseEntity<ResourceCatalog> updateCatalog(
            @PathVariable UUID id, 
            @RequestBody ResourceCatalog request) {
        ResourceCatalog catalog = resourceCatalogService.updateCatalog(id, request);
        return ResponseEntity.ok(catalog);
    }

    @DeleteMapping("/resourceCatalog/{id}")
    @Operation(summary = "Delete resource catalog", description = "Deletes a resource catalog")
    public ResponseEntity<Void> deleteCatalog(@PathVariable UUID id) {
        resourceCatalogService.deleteCatalog(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/resourceCatalog/{id}/activate")
    @Operation(summary = "Activate resource catalog", description = "Activates a resource catalog")
    public ResponseEntity<ResourceCatalog> activateCatalog(@PathVariable UUID id) {
        ResourceCatalog catalog = resourceCatalogService.activateCatalog(id);
        return ResponseEntity.ok(catalog);
    }

    @PatchMapping("/resourceCatalog/{id}/deprecate")
    @Operation(summary = "Deprecate resource catalog", description = "Deprecates a resource catalog")
    public ResponseEntity<ResourceCatalog> deprecateCatalog(@PathVariable UUID id) {
        ResourceCatalog catalog = resourceCatalogService.deprecateCatalog(id);
        return ResponseEntity.ok(catalog);
    }

    @PostMapping("/resourceSpecification")
    @Operation(summary = "Create resource specification", description = "Creates a new resource specification")
    public ResponseEntity<ResourceSpecification> createSpecification(@RequestBody ResourceSpecification request) {
        ResourceSpecification specification = resourceCatalogService.createSpecification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(specification);
    }

    @GetMapping("/resourceSpecification")
    @Operation(summary = "List resource specifications", description = "Retrieves all resource specifications")
    public ResponseEntity<Page<ResourceSpecification>> listSpecifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ResourceSpecification> specifications = resourceCatalogService.listSpecifications(PageRequest.of(page, size));
        return ResponseEntity.ok(specifications);
    }

    @GetMapping("/resourceSpecification/resourceType/{resourceType}")
    @Operation(summary = "Get specifications by resource type", description = "Retrieves resource specifications by resource type")
    public ResponseEntity<List<ResourceSpecification>> getSpecificationsByType(@PathVariable String resourceType) {
        List<ResourceSpecification> specifications = resourceCatalogService.getSpecificationsByType(resourceType);
        return ResponseEntity.ok(specifications);
    }

    @PatchMapping("/resourceSpecification/{id}/activate")
    @Operation(summary = "Activate resource specification", description = "Activates a resource specification")
    public ResponseEntity<ResourceSpecification> activateSpecification(@PathVariable UUID id) {
        ResourceSpecification specification = resourceCatalogService.activateSpecification(id);
        return ResponseEntity.ok(specification);
    }
}
