package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseTmfEntity {

    @Column(name = "task_number", unique = true, nullable = false, length = 50)
    private String taskNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/resourceOrderingManagement/v4/task";
    }
}
