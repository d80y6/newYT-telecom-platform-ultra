package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "self_service_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class SelfServiceAction extends BaseTmfEntity {

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private SelfServiceActionType actionType;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ActionStatus status = ActionStatus.PENDING;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "result_message", length = 500)
    private String resultMessage;

    @Column(name = "processed_by", length = 100)
    private String processedBy;

    public enum SelfServiceActionType {
        PLAN_CHANGE, SERVICE_UPGRADE, SERVICE_SUSPEND, SERVICE_RESTORE, 
        BILLING_ADDRESS_CHANGE, PAYMENT_METHOD_UPDATE, PASSWORD_CHANGE
    }

    public enum ActionStatus {
        PENDING, IN_PROGRESS, COMPLETED, REJECTED, FAILED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerPortal/v5/action";
    }
}
