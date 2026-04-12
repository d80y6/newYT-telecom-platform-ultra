package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PaymentHistoryItem extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_summary_id", nullable = false)
    private BillingSummary billingSummary;

    @Column(name = "payment_date")
    private LocalDateTime date;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 50)
    private String method;

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/paymentHistory";
    }
}