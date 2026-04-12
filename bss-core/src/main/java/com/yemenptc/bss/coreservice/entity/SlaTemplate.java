package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sla_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlaTemplate extends BaseTmfEntity {

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Column(name = "template_type", length = 50)
    private String templateType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "service_level", length = 50)
    private String serviceLevel;

    @Column(name = "availability_target", precision = 5, scale = 2)
    private BigDecimal availabilityTarget;

    @Column(name = "response_time_sla", length = 50)
    private String responseTimeSla;

    @Column(name = "resolution_time_sla", length = 50)
    private String resolutionTimeSla;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private SlaStatus status = SlaStatus.DRAFT;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "category", length = 50)
    private String category;

    @Override
    protected String getApiPath() {
        return "/tmf-api/slaManagement/v5/slaTemplate";
    }

    public enum SlaStatus { DRAFT, ACTIVE, DEPRECATED, ARCHIVED }
}