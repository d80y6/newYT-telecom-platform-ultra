package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ResourceOrderItem extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_order_id", nullable = false)
    private ResourceOrder resourceOrder;

    @Column(name = "item_reference", length = 50)
    private String itemReference;

    @Column(name = "action", length = 30)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", length = 30)
    private ResourceOrder.ResourceType resourceType;

    @Column(name = "resource_identifier", length = 100)
    private String resourceIdentifier;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "schedule_date")
    private LocalDateTime scheduleDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ItemStatus status;

    @Column(name = "result_message", length = 500)
    private String resultMessage;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

    public enum ItemStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/resourceOrderingManagement/v4/resourceOrder";
    }
}