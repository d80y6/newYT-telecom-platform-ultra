package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder extends BaseTmfEntity {

    @Column(name = "work_order_number", unique = true, nullable = false, length = 50)
    private String workOrderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_order_id")
    private ResourceOrder resourceOrder;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkOrderStatus status = WorkOrderStatus.PENDING;

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    public enum WorkOrderStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/resourceOrderingManagement/v4/workOrder";
    }
}
