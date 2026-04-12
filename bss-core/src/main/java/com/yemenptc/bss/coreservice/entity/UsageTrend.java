package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Entity
@Table(name = "usage_trends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class UsageTrend extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usage_statistics_id", nullable = false)
    private UsageStatistics usageStatistics;

    @Column(name = "period", length = 20)
    private String period;

    @Column(name = "voice_minutes")
    private BigDecimal voiceMinutes;

    @Column(name = "data_usage_gb")
    private BigDecimal dataUsageGB;

    @Column(name = "sms_count")
    private Integer smsCount;

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/usageTrend";
    }
}