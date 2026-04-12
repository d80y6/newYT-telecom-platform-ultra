package com.yemenptc.bss.coreservice.repository.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.*;

/**
 * Neo4j node for network resource topology.
 * Models OLT→PON Port→Splitter→ONT→Service relationships.
 */
@Node("Resource")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceNode {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("resourceType")
    private String resourceType;

    @Property("status")
    private String status;

    @Property("location")
    private String location;

    @Property("characteristics")
    private Map<String, Object> characteristics;

    @Relationship(type = "HAS_PORT", direction = Relationship.Direction.OUTGOING)
    private List<ResourceNode> ports;

    @Relationship(type = "CONNECTS_TO", direction = Relationship.Direction.OUTGOING)
    private List<ResourceNode> connections;

    @Relationship(type = "SERVES", direction = Relationship.Direction.OUTGOING)
    private List<ServiceNode> services;
}
