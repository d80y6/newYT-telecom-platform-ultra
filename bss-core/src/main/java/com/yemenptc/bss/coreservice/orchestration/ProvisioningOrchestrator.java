package com.yemenptc.bss.coreservice.orchestration;

import com.yemenptc.bss.coreservice.adapter.AdapterRegistry;
import com.yemenptc.bss.coreservice.adapter.NetworkAdapter;
import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import com.yemenptc.bss.coreservice.entity.NetworkProvisioningOrder;
import com.yemenptc.bss.coreservice.repository.OrderRepository;
import com.yemenptc.bss.coreservice.repository.ServiceOrderRepository;
import com.yemenptc.bss.coreservice.repository.NetworkProvisioningOrderRepository;

import io.temporal.client.WorkflowClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvisioningOrchestrator {

    private final AdapterRegistry adapterRegistry;
    private final OrderRepository orderRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final NetworkProvisioningOrderRepository provisioningOrderRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final Map<String, SagaState> activeSagas = new ConcurrentHashMap<>();

    private static final String SAGA_KEY_PREFIX = "saga:";

    public ProvisioningResult provisionOrder(String orderId) {
        log.info("Starting provisioning for order: {}", orderId);
        Order order = orderRepository.findById(UUID.fromString(orderId)).orElseThrow(() -> new RuntimeException("Order not found"));
        
        SagaState saga = activeSagas.computeIfAbsent(orderId, k -> new SagaState());
        saga.workflowId = UUID.randomUUID().toString();
        
        List<Order.StepResult> stepResults = new ArrayList<>();

        // Step 1: Validate Order
        Order.StepResult validationResult = validateOrder(order);
        stepResults.add(validationResult);
        if (!validationResult.isSuccess()) {
            return ProvisioningResult.failure(orderId, "VALIDATION_FAILED", "Order validation failed", stepResults);
        }

        // Step 2: Create Service Orders
        Order.StepResult serviceOrderResult = createServiceOrders(order, saga);
        stepResults.add(serviceOrderResult);
        if (!serviceOrderResult.isSuccess()) {
            return ProvisioningResult.failure(orderId, "SERVICE_ORDER_CREATION_FAILED", "Failed to create service orders", stepResults);
        }

        // Step 3: Provision Network Resources
        Order.StepResult networkProvisioningResult = provisionNetwork(order, saga);
        stepResults.add(networkProvisioningResult);
        if (!networkProvisioningResult.isSuccess()) {
            return ProvisioningResult.failure(orderId, "NETWORK_PROVISIONING_FAILED", "Network provisioning failed", stepResults);
        }

        // Step 4: Activate Services
        Order.StepResult serviceActivationResult = activateServices(order, saga);
        stepResults.add(serviceActivationResult);
        if (!serviceActivationResult.isSuccess()) {
            return ProvisioningResult.failure(orderId, "SERVICE_ACTIVATION_FAILED", "Service activation failed", stepResults);
        }

        // Step 5: Confirm Order
        Order.StepResult confirmResult = confirmOrder(order, saga);
        stepResults.add(confirmResult);
        if (!confirmResult.isSuccess()) {
            return ProvisioningResult.failure(orderId, "ORDER_CONFIRMATION_FAILED", "Failed to confirm order", stepResults);
        }

        order.setStatus(Order.OrderStatus.COMPLETED);
        orderRepository.save(order);

        activeSagas.remove(orderId);
        return ProvisioningResult.success(orderId, stepResults);
    }

    private Order.StepResult validateOrder(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return Order.StepResult.failure("EMPTY_ORDER", "Order has no items");
        }
        return Order.StepResult.success("VALIDATION", "Order validation");
    }

    private Order.StepResult createServiceOrders(Order order, SagaState saga) {
        log.info("Creating service orders for order: {}", order.getId());
        try {
            order.getOrderItems().forEach(item -> {
                ServiceOrder serviceOrder = new ServiceOrder();
                serviceOrder.setProductOrderId(order.getId());
                serviceOrder.setOrderNumber("SO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                serviceOrder.setStatus(ServiceOrder.ServiceOrderStatus.IN_PROGRESS);
                serviceOrder.setOrderType(mapItemActionToServiceOrderType(item.getAction()));
                serviceOrderRepository.save(serviceOrder);
            });
            return Order.StepResult.success("SERVICE_ORDER_CREATION", "Service order creation");
        } catch (Exception e) {
            log.error("Error creating service orders", e);
            return Order.StepResult.failure("SERVICE_ORDER_ERROR", e.getMessage());
        }
    }

    private Order.StepResult provisionNetwork(Order order, SagaState saga) {
        log.info("Provisioning network for order: {}", order.getId());
        // Simplified network provisioning logic
        return Order.StepResult.success("NETWORK_PROVISIONING", "Network provisioning");
    }

    private Order.StepResult activateServices(Order order, SagaState saga) {
        log.info("Activating services for order: {}", order.getId());
        // Simplified service activation logic
        return Order.StepResult.success("SERVICE_ACTIVATION", "Service activation");
    }

    private Order.StepResult confirmOrder(Order order, SagaState saga) {
        log.info("Confirming order: {}", order.getId());
        return Order.StepResult.success("ORDER_CONFIRMATION", "Order confirmation");
    }

    private Order.StepResult compensateAndFail(SagaState saga, List<Order.StepResult> steps) {
        log.warn("Initiating compensation for order, saga: {}", saga.workflowId);
        // Implement compensation logic here
        return Order.StepResult.failure("SAGA_COMPENSATION", "Orchestration failed, compensation initiated.");
    }

    private ServiceOrder.ServiceOrderType mapOrderTypeToServiceOrderType(Order.OrderType orderType) {
        if (orderType == null) return ServiceOrder.ServiceOrderType.ACTIVATION;
        return switch (orderType) {
            case ACQUISITION -> ServiceOrder.ServiceOrderType.ACTIVATION;
            case MODIFICATION -> ServiceOrder.ServiceOrderType.MODIFICATION;
            case TERMINATION -> ServiceOrder.ServiceOrderType.DEACTIVATION;
            case SUSPENSION -> ServiceOrder.ServiceOrderType.SUSPENSION;
            case TRANSFER -> ServiceOrder.ServiceOrderType.MODIFICATION;
            default -> ServiceOrder.ServiceOrderType.ACTIVATION;
        };
    }

    private ServiceOrder.ServiceOrderType mapItemActionToServiceOrderType(String action) {
        if (action == null) return ServiceOrder.ServiceOrderType.ACTIVATION;
        return switch (action.toUpperCase()) {
            case "ADD" -> ServiceOrder.ServiceOrderType.ACTIVATION;
            case "REMOVE" -> ServiceOrder.ServiceOrderType.DEACTIVATION;
            case "MODIFY" -> ServiceOrder.ServiceOrderType.MODIFICATION;
            case "SUSPEND" -> ServiceOrder.ServiceOrderType.SUSPENSION;
            default -> ServiceOrder.ServiceOrderType.ACTIVATION;
        };
    }
}
