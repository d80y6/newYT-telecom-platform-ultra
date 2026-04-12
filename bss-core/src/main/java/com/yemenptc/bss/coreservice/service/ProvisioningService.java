package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import com.yemenptc.bss.coreservice.repository.ServiceOrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProvisioningService {

    private final ServiceOrderRepository serviceOrderRepository;

    @Transactional
    @CircuitBreaker(name = "provisioningService", fallbackMethod = "createServiceOrderFallback")
    @Retry(name = "provisioningService")
    @Bulkhead(name = "provisioningService")
    public ServiceOrder createServiceOrder(ServiceOrder request) {
        ServiceOrder order = ServiceOrder.builder()
            .orderNumber("SO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .partyId(request.getPartyId())
            .productOrderId(request.getProductOrderId())
            .orderType(request.getOrderType())
            .status(ServiceOrder.ServiceOrderStatus.ACKNOWLEDGED)
            .cfsType(request.getCfsType())
            .rfsType(request.getRfsType())
            .build();
        return serviceOrderRepository.save(order);
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
    public List<ServiceOrder> getOrdersByParty(UUID partyId) {
        return serviceOrderRepository.findByPartyId(partyId);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrder> getOrdersByStatus(ServiceOrder.ServiceOrderStatus status) {
        return serviceOrderRepository.findByStatus(status);
    }

    @Transactional
    public ServiceOrder updateOrderStatus(UUID id, ServiceOrder.ServiceOrderStatus status) {
        ServiceOrder order = getServiceOrder(id);
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        if (status == ServiceOrder.ServiceOrderStatus.COMPLETED) {
            order.setCompletedAt(Instant.now());
        }
        return serviceOrderRepository.save(order);
    }

    @Transactional
    public ServiceOrder completeOrder(UUID id) {
        ServiceOrder order = getServiceOrder(id);
        order.setStatus(ServiceOrder.ServiceOrderStatus.COMPLETED);
        order.setCompletedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        return serviceOrderRepository.save(order);
    }

    @Transactional
    public ServiceOrder failOrder(UUID id, String errorMessage) {
        ServiceOrder order = getServiceOrder(id);
        order.setStatus(ServiceOrder.ServiceOrderStatus.FAILED);
        order.setUpdatedAt(Instant.now());
        return serviceOrderRepository.save(order);
    }

    private ServiceOrder createServiceOrderFallback(ServiceOrder request, Throwable t) {
        log.error("Circuit breaker fallback for createServiceOrder: {}", t.getMessage());
        throw new RuntimeException("Provisioning service temporarily unavailable. Please try again later.", t);
    }
}
