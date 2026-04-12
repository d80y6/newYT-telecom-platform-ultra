package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.NetworkProvisioningOrder;
import com.yemenptc.bss.coreservice.repository.NetworkProvisioningOrderRepository;
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
public class NetworkProvisioningService {

    private final NetworkProvisioningOrderRepository provisioningOrderRepository;

    @Transactional
    public NetworkProvisioningOrder createProvisioningOrder(NetworkProvisioningOrder request) {
        log.info("Creating provisioning order: type={}, service={}", request.getOrderType(), request.getServiceType());

        NetworkProvisioningOrder order = NetworkProvisioningOrder.builder()
            .orderId("PROV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .orderType(request.getOrderType())
            .status(NetworkProvisioningOrder.ProvisionOrderStatus.PENDING)
            .customerId(request.getCustomerId())
            .serviceType(request.getServiceType())
            .serviceId(request.getServiceId())
            .productId(request.getProductId())
            .locationId(request.getLocationId())
            .address(request.getAddress())
            .installationDate(request.getInstallationDate())
            .priority(request.getPriority() != null ? request.getPriority() : NetworkProvisioningOrder.ProvisionPriority.NORMAL)
            .equipmentRequired(request.getEquipmentRequired())
            .notes(request.getNotes())
            .build();

        NetworkProvisioningOrder saved = provisioningOrderRepository.save(order);
        log.info("Provisioning order created: {}", saved.getOrderId());
        return saved;
    }

    @Transactional(readOnly = true)
    public NetworkProvisioningOrder getProvisioningOrder(UUID id) {
        return provisioningOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Provisioning order not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<NetworkProvisioningOrder> listProvisioningOrders(Pageable pageable) {
        return provisioningOrderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<NetworkProvisioningOrder> findByStatus(NetworkProvisioningOrder.ProvisionOrderStatus status, Pageable pageable) {
        return provisioningOrderRepository.findByStatus(status, pageable);
    }

    @Transactional
    public NetworkProvisioningOrder scheduleOrder(UUID id, LocalDateTime installationDate, String technicianId) {
        log.info("Scheduling provisioning order {} for {}", id, installationDate);
        
        NetworkProvisioningOrder order = getProvisioningOrder(id);
        order.setStatus(NetworkProvisioningOrder.ProvisionOrderStatus.SCHEDULED);
        order.setInstallationDate(installationDate);
        order.setTechnicianId(technicianId);
        
        return provisioningOrderRepository.save(order);
    }

    @Transactional
    public NetworkProvisioningOrder assignTechnician(UUID id, String technicianId, String technicianName) {
        log.info("Assigning technician {} to order {}", technicianId, id);
        
        NetworkProvisioningOrder order = getProvisioningOrder(id);
        order.setTechnicianId(technicianId);
        order.setTechnicianName(technicianName);
        order.setStatus(NetworkProvisioningOrder.ProvisionOrderStatus.ASSIGNED);
        
        return provisioningOrderRepository.save(order);
    }

    @Transactional
    public NetworkProvisioningOrder startWork(UUID id) {
        log.info("Starting work on provisioning order {}", id);
        
        NetworkProvisioningOrder order = getProvisioningOrder(id);
        order.setStatus(NetworkProvisioningOrder.ProvisionOrderStatus.IN_PROGRESS);
        order.setStartedAt(LocalDateTime.now());
        
        return provisioningOrderRepository.save(order);
    }

    @Transactional
    public NetworkProvisioningOrder completeOrder(UUID id, String completionNotes) {
        log.info("Completing provisioning order {}", id);
        
        NetworkProvisioningOrder order = getProvisioningOrder(id);
        order.setStatus(NetworkProvisioningOrder.ProvisionOrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        order.setCompletionNotes(completionNotes);
        
        return provisioningOrderRepository.save(order);
    }

    @Transactional
    public NetworkProvisioningOrder failOrder(UUID id, String reason) {
        log.info("Marking provisioning order {} as failed: {}", id, reason);
        
        NetworkProvisioningOrder order = getProvisioningOrder(id);
        order.setStatus(NetworkProvisioningOrder.ProvisionOrderStatus.FAILED);
        order.setCompletionNotes(reason);
        order.setCompletedAt(LocalDateTime.now());
        
        return provisioningOrderRepository.save(order);
    }
}