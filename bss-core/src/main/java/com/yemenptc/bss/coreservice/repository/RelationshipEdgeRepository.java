package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.RelationshipEdge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RelationshipEdgeRepository extends JpaRepository<RelationshipEdge, UUID> {

    List<RelationshipEdge> findByCustomer360Id(UUID customer360Id);
}
