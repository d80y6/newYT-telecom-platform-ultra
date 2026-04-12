package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.NetworkProvisioningOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NetworkProvisioningOrderRepository extends JpaRepository<NetworkProvisioningOrder, UUID> {

    Page<NetworkProvisioningOrder> findByStatus(NetworkProvisioningOrder.ProvisionOrderStatus status, Pageable pageable);

    Page<NetworkProvisioningOrder> findByCustomerId(String customerId, Pageable pageable);

    Page<NetworkProvisioningOrder> findByServiceType(String serviceType, Pageable pageable);
}