package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.RelationshipNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RelationshipNodeRepository extends JpaRepository<RelationshipNode, UUID> {

    List<RelationshipNode> findByCustomer360Id(UUID customer360Id);
}
