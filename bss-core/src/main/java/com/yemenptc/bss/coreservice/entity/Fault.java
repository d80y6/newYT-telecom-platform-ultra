package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "faults")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Fault extends BaseTmfEntity {

    @Column(name = "fault_id", unique = true, length = 50)
    private String faultId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_severity", nullable = false, length = 20)
    private AlarmSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "fault_status", nullable = false, length = 20)
    private FaultStatus status = FaultStatus.ACTIVE;

    @Column(name = "alarm_type", nullable = false, length = 50)
    private String alarmType;

    @Column(name = "alarm_name", length = 200)
    private String alarmName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "resource_id", length = 50)
    private String resourceId;

    @Column(name = "resource_type", length = 50)
    private String resourceType;

    @Column(name = "resource_name", length = 200)
    private String resourceName;

    @Column(name = "ne_id", length = 100)
    private String neId;

    @Column(name = "location_id", length = 50)
    private String locationId;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "affected_services", length = 500)
    private String affectedServices;

    @Column(name = "correlation_id", length = 50)
    private String correlationId;

    @Column(name = "parent_alarm_id", length = 50)
    private String parentAlarmId;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "acknowledged_by", length = 100)
    private String acknowledgedBy;

    @Column(name = "cleared_at")
    private LocalDateTime clearedAt;

    @Column(name = "cleared_by", length = 100)
    private String clearedBy;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    public enum AlarmSeverity {
        CRITICAL, MAJOR, MINOR, WARNING, INDETERMINATE
    }

    public enum FaultStatus {
        ACTIVE, ACKNOWLEDGED, CLEARED, CLOSED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/faultManagement/v5/fault";
    }
}