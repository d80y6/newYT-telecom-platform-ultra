package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_catalogs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String resourceCatalogId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    private CatalogState state;

    @Column(nullable = false)
    private Instant validFrom;

    private Instant validTo;

    private Instant createdAt;

    private Instant updatedAt;

    @Column(nullable = false)
    private String category;

    private String resourceType;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (resourceCatalogId == null) resourceCatalogId = "RC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (validFrom == null) validFrom = Instant.now();
    }

    public enum CatalogState {
        ACTIVE,
        INACTIVE,
        DRAFT,
        DEPRECATED
    }
}
