package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "customer_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerPreferences extends BaseTmfEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "communication_channel", length = 50)
    private String communicationChannel;

    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "marketing_consent")
    private Boolean marketingConsent;

    @Column(name = "privacy_settings", columnDefinition = "TEXT")
    private String privacySettings;

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/preferences";
    }
}