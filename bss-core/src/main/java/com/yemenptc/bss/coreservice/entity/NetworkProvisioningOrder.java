package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "network_provisioning_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class NetworkProvisioningOrder extends BaseTmfEntity {

    @Column(name = "order_id", unique = true, length = 50)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 30)
    private ProvisionOrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProvisionOrderStatus status = ProvisionOrderStatus.PENDING;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "product_id", length = 50)
    private String productId;

    @Column(name = "location_id", length = 50)
    private String locationId;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "installation_date")
    private LocalDateTime installationDate;

    @Column(name = "technician_id", length = 50)
    private String technicianId;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(name = "appointment_slot", length = 50)
    private String appointmentSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private ProvisionPriority priority = ProvisionPriority.NORMAL;

    @Column(name = "equipment_required", length = 500)
    private String equipmentRequired;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public enum ProvisionOrderType {
        NEW_INSTALLATION, TRANSFER, UPGRADE, DOWNGRADE, SUSPEND, RESTORE, TERMINATE
    }

    public enum ProvisionOrderStatus {
        PENDING, SCHEDULED, ASSIGNED, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    }

    public enum ProvisionPriority {
        CRITICAL, HIGH, NORMAL, LOW
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/serviceProvisioningManagement/v5/provisioningOrder";
    }
}