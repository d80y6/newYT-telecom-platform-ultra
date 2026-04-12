package com.yemenptc.bss.coreservice.kafka;

import com.yemenptc.bss.coreservice.entity.Payment;
import com.yemenptc.bss.coreservice.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final BillingService billingService;

    @KafkaListener(topics = "billing.payment", groupId = "billing-service-group")
    public void consumePayment(Payment event) {
        billingService.processPayment(event.getId());
    }
}
