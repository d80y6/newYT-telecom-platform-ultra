package com.yemenptc.bss.coreservice.repository.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.*;

/**
 * Neo4j node for service topology.
 */
@Node("Service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceNode {

    @Id
    private String id;

    @Property("serviceType")
    private String serviceType;

    @Property("status")
    private String status;

    @Property("customerId")
    private String customerId;

    @Property("characteristics")
    private Map<String, Object> characteristics;
}
