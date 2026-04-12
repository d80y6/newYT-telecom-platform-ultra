package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, UUID> {
    List<ServiceOrder> findByPartyId(UUID partyId);
    List<ServiceOrder> findByProductOrderId(UUID productOrderId);
    List<ServiceOrder> findByStatus(ServiceOrder.ServiceOrderStatus status);
    List<ServiceOrder> findByOrderType(ServiceOrder.ServiceOrderType orderType);
}
