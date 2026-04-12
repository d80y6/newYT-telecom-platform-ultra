package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sla_contracts", indexes = {
    @Index(name = "idx_sla_customer", columnList = "customerId"),
    @Index(name = "idx_sla_status", columnList = "status"),
    @Index(name = "idx_sla_service", columnList = "serviceId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlaContract extends BaseTmfEntity {

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "sla_name", nullable = false, length = 200)
    private String slaName;

    @Column(name = "sla_type", length = 30)
    private String slaType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SlaStatus status;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "guaranteed_uptime", precision = 5, scale = 2)
    private Double guaranteedUptime;

    @Column(name = "response_time_minutes")
    private Integer responseTimeMinutes;

    @Column(name = "resolution_time_hours")
    private Integer resolutionTimeHours;

    @Column(name = "availability_target", precision = 5, scale = 2)
    private Double availabilityTarget;

    @Column(name = "latency_target_ms")
    private Integer latencyTargetMs;

    @Column(name = "throughput_target_mbps")
    private Integer throughputTargetMbps;

    @Column(name = "packet_loss_target")
    private Double packetLossTarget;

    @Column(name = "jitter_target_ms")
    private Integer jitterTargetMs;

    @OneToMany(mappedBy = "slaContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SlaThreshold> thresholds = new ArrayList<>();

    @OneToMany(mappedBy = "slaContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SlaViolation> violations = new ArrayList<>();

    @Column(name = "total_credits_applied", precision = 12, scale = 2)
    private Double totalCreditsApplied;

    @Column(name = "auto_renew")
    private Boolean autoRenew;

    @Column(name = "escalation_contact", length = 100)
    private String escalationContact;

    @Column(name = "linked_agreement_id", length = 50)
    private String linkedAgreementId;

    @Override
    protected String getApiPath() {
        return "/tmf-api/slaManagement/v5/slaContract";
    }

    public enum SlaStatus {
        DRAFT, ACTIVE, SUSPENDED, EXPIRED, TERMINATED, PENDING_RENEWAL
    }
}
