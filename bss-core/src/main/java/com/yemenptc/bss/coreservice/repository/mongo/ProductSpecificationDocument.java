package com.yemenptc.bss.coreservice.repository.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

/**
 * MongoDB document for product specifications with flexible schema.
 * Stores complex characteristic arrays for PSTN, FTTH, 4G, MPLS, Hosting.
 */
@Document(collection = "product_specifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSpecificationDocument {

    @Id
    private String id;

    private String name;
    private String description;
    private String brand;
    private String version;
    private String lifecycleStatus;
    private String serviceType;

    private List<Map<String, Object>> productSpecCharacteristic;
    private List<String> productOfferingIds;
    private Map<String, Object> bundlingRules;
    private Map<String, Object> constraints;

    private Instant createdAt;
    private Instant updatedAt;
}
