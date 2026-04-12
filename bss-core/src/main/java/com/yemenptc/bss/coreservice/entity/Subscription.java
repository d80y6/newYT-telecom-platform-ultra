package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "subscription_number", unique = true, nullable = false, length = 50)
    private String subscriptionNumber;

    @Column(name = "customer_id", nullable = false, length = 36)
    private String customerId;

    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @Column(name = "service_type", nullable = false, length = 30)
    private String serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(name = "product_offering_id", length = 36)
    private String productOfferingId;

    @Column(name = "service_identifier", length = 100)
    private String serviceIdentifier;

    @Column(name = "plan_name", length = 200)
    private String planName;

    @Column(name = "monthly_fee", precision = 15, scale = 2)
    private BigDecimal monthlyFee;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Integer version = 1;

    public enum SubscriptionStatus { ACTIVE, SUSPENDED, TERMINATED, PENDING }
}
