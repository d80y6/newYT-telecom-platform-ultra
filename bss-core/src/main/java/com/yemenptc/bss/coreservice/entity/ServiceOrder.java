package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrder extends BaseTmfEntity {

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "party_id", columnDefinition = "uuid")
    private UUID partyId;

    @Column(name = "product_order_id", columnDefinition = "uuid")
    private UUID productOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceOrderStatus status = ServiceOrderStatus.ACKNOWLEDGED;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 30)
    private ServiceOrderType orderType;

    @Column(name = "cfs_type", length = 50)
    private String cfsType;

    @Column(name = "rfs_type", length = 50)
    private String rfsType;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Override
    protected String getApiPath() {
        return "/tmf-api/serviceOrderingManagement/v4/serviceOrder";
    }

    public enum ServiceOrderStatus { ACKNOWLEDGED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED }
    public enum ServiceOrderType { ACTIVATION, DEACTIVATION, MODIFICATION, SUSPENSION }
}
