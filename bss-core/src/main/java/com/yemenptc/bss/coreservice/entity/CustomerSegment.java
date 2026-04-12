package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_segments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerSegment extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "segment_id", length = 50)
    private String segmentId;

    @Column(name = "segment_name", length = 100)
    private String segmentName;

    @Column(name = "segment_type")
    @Enumerated(EnumType.STRING)
    private SegmentType segmentType;

    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    public enum SegmentType {
        VALUE, BEHAVIOR, DEMOGRAPHIC, LIFECYCLE, CHURN_RISK
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/segment";
    }
}