package com.yemenptc.bss.coreservice.orchestration;

import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.entity.OrderItem;
import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import com.yemenptc.bss.coreservice.repository.OrderItemRepository;
import com.yemenptc.bss.coreservice.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order Decomposition Engine
 * Converts Product Orders (TMF622) to Service Orders (TMF641)
 * Each OrderItem maps to a ServiceOrder for provisioning
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDecomposer {

    private final ServiceOrderRepository serviceOrderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Decompose a Product Order into Service Orders
     * Each OrderItem becomes a ServiceOrder for network provisioning
     */
    @Transactional
    public List<ServiceOrder> decompose(Order productOrder) {
        log.info("Starting order decomposition for order: {}", productOrder.getOrderNumber());
        
        List<ServiceOrder> serviceOrders = new ArrayList<>();
        
        // Get all order items for this product order
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(productOrder.getId().toString());
        
        if (orderItems.isEmpty()) {
            log.info("No order items found, creating default service order for order: {}", productOrder.getOrderNumber());
            // Create a default service order if no items exist
            ServiceOrder defaultServiceOrder = createDefaultServiceOrder(productOrder);
            serviceOrders.add(defaultServiceOrder);
        } else {
            // Decompose each order item into a service order
            for (OrderItem item : orderItems) {
                ServiceOrder serviceOrder = createServiceOrderFromItem(productOrder, item);
                serviceOrders.add(serviceOrder);
                
                // Update order item with service order reference
                item.setStatus(OrderItem.ItemStatus.PROCESSING);
                item.setUpdatedAt(Instant.now());
                orderItemRepository.save(item);
            }
        }
        
        log.info("Order decomposition complete: {} service orders created for order: {}", 
                serviceOrders.size(), productOrder.getOrderNumber());
        
        return serviceOrders;
    }

    /**
     * Create a ServiceOrder from an OrderItem
     */
    private ServiceOrder createServiceOrderFromItem(Order productOrder, OrderItem item) {
        ServiceOrder serviceOrder = ServiceOrder.builder()
            .orderNumber("SVO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .partyId(productOrder.getCustomerId())
            .productOrderId(productOrder.getId())
            .status(ServiceOrder.ServiceOrderStatus.ACKNOWLEDGED)
            .orderType(mapItemActionToServiceOrderType(item.getAction()))
            .cfsType(determineCfsType(item.getProductOfferingId()))
            .rfsType(determineRfsType(item.getItemType()))
            .build();
        
        serviceOrder.setCreatedAt(Instant.now());
        serviceOrder.setUpdatedAt(Instant.now());
        
        return serviceOrderRepository.save(serviceOrder);
    }

    /**
     * Create a default Service Order when no items exist
     */
    private ServiceOrder createDefaultServiceOrder(Order productOrder) {
        ServiceOrder serviceOrder = ServiceOrder.builder()
            .orderNumber("SVO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .partyId(productOrder.getCustomerId())
            .productOrderId(productOrder.getId())
            .status(ServiceOrder.ServiceOrderStatus.ACKNOWLEDGED)
            .orderType(mapOrderTypeToServiceOrderType(productOrder.getOrderType()))
            .cfsType("DEFAULT")
            .rfsType("DEFAULT")
            .build();
        
        serviceOrder.setCreatedAt(Instant.now());
        serviceOrder.setUpdatedAt(Instant.now());
        
        return serviceOrderRepository.save(serviceOrder);
    }

    /**
     * Map OrderItem action to ServiceOrderType
     */
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

    /**
     * Map OrderType to ServiceOrderType
     */
    private ServiceOrder.ServiceOrderType mapOrderTypeToServiceOrderType(Order.OrderType orderType) {
        if (orderType == null) return ServiceOrder.ServiceOrderType.ACTIVATION;
        
        return switch (orderType) {
            case ACQUISITION -> ServiceOrder.ServiceOrderType.ACTIVATION;
            case TERMINATION -> ServiceOrder.ServiceOrderType.DEACTIVATION;
            case MODIFICATION -> ServiceOrder.ServiceOrderType.MODIFICATION;
            case SUSPENSION -> ServiceOrder.ServiceOrderType.SUSPENSION;
            case TRANSFER -> ServiceOrder.ServiceOrderType.MODIFICATION;
        };
    }

    /**
     * Determine CFS (Carrier Facing Service) type from product offering
     */
    private String determineCfsType(String productOfferingId) {
        // In production, this would look up the product offering
        // For now, return based on offering ID pattern
        if (productOfferingId == null) return "MOBILE_VOICE";
        
        if (productOfferingId.contains("DATA")) return "MOBILE_DATA";
        if (productOfferingId.contains("VOICE")) return "MOBILE_VOICE";
        if (productOfferingId.contains("SMS")) return "MOBILE_SMS";
        if (productOfferingId.contains("FIXED")) return "FIXED_BROADBAND";
        
        return "MOBILE_VOICE";
    }

    /**
     * Determine RFS (Resource Facing Service) type from item type
     */
    private String determineRfsType(String itemType) {
        if (itemType == null) return "SUBSCRIPTION";
        
        return switch (itemType.toUpperCase()) {
            case "PRODUCT" -> "SUBSCRIPTION";
            case "SERVICE" -> "SERVICE_INSTANCE";
            case "RESOURCE" -> "RESOURCE_INSTANCE";
            default -> "SUBSCRIPTION";
        };
    }

    /**
     * Get service orders by product order ID
     */
    @Transactional(readOnly = true)
    public List<ServiceOrder> getServiceOrdersByProductOrder(UUID productOrderId) {
        return serviceOrderRepository.findByProductOrderId(productOrderId);
    }

    /**
     * Update service order status and sync with order item
     */
    @Transactional
    public void updateServiceOrderStatus(UUID serviceOrderId, ServiceOrder.ServiceOrderStatus status) {
        ServiceOrder serviceOrder = serviceOrderRepository.findById(serviceOrderId)
            .orElseThrow(() -> new RuntimeException("Service order not found: " + serviceOrderId));
        
        serviceOrder.setStatus(status);
        serviceOrder.setUpdatedAt(Instant.now());
        
        if (status == ServiceOrder.ServiceOrderStatus.COMPLETED) {
            serviceOrder.setCompletedAt(Instant.now());
        }
        
        serviceOrderRepository.save(serviceOrder);
        
        // Update corresponding order item status
        List<OrderItem> items = orderItemRepository.findByOrderId(serviceOrder.getProductOrderId().toString());
        for (OrderItem item : items) {
            if (item.getStatus() == OrderItem.ItemStatus.PROCESSING) {
                item.setStatus(OrderItem.ItemStatus.COMPLETED);
                item.setCompletedAt(Instant.now());
                item.setUpdatedAt(Instant.now());
                orderItemRepository.save(item);
                break;
            }
        }
    }
}