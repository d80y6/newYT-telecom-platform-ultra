package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import com.yemenptc.bss.coreservice.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;

    @Transactional
    public ServiceOrder createServiceOrder(ServiceOrder request) {
        log.info("Creating service order: type={}, party={}", request.getOrderType(), request.getPartyId());

        ServiceOrder order = ServiceOrder.builder()
            .orderNumber("SVO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .partyId(request.getPartyId())
            .productOrderId(request.getProductOrderId())
            .status(ServiceOrder.ServiceOrderStatus.ACKNOWLEDGED)
            .orderType(request.getOrderType())
            .cfsType(request.getCfsType())
            .rfsType(request.getRfsType())
            .build();

        ServiceOrder saved = serviceOrderRepository.save(order);
        log.info("Service order created: {}", saved.getOrderNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public ServiceOrder getServiceOrder(UUID id) {
        return serviceOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service order not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ServiceOrder> listServiceOrders(Pageable pageable) {
        return serviceOrderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ServiceOrder> getServiceOrdersByParty(UUID partyId, Pageable pageable) {
        return serviceOrderRepository.findByPartyId(partyId).stream()
            .collect(java.util.stream.Collectors.collectingAndThen(
                java.util.stream.Collectors.toList(),
                list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
            ));
    }

    @Transactional
    public ServiceOrder updateServiceOrder(UUID id, ServiceOrder request) {
        ServiceOrder existing = getServiceOrder(id);
        
        ServiceOrder updated = ServiceOrder.builder()
            .orderNumber(existing.getOrderNumber())
            .partyId(request.getPartyId() != null ? request.getPartyId() : existing.getPartyId())
            .productOrderId(request.getProductOrderId() != null ? request.getProductOrderId() : existing.getProductOrderId())
            .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
            .orderType(request.getOrderType() != null ? request.getOrderType() : existing.getOrderType())
            .cfsType(request.getCfsType() != null ? request.getCfsType() : existing.getCfsType())
            .rfsType(request.getRfsType() != null ? request.getRfsType() : existing.getRfsType())
            .completedAt(request.getStatus() == ServiceOrder.ServiceOrderStatus.COMPLETED ? LocalDateTime.now() : existing.getCompletedAt())
            .build();
        
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(java.time.Instant.now());
        updated.setVersion(existing.getVersion() + 1);
        
        return serviceOrderRepository.save(updated);
    }

    @Transactional
    public ServiceOrder acknowledgeOrder(UUID id) {
        return updateState(id, ServiceOrder.ServiceOrderStatus.ACKNOWLEDGED);
    }

    @Transactional
    public ServiceOrder startOrder(UUID id) {
        return updateState(id, ServiceOrder.ServiceOrderStatus.IN_PROGRESS);
    }

    @Transactional
    public ServiceOrder completeOrder(UUID id) {
        ServiceOrder order = getServiceOrder(id);
        order.setStatus(ServiceOrder.ServiceOrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        order.setUpdatedAt(java.time.Instant.now());
        return serviceOrderRepository.save(order);
    }

    @Transactional
    public ServiceOrder cancelOrder(UUID id, String reason) {
        log.info("Cancelling service order {}: {}", id, reason);
        ServiceOrder order = getServiceOrder(id);
        order.setStatus(ServiceOrder.ServiceOrderStatus.CANCELLED);
        order.setUpdatedAt(java.time.Instant.now());
        return serviceOrderRepository.save(order);
    }

    private ServiceOrder updateState(UUID id, ServiceOrder.ServiceOrderStatus newStatus) {
        log.info("Updating service order {} state to {}", id, newStatus);
        ServiceOrder order = getServiceOrder(id);
        order.setStatus(newStatus);
        order.setUpdatedAt(java.time.Instant.now());
        
        if (newStatus == ServiceOrder.ServiceOrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }
        
        return serviceOrderRepository.save(order);
    }
}