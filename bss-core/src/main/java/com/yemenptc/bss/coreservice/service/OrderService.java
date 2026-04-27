package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.repository.OrderRepository;
import com.yemenptc.bss.coreservice.temporal.FtthOrderWorkflow;
import com.yemenptc.bss.coreservice.temporal.MobileActivationWorkflow;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
    private final WorkflowClient workflowClient;

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
            .orderItems(request.getOrderItems())
            .build();
        
        // Associate items with order
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }

        Order saved = orderRepository.save(order);
        log.info("Order created: {}", saved.getOrderNumber());

        // Trigger orchestration based on item types
        if (saved.getOrderItems() != null) {
            saved.getOrderItems().forEach(item -> {
                if ("FTTH".equalsIgnoreCase(item.getItemType()) || 
                    (item.getProductOfferingId() != null && item.getProductOfferingId().contains("FTTH"))) {
                    startFtthWorkflow(saved);
                } else if ("MOBILE".equalsIgnoreCase(item.getItemType())) {
                    startMobileWorkflow(saved, item.getProductOfferingId());
                }
            });
        }

        return saved;
    }

    private void startFtthWorkflow(Order order) {
        log.info("Starting FTTH Temporal workflow for order: {}", order.getId());
        
        FtthOrderWorkflow workflow = workflowClient.newWorkflowStub(
                FtthOrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("FTTH_ORDER_TASK_QUEUE")
                        .setWorkflowId("FTTH-" + order.getId())
                        .build());

        Map<String, Object> config = new HashMap<>();
        config.put("partyId", order.getCustomerId().toString());
        config.put("orderNumber", order.getOrderNumber());

        WorkflowClient.start(workflow::executeFtthOrder, order.getId().toString(), config);
    }

    private void startMobileWorkflow(Order order, String planId) {
        log.info("Starting Mobile Activation Temporal workflow for order: {}", order.getId());

        MobileActivationWorkflow workflow = workflowClient.newWorkflowStub(
                MobileActivationWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("MOBILE_ACTIVATION_TASK_QUEUE")
                        .setWorkflowId("MOBILE-" + order.getId())
                        .build());

        // Assuming MSISDN might be provided in order notes or characteristics in a real scenario
        String msisdn = order.getPrimaryPhone() != null ? order.getPrimaryPhone() : "RESERVE_NEW";
        
        WorkflowClient.start(workflow::activateMobile, order.getId().toString(), msisdn, planId);
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
