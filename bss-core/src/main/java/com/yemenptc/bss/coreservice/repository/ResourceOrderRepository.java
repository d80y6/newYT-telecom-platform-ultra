package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ResourceOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceOrderRepository extends JpaRepository<ResourceOrder, UUID> {

    Optional<ResourceOrder> findByExternalId(String externalId);

    Page<ResourceOrder> findByState(ResourceOrder.ResourceOrderState state, Pageable pageable);

    Page<ResourceOrder> findByOrderType(ResourceOrder.ResourceOrderType orderType, Pageable pageable);

    Page<ResourceOrder> findByResourceType(ResourceOrder.ResourceType resourceType, Pageable pageable);

    Page<ResourceOrder> findByCustomerId(String customerId, Pageable pageable);

    long countByState(ResourceOrder.ResourceOrderState state);
}