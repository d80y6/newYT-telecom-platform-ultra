package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "order_items") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {
    @Id @Column(length = 36) private String id;
    @Column(name = "order_id", nullable = false, length = 36) private String orderId;
    @Column(name = "item_type", nullable = false, length = 30) private String itemType;
    @Column(name = "product_offering_id", length = 36) private String productOfferingId;
    @Column(name = "subscription_id", length = 36) private String subscriptionId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private ItemStatus status = ItemStatus.PENDING;
    @Column(length = 30) private String action;
    @Column(name = "error_message") private String errorMessage;
    @Column(name = "created_at") private Instant createdAt = Instant.now();
    @Column(name = "updated_at") private Instant updatedAt = Instant.now();
    @Column(name = "completed_at") private Instant completedAt;
    public enum ItemStatus { PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED }
}
