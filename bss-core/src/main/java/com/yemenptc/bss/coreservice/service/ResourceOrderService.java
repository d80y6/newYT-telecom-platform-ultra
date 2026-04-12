package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ResourceOrder;
import com.yemenptc.bss.coreservice.repository.ResourceOrderRepository;
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
public class ResourceOrderService {

    private final ResourceOrderRepository resourceOrderRepository;

    @Transactional
    public ResourceOrder createResourceOrder(ResourceOrder request) {
        log.info("Creating resource order: type={}, customer={}", request.getOrderType(), request.getCustomerId());

        ResourceOrder order = ResourceOrder.builder()
            .externalId("RO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .orderType(request.getOrderType())
            .state(ResourceOrder.ResourceOrderState.PENDING)
            .priority(request.getPriority() != null ? request.getPriority() : ResourceOrder.OrderPriority.NORMAL)
            .requestedStartDate(request.getRequestedStartDate())
            .requestedCompletionDate(request.getRequestedCompletionDate())
            .description(request.getDescription())
            .customerId(request.getCustomerId())
            .customerName(request.getCustomerName())
            .serviceAccountId(request.getServiceAccountId())
            .resourceType(request.getResourceType())
            .resourceQuantity(request.getResourceQuantity())
            .locationId(request.getLocationId())
            .locationName(request.getLocationName())
            .installationAddress(request.getInstallationAddress())
            .contactPerson(request.getContactPerson())
            .contactPhone(request.getContactPhone())
            .notes(request.getNotes())
            .build();

        ResourceOrder saved = resourceOrderRepository.save(order);
        log.info("Resource order created: {}", saved.getExternalId());
        return saved;
    }

    @Transactional(readOnly = true)
    public ResourceOrder getResourceOrder(UUID id) {
        return resourceOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resource order not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ResourceOrder> listResourceOrders(Pageable pageable) {
        return resourceOrderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ResourceOrder> findByState(ResourceOrder.ResourceOrderState state, Pageable pageable) {
        return resourceOrderRepository.findByState(state, pageable);
    }

    @Transactional
    public ResourceOrder updateState(UUID id, ResourceOrder.ResourceOrderState newState) {
        log.info("Updating resource order {} state to {}", id, newState);
        
        ResourceOrder order = getResourceOrder(id);
        order.setState(newState);
        
        if (newState == ResourceOrder.ResourceOrderState.COMPLETED) {
            order.setCompletionDate(LocalDateTime.now());
        }
        
        return resourceOrderRepository.save(order);
    }

    @Transactional
    public ResourceOrder acknowledgeOrder(UUID id) {
        return updateState(id, ResourceOrder.ResourceOrderState.ACKNOWLEDGED);
    }

    @Transactional
    public ResourceOrder startOrder(UUID id) {
        return updateState(id, ResourceOrder.ResourceOrderState.IN_PROGRESS);
    }

    @Transactional
    public ResourceOrder completeOrder(UUID id) {
        return updateState(id, ResourceOrder.ResourceOrderState.COMPLETED);
    }

    @Transactional
    public ResourceOrder cancelOrder(UUID id, String reason) {
        log.info("Cancelling resource order {}: {}", id, reason);
        ResourceOrder order = getResourceOrder(id);
        order.setState(ResourceOrder.ResourceOrderState.CANCELLED);
        order.setCancellationReason(reason);
        return resourceOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public long countByState(ResourceOrder.ResourceOrderState state) {
        return resourceOrderRepository.countByState(state);
    }
}