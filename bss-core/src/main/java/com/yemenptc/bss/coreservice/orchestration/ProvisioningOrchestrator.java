package com.yemenptc.bss.coreservice.orchestration;

import com.yemenptc.bss.coreservice.adapter.AdapterRegistry;
import com.yemenptc.bss.coreservice.adapter.NetworkAdapter;
import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import com.yemenptc.bss.coreservice.entity.NetworkProvisioningOrder;
import com.yemenptc.bss.coreservice.repository.OrderRepository;
import com.yemenptc.bss.coreservice.repository.ServiceOrderRepository;
import com.yemenptc.bss.coreservice.repository.NetworkProvisioningOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final String PROVISIONING_ORDER_KEY = "provisioning:order:";
    
    @Transactional
    public ProvisioningResult provisionOrder(String orderId) {
        log.info("Starting provisioning orchestration for order: {}", orderId);
        
        Order order = orderRepository.findByOrderNumber(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        if (order.getStatus() != Order.OrderStatus.ACKNOWLEDGED) {
            return ProvisioningResult.failure(orderId, "INVALID_ORDER_STATE", 
                    "Order must be in ACKNOWLEDGED state");
        }
        
        String sagaId = UUID.randomUUID().toString();
        SagaState saga = new SagaState();
        saga.sagaId = sagaId;
        saga.orderId = orderId;
        saga.state = SagaState.State.INITIATED;
        saga.startedAt = Instant.now();
        saga.steps = new ArrayList<>();
        saga.compensations = new ArrayList<>();
        
        activeSagas.put(sagaId, saga);
        persistSagaState(saga);
        
        try {
            saga.state = SagaState.State.IN_PROGRESS;
            
            List<Order.StepResult> stepResults = new ArrayList<>();
            
            stepResults.add(executeStep(saga, "VALIDATE_ORDER", () -> validateOrder(order)));
            if (hasFailed(stepResults)) {
                return compensateAndFail(saga, stepResults);
            }
            
            stepResults.add(executeStep(saga, "CREATE_SERVICE_ORDERS", () -> createServiceOrders(order, saga)));
            if (hasFailed(stepResults)) {
                return compensateAndFail(saga, stepResults);
            }
            
            stepResults.add(executeStep(saga, "PROVISION_NETWORK", () -> provisionNetwork(order, saga)));
            if (hasFailed(stepResults)) {
                return compensateAndFail(saga, stepResults);
            }
            
            stepResults.add(executeStep(saga, "ACTIVATE_SERVICES", () -> activateServices(order, saga)));
            if (hasFailed(stepResults)) {
                return compensateAndFail(saga, stepResults);
            }
            
            stepResults.add(executeStep(saga, "CONFIRM_ORDER", () -> confirmOrder(order)));
            
            order.setStatus(Order.OrderStatus.COMPLETED);
            order.setCompletedAt(Instant.now());
            orderRepository.save(order);
            
            saga.state = SagaState.State.COMPLETED;
            saga.completedAt = Instant.now();
            persistSagaState(saga);
            
            log.info("Provisioning completed successfully for order: {}", orderId);
            
            return ProvisioningResult.success(orderId, sagaId, stepResults);
            
        } catch (Exception e) {
            log.error("Provisioning failed for order {}: {}", orderId, e.getMessage());
            return compensateAndFail(saga, Collections.emptyList());
        }
    }
    
    private Order.StepResult validateOrder(Order order) {
        log.info("Validating order: {}", order.getOrderNumber());
        
        if (order.getCustomerId() == null) {
            return StepResult.builder()
                    .stepId("VALIDATE_ORDER")
                    .stepName("Validate Order")
                    .status(StepResult.StepStatus.FAILED)
                    .errorCode("CUSTOMER_ID_REQUIRED")
                    .errorMessage("Customer ID is required")
                    .startedAt(Instant.now())
                    .build();
        }
        
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return Order.StepResult.failure("VALIDATE_ORDER", "Order has no items");
        }
        
        return Order.StepResult.success("VALIDATE_ORDER", "Order validated");
    }
    
    private Order.StepResult createServiceOrders(Order order, SagaState saga) {
        log.info("Creating service orders for: {}", order.getOrderNumber());
        
        List<Order.StepResult> results = new ArrayList<>();
        
        try {
            List<ServiceOrder> serviceOrders = new ArrayList<>();
            
            ServiceOrder serviceOrder = ServiceOrder.builder()
                    .orderNumber("SO-" + UUID.randomUUID().toString().substring(0, 8))
                    .partyId(order.getCustomerId())
                    .productOrderId(order.getId().toString())
                    .orderType(ServiceOrder.OrderType.NEW)
                    .status(ServiceOrder.ServiceOrderStatus.IN_PROGRESS)
                    .cfsType("FTTH")
                    .rfsType("INTERNET")
                    .build();
            
            serviceOrder = serviceOrderRepository.save(serviceOrder);
            serviceOrders.add(serviceOrder);
            
            saga.serviceOrderId = serviceOrder.getId().toString();
            
            log.info("Created service order: {}", serviceOrder.getOrderNumber());
            
            return Order.StepResult.success("CREATE_SERVICE_ORDERS", 
                    "Created " + serviceOrders.size() + " service orders");
            
        } catch (Exception e) {
            return Order.StepResult.failure("CREATE_SERVICE_ORDERS", e.getMessage());
        }
    }
    
    private Order.StepResult provisionNetwork(Order order, SagaState saga) {
        log.info("Provisioning network for: {}", order.getOrderNumber());
        
        try {
            NetworkAdapter.ProvisionRequest request = NetworkAdapter.ProvisionRequest.builder()
                    .orderId(order.getId().toString())
                    .serviceId(saga.serviceOrderId)
                    .customerId(order.getCustomerId())
                    .accountId(order.getAccountId())
                    .serviceType("INTERNET")
                    .technology("FTTH")
                    .serviceIdentifier(order.getPrimaryPhone())
                    .parameters(new HashMap<>())
                    .requestedAt(Instant.now())
                    .build();
            
            NetworkAdapter.ProvisioningResult result = adapterRegistry.provision("FTTH", request);
            
            if (!result.isSuccess()) {
                saga.compensations.add(() -> rollbackProvision(saga.serviceOrderId));
                return Order.StepResult.failure("PROVISION_NETWORK", 
                        result.getErrorCode() + ": " + result.getMessage());
            }
            
            saga.provisionedDetails = result.getProvisionedDetails();
            
            persistProvisioningOrder(saga, result);
            
            log.info("Network provisioned for order {}: {}", order.getOrderNumber(), 
                    result.getProvisionedDetails());
            
            return Order.StepResult.success("PROVISION_NETWORK", 
                    "Network provisioned: " + result.getStatus());
            
        } catch (Exception e) {
            log.error("Network provisioning failed: {}", e.getMessage());
            saga.compensations.add(() -> rollbackProvision(saga.serviceOrderId));
            return Order.StepResult.failure("PROVISION_NETWORK", e.getMessage());
        }
    }
    
    private Order.StepResult activateServices(Order order, SagaState saga) {
        log.info("Activating services for: {}", order.getOrderNumber());
        
        try {
            NetworkAdapter adapter = adapterRegistry.getAdapterForTechnology("FTTH")
                    .orElseThrow(() -> new RuntimeException("FTTX adapter not found"));
            
            NetworkAdapter.StatusResult status = adapter.getStatus(saga.serviceOrderId);
            
            if (!status.isSuccess()) {
                return Order.StepResult.failure("ACTIVATE_SERVICES", "Status check failed");
            }
            
            if (!"ACTIVE".equals(status.getStatus())) {
                return Order.StepResult.failure("ACTIVATE_SERVICES", 
                        "Service not active: " + status.getStatus());
            }
            
            return Order.StepResult.success("ACTIVATE_SERVICES", "Services activated");
            
        } catch (Exception e) {
            return Order.StepResult.failure("ACTIVATE_SERVICES", e.getMessage());
        }
    }
    
    private Order.StepResult confirmOrder(Order order) {
        log.info("Confirming order: {}", order.getOrderNumber());
        
        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setCompletedAt(Instant.now());
        orderRepository.save(order);
        
        return Order.StepResult.success("CONFIRM_ORDER", "Order confirmed");
    }
    
    private Order.StepResult executeStep(SagaState saga, String stepName, 
            java.util.function.Supplier<Order.StepResult> step) {
        Instant start = Instant.now();
        
        try {
            saga.currentStep = stepName;
            persistSagaState(saga);
            
            Order.StepResult result = step.get();
            
            saga.steps.add(SagaState.StepInfo.builder()
                    .stepName(stepName)
                    .success(result.isSuccess())
                    .message(result.getMessage())
                    .startedAt(start)
                    .completedAt(Instant.now())
                    .build());
            
            persistSagaState(saga);
            
            log.info("Step {} {}: {}", stepName, result.isSuccess() ? "SUCCESS" : "FAILED", 
                    result.getMessage());
            
            return result;
            
        } catch (Exception e) {
            log.error("Step {} threw exception: {}", stepName, e.getMessage());
            
            saga.steps.add(SagaState.StepInfo.builder()
                    .stepName(stepName)
                    .success(false)
                    .message(e.getMessage())
                    .startedAt(start)
                    .completedAt(Instant.now())
                    .build());
            
            persistSagaState(saga);
            
            return Order.StepResult.failure(stepName, e.getMessage());
        }
    }
    
    private boolean hasFailed(List<Order.StepResult> results) {
        return results.stream().anyMatch(r -> !r.isSuccess());
    }
    
    private ProvisioningResult compensateAndFail(SagaState saga, List<Order.StepResult> steps) {
        log.warn("Saga {} failed, executing compensations", saga.sagaId);
        
        saga.state = SagaState.State.COMPENSATING;
        persistSagaState(saga);
        
        List<String> compensationsExecuted = new ArrayList<>();
        
        for (int i = saga.compensations.size() - 1; i >= 0; i--) {
            try {
                saga.compensations.get(i).run();
                compensationsExecuted.add("Compensation " + i);
            } catch (Exception e) {
                log.error("Compensation {} failed: {}", i, e.getMessage());
            }
        }
        
        saga.state = SagaState.State.FAILED;
        saga.completedAt = Instant.now();
        saga.compensationsExecuted = compensationsExecuted;
        persistSagaState(saga);
        
        String failureReason = steps.stream()
                .filter(r -> !r.isSuccess())
                .map(r -> r.getStepName() + ": " + r.getMessage())
                .findFirst()
                .orElse("Unknown failure");
        
        return ProvisioningResult.failure(saga.orderId, "PROVISIONING_FAILED", 
                failureReason);
    }
    
    private void rollbackProvision(String serviceId) {
        if (serviceId == null) return;
        
        try {
            NetworkAdapter adapter = adapterRegistry.getAdapterForTechnology("FTTH")
                    .orElse(null);
            
            if (adapter != null) {
                adapter.deprovision(serviceId);
                log.info("Rolled back provisioning for: {}", serviceId);
            }
        } catch (Exception e) {
            log.error("Failed to rollback provisioning: {}", e.getMessage());
        }
    }
    
    private void persistSagaState(SagaState saga) {
        String key = SAGA_KEY_PREFIX + saga.sagaId;
        try {
            redisTemplate.opsForValue().set(key, saga, Duration.ofHours(24));
        } catch (Exception e) {
            log.warn("Failed to persist saga state: {}", e.getMessage());
        }
    }
    
    private void persistProvisioningOrder(SagaState saga, NetworkAdapter.ProvisioningResult result) {
        NetworkProvisioningOrder order = NetworkProvisioningOrder.builder()
                .serviceId(saga.serviceOrderId)
                .orderId(saga.orderId)
                .status(result.getStatus())
                .ipAddress(result.getProvisionedDetails().get("ipAddress"))
                .technology("FTTH")
                .build();
        
        provisioningOrderRepository.save(order);
    }
    
    public Optional<SagaState> getSagaState(String sagaId) {
        String key = SAGA_KEY_PREFIX + sagaId;
        try {
            Object state = redisTemplate.opsForValue().get(key);
            if (state instanceof SagaState) {
                return Optional.of((SagaState) state);
            }
        } catch (Exception e) {
            log.warn("Failed to load saga state: {}", e.getMessage());
        }
        
        return Optional.ofNullable(activeSagas.get(sagaId));
    }
    
    @lombok.Data
    @lombok.Builder
    public static class ProvisioningResult {
        private boolean success;
        private String orderId;
        private String sagaId;
        private String errorCode;
        private String errorMessage;
        private List<Order.StepResult> steps;
        private Instant completedAt;
        
        public static ProvisioningResult success(String orderId, String sagaId, 
                List<Order.StepResult> steps) {
            return ProvisioningResult.builder()
                    .success(true)
                    .orderId(orderId)
                    .sagaId(sagaId)
                    .steps(steps)
                    .completedAt(Instant.now())
                    .build();
        }
        
        public static ProvisioningResult failure(String orderId, String errorCode, 
                String errorMessage) {
            return ProvisioningResult.builder()
                    .success(false)
                    .orderId(orderId)
                    .errorCode(errorCode)
                    .errorMessage(errorMessage)
                    .completedAt(Instant.now())
                    .build();
        }
    }
    
    @lombok.Data
    public static class SagaState {
        private String sagaId;
        private String orderId;
        private String serviceOrderId;
        private State state;
        private String currentStep;
        private Instant startedAt;
        private Instant completedAt;
        private List<StepInfo> steps;
        private List<Runnable> compensations;
        private List<String> compensationsExecuted;
        private Map<String, String> provisionedDetails;
        
        public enum State {
            INITIATED, IN_PROGRESS, COMPENSATING, COMPLETED, FAILED
        }
        
        @lombok.Data
        @lombok.Builder
        public static class StepInfo {
            private String stepName;
            private boolean success;
            private String message;
            private Instant startedAt;
            private Instant completedAt;
        }
    }
}
