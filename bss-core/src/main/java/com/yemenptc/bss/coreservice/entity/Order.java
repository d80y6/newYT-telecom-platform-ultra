package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseTmfEntity {

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false, columnDefinition = "uuid")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 30)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.ACKNOWLEDGED;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderPriority priority = OrderPriority.MEDIUM;

    @Column(length = 30)
    private String channel;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Override
    protected String getApiPath() {
        return "/tmf-api/productOrderingManagement/v5/productOrder";
    }

    public enum OrderType { ACQUISITION, MODIFICATION, TERMINATION, SUSPENSION, TRANSFER }
    public enum OrderStatus { ACKNOWLEDGED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED }
    public enum OrderPriority { LOW, MEDIUM, HIGH, CRITICAL }
}
