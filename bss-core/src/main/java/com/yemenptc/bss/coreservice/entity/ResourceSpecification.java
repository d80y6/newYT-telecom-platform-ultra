package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_specifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String resourceSpecificationId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    private SpecState state;

    @Column(nullable = false)
    private String resourceType;

    private String category;

    private Instant validFrom;

    private Instant validTo;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (resourceSpecificationId == null) resourceSpecificationId = "RS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public enum SpecState {
        ACTIVE,
        INACTIVE,
        DRAFT,
        DEPRECATED
    }
}
