package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseTmfEntity {

    @Column(name = "notification_id", unique = true, nullable = false, length = 100)
    private String notificationId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "callback_url", nullable = false, length = 500)
    private String callbackUrl;

    @Column(name = "subscription_id", columnDefinition = "uuid")
    private UUID subscriptionId;

    @Column(name = "listener_type", length = 50)
    private String listenerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Override
    protected String getApiPath() {
        return "/tmf-api/notificationListener/v5/notification";
    }

    public enum DeliveryStatus { PENDING, DELIVERED, FAILED, RETRYING }
}
