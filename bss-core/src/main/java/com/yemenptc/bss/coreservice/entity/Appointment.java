package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseTmfEntity {

    @Column(name = "appointment_id", unique = true, nullable = false, length = 50)
    private String appointmentId;

    @Column(name = "customer_id", columnDefinition = "uuid")
    private UUID customerId;

    @Column(name = "appointment_type", length = 100)
    private String appointmentType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "technician_id", columnDefinition = "uuid")
    private UUID technicianId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_at")
    private Instant completedAt;

    public enum AppointmentStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/appointmentManagement/v5/appointment";
    }

    @PrePersist
    protected void onCreate() {
        if (appointmentId == null) {
            appointmentId = "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (status == null) {
            status = AppointmentStatus.SCHEDULED;
        }
    }
}
