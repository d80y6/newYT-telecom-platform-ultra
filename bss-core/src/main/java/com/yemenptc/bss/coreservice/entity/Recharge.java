package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "recharges") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Recharge {
    @Id @Column(length = 36) private String id;
    @Column(name = "transaction_id", unique = true, nullable = false, length = 50) private String transactionId;
    @Column(name = "account_id", nullable = false, length = 36) private String accountId;
    @Column(length = 20) private String msisdn;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal amount;
    @Column(name = "bonus_amount", precision = 15, scale = 2) private BigDecimal bonusAmount = BigDecimal.ZERO;
    @Column(name = "previous_balance", precision = 15, scale = 2) private BigDecimal previousBalance;
    @Column(name = "new_balance", precision = 15, scale = 2) private BigDecimal newBalance;
    @Column(name = "payment_method", nullable = false, length = 30) private String paymentMethod;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private RechargeStatus status = RechargeStatus.PENDING;
    @Column(name = "created_at") private Instant createdAt = Instant.now();
    public enum RechargeStatus { PENDING, COMPLETED, FAILED }
}
