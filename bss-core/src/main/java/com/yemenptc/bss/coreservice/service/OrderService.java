package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.repository.OrderRepository;
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
import java.util.UUID;

/**
 * Order Service - TMF622 Product Ordering Management API
 * Manages product order lifecycle
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    @Retry(name = "orderService")
    @Bulkhead(name = "orderService")
    public Order createOrder(Order request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        
        Order order = Order.builder()
            .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerId(request.getCustomerId())
            .orderType(request.getOrderType())
            .status(Order.OrderStatus.ACKNOWLEDGED)
            .priority(request.getPriority() != null ? request.getPriority() : Order.OrderPriority.MEDIUM)
            .channel(request.getChannel())
            .notes(request.getNotes())
            .build();
        
        Order saved = orderRepository.save(order);
        log.info("Order created: {}", saved.getOrderNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
    }

    @Transactional(readOnly = true)
    public Page<Order> listOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrdersByCustomer(UUID customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Order updateOrder(UUID id, Order request) {
        Order existing = getOrder(id);
        
        Order updated = Order.builder()
            .orderNumber(existing.getOrderNumber())
            .customerId(request.getCustomerId() != null ? request.getCustomerId() : existing.getCustomerId())
            .orderType(request.getOrderType() != null ? request.getOrderType() : existing.getOrderType())
            .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
            .priority(request.getPriority() != null ? request.getPriority() : existing.getPriority())
            .channel(request.getChannel() != null ? request.getChannel() : existing.getChannel())
            .notes(request.getNotes() != null ? request.getNotes() : existing.getNotes())
            .completedAt(request.getStatus() == Order.OrderStatus.COMPLETED ? Instant.now() : existing.getCompletedAt())
            .build();
        
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(Instant.now());
        updated.setVersion(existing.getVersion() + 1);
        
        return orderRepository.save(updated);
    }

    @Transactional
    public void deleteOrder(UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

    private Order createOrderFallback(Order request, Throwable t) {
        log.error("Circuit breaker fallback for createOrder: {}", t.getMessage());
        throw new RuntimeException("Service temporarily unavailable. Please try again later.", t);
    }
}
