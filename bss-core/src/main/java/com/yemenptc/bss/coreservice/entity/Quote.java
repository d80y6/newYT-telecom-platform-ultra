package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "quotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "quote_id", unique = true, nullable = false, length = 50)
    private String quoteId;

    @Column(name = "customer_id", columnDefinition = "uuid")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private QuoteStatus status = QuoteStatus.DRAFT;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 10)
    @Builder.Default
    private String currency = "YER";

    @Column(name = "valid_until")
    private Instant validUntil;

    @Column(name = "created_at")
    private Instant createdAt;

    public enum QuoteStatus { DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (quoteId == null) quoteId = "QTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
