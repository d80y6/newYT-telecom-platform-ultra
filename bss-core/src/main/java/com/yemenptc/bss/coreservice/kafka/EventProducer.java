package com.yemenptc.bss.coreservice.kafka;

import com.yemenptc.bss.coreservice.entity.Customer;
import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.entity.Payment;
import com.yemenptc.bss.coreservice.entity.UsageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String CUSTOMER_EVENTS_TOPIC = "customer.events";
    private static final String ORDER_EVENTS_TOPIC = "order.events";
    private static final String BILLING_EVENTS_TOPIC = "billing.events";
    private static final String PROVISIONING_EVENTS_TOPIC = "provisioning.events";

    public void publishCustomerCreated(Customer customer) {
        publishEvent(CUSTOMER_EVENTS_TOPIC, "customer.created", customer.getId().toString(), customer);
    }

    public void publishCustomerUpdated(Customer customer) {
        publishEvent(CUSTOMER_EVENTS_TOPIC, "customer.updated", customer.getId().toString(), customer);
    }

    public void publishCustomerDeleted(UUID customerId) {
        publishEvent(CUSTOMER_EVENTS_TOPIC, "customer.deleted", customerId.toString(), customerId.toString());
    }

    public void publishOrderCreated(Order order) {
        publishEvent(ORDER_EVENTS_TOPIC, "order.created", order.getId().toString(), order);
    }

    public void publishOrderStatusChanged(Order order) {
        publishEvent(ORDER_EVENTS_TOPIC, "order.statusChanged", order.getId().toString(), order);
    }

    public void publishOrderCompleted(Order order) {
        publishEvent(ORDER_EVENTS_TOPIC, "order.completed", order.getId().toString(), order);
    }

    public void publishPaymentReceived(Payment payment) {
        publishEvent(BILLING_EVENTS_TOPIC, "payment.received", payment.getId().toString(), payment);
    }

    public void publishPaymentProcessed(Payment payment) {
        publishEvent(BILLING_EVENTS_TOPIC, "payment.processed", payment.getId().toString(), payment);
    }

    public void publishUsageEvent(UsageEvent event) {
        publishEvent(BILLING_EVENTS_TOPIC, "usage.recorded", event.getId().toString(), event);
    }

    public void publishProvisioningStarted(UUID orderId, UUID serviceId) {
        publishEvent(PROVISIONING_EVENTS_TOPIC, "provisioning.started", orderId.toString(), 
            new ProvisioningEvent(orderId.toString(), serviceId.toString(), "STARTED"));
    }

    public void publishProvisioningCompleted(UUID orderId, UUID serviceId) {
        publishEvent(PROVISIONING_EVENTS_TOPIC, "provisioning.completed", orderId.toString(),
            new ProvisioningEvent(orderId.toString(), serviceId.toString(), "COMPLETED"));
    }

    public void publishProvisioningFailed(UUID orderId, UUID serviceId, String error) {
        publishEvent(PROVISIONING_EVENTS_TOPIC, "provisioning.failed", orderId.toString(),
            new ProvisioningEvent(orderId.toString(), serviceId.toString(), "FAILED", error));
    }

    private void publishEvent(String topic, String eventType, String key, Object payload) {
        EventWrapper event = new EventWrapper(eventType, payload);
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(topic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published event: {} to topic: {} with key: {}", 
                    eventType, topic, key);
            } else {
                log.error("Failed to publish event: {} to topic: {}", 
                    eventType, topic, ex);
            }
        });
    }

    public record EventWrapper(String eventType, Object payload) {}

    public record ProvisioningEvent(String orderId, String serviceId, String status, String errorMessage) {
        public ProvisioningEvent(String orderId, String serviceId, String status) {
            this(orderId, serviceId, status, null);
        }
    }
}
