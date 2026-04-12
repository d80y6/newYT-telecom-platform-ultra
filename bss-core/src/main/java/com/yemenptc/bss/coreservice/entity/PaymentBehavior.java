package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_behaviors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PaymentBehavior extends BaseTmfEntity {

    @Column(name = "on_time_payment_rate", precision = 5, scale = 2)
    private BigDecimal onTimePaymentRate;

    @Column(name = "late_payment_count")
    private Integer latePaymentCount;

    @Column(name = "default_count")
    private Integer defaultCount;

    @Column(name = "days_past_due")
    private Integer daysPastDue;

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/paymentBehavior";
    }
}