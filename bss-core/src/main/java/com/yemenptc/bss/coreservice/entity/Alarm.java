package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alarms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alarm extends BaseTmfEntity {

    @Column(name = "alarm_id", unique = true, nullable = false, length = 50)
    private String alarmId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlarmSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AlarmStatus status = AlarmStatus.ACTIVE;

    @Column(length = 200)
    private String source;

    @Column(name = "resource_id", columnDefinition = "uuid")
    private UUID resourceId;

    @Column(name = "alarm_type", nullable = false, length = 50)
    private String alarmType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "cleared_at")
    private Instant clearedAt;

    @Override
    protected String getApiPath() {
        return "/tmf-api/alarmManagement/v5/alarm";
    }

    public enum AlarmSeverity { CRITICAL, MAJOR, MINOR, WARNING, INDETERMINATE }
    public enum AlarmStatus { ACTIVE, ACKNOWLEDGED, CLEARED, CLOSED }
}
