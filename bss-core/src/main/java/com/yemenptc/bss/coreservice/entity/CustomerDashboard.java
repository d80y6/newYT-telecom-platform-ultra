package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "customer_dashboards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerDashboard extends BaseTmfEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "summary", columnDefinition = "JSON")
    private Map<String, Object> summary;

    @OneToMany(mappedBy = "customerDashboard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Object> alerts = new ArrayList<>();

    @OneToMany(mappedBy = "customerDashboard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<String> recommendations = new ArrayList<>();

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/dashboard";
    }
}