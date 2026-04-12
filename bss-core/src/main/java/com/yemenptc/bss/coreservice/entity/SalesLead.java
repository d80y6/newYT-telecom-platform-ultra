package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sales_leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesLead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "lead_id", unique = true, nullable = false, length = 50)
    private String leadId;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 200)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(name = "potential_value", precision = 15, scale = 2)
    private BigDecimal potentialValue;

    @Column(length = 10)
    @Builder.Default
    private String currency = "YER";

    @Column(name = "source", length = 100)
    private String source;

    @Column(name = "assigned_to", columnDefinition = "uuid")
    private UUID assignedTo;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "converted_at")
    private Instant convertedAt;

    public enum LeadStatus { NEW, CONTACTED, QUALIFIED, PROPOSAL, NEGOTIATION, CONVERTED, LOST }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (leadId == null) leadId = "LEAD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
