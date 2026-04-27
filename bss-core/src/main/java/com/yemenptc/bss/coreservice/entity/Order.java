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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<OrderItem> orderItems = new java.util.ArrayList<>();

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "primary_phone")
    private String primaryPhone;

    @Override
    protected String getApiPath() {
        return "/tmf-api/productOrderingManagement/v5/productOrder";
    }

    public enum OrderType { ACQUISITION, MODIFICATION, TERMINATION, SUSPENSION, TRANSFER }
    public enum OrderStatus { ACKNOWLEDGED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED }
    public enum OrderPriority { LOW, MEDIUM, HIGH, CRITICAL }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepResult {
        private String stepId;
        private String stepName;
        private StepStatus status;
        private String errorCode;
        private String errorMessage;
        private Instant startedAt;
        private Instant completedAt;

        public enum StepStatus {
            PENDING, IN_PROGRESS, COMPLETED, FAILED, COMPENSATED, SKIPPED
        }

        public static StepResult success(String stepId, String stepName) {
            return StepResult.builder()
                    .stepId(stepId)
                    .stepName(stepName)
                    .status(StepStatus.COMPLETED)
                    .startedAt(Instant.now())
                    .completedAt(Instant.now())
                    .build();
        }

        public static StepResult failure(String stepId, String errorMessage) {
            return StepResult.builder()
                    .stepId(stepId)
                    .status(StepStatus.FAILED)
                    .errorMessage(errorMessage)
                    .startedAt(Instant.now())
                    .completedAt(Instant.now())
                    .build();
        }

        public boolean isSuccess() {
            return status == StepStatus.COMPLETED;
        }

        public String getMessage() {
            return isSuccess() ? "Success" : errorMessage;
        }
    }
}
