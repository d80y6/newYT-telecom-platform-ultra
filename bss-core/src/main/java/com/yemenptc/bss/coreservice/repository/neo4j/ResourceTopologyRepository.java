package com.yemenptc.bss.coreservice.repository.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Neo4j repository for resource topology queries.
 */
@Repository
public interface ResourceTopologyRepository extends Neo4jRepository<ResourceNode, String> {

    @Query("MATCH (r:Resource {resourceType: $type}) RETURN r")
    List<ResourceNode> findByResourceType(String type);

    @Query("MATCH (r:Resource {status: $status}) RETURN r")
    List<ResourceNode> findByStatus(String status);

    @Query("MATCH path = (start:Resource {id: $startId})-[*1..5]->(end:Resource {id: $endId}) RETURN path")
    List<ResourceNode> findPathBetween(String startId, String endId);

    @Query("MATCH (r:Resource {id: $resourceId})-[:SERVES]->(s:Service) RETURN s")
    List<ServiceNode> findAffectedServices(String resourceId);

    @Query("MATCH (olt:Resource {resourceType: 'OLT'})-[:HAS_PORT]->(port:Resource)-[:CONNECTS_TO]->(splitter:Resource)-[:SERVES]->(s:Service {customerId: $customerId}) RETURN olt, port, splitter, s")
    List<ResourceNode> findFiberRouteForCustomer(String customerId);
}
