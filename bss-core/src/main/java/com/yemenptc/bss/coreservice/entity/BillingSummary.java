package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billing_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class BillingSummary extends BaseTmfEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "current_balance", precision = 15, scale = 2)
    private BigDecimal currentBalance;

    @Column(name = "overdue_amount", precision = 15, scale = 2)
    private BigDecimal overdueAmount;

    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate;

    @Column(name = "last_payment_amount", precision = 15, scale = 2)
    private BigDecimal lastPaymentAmount;

    @Column(name = "average_monthly_bill", precision = 15, scale = 2)
    private BigDecimal averageMonthlyBill;

    @OneToMany(mappedBy = "billingSummary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PaymentHistoryItem> paymentHistory = new ArrayList<>();

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/billingSummary";
    }
}