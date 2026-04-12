package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "convergent_billing_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ConvergentBillingAccount extends BaseTmfEntity {

    @Column(name = "account_id", unique = true, nullable = false, length = 50)
    private String accountId;

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false, length = 20)
    private BillingType billingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "current_balance", precision = 15, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "available_credit", precision = 15, scale = 2)
    private BigDecimal availableCredit;

    @Column(name = "last_recharge_amount", precision = 15, scale = 2)
    private BigDecimal lastRechargeAmount;

    @Column(name = "last_recharge_date")
    private LocalDateTime lastRechargeDate;

    @Column(name = "billing_cycle_day")
    private Integer billingCycleDay;

    @Column(name = "billing_cycle_start")
    private LocalDateTime billingCycleStart;

    @Column(name = "billing_cycle_end")
    private LocalDateTime billingCycleEnd;

    @Column(name = "total_revenue_ytd", precision = 15, scale = 2)
    private BigDecimal totalRevenueYtd;

    @Column(name = "prepaid_balance", precision = 15, scale = 2)
    private BigDecimal prepaidBalance;

    @Column(name = "postpaid_balance", precision = 15, scale = 2)
    private BigDecimal postpaidBalance;

    public enum BillingType {
        PREPAID, POSTPAID, HYBRID
    }

    public enum AccountStatus {
        ACTIVE, SUSPENDED, TERMINATED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/convergentBilling/v5/account";
    }
}