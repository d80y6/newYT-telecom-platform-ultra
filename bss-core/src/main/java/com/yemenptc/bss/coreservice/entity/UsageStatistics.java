package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usage_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class UsageStatistics extends BaseTmfEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "period", length = 20)
    private String period;

    @Column(name = "voice_minutes")
    private BigDecimal voiceMinutes;

    @Column(name = "data_usage_gb")
    private BigDecimal dataUsageGB;

    @Column(name = "sms_count")
    private Integer smsCount;

    @Column(name = "mms_count")
    private Integer mmsCount;

    @Column(name = "international_minutes")
    private BigDecimal internationalMinutes;

    @Column(name = "roaming_data_gb")
    private BigDecimal roamingDataGB;

    @OneToMany(mappedBy = "usageStatistics", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UsageTrend> trends = new ArrayList<>();

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/usageStats";
    }
}