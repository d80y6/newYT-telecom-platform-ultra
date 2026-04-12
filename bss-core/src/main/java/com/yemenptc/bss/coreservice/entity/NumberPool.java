package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "number_pool") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NumberPool {
    @Id @Column(length = 36) private String id;
    @Column(nullable = false, length = 20, unique = true) private String number;
    @Enumerated(EnumType.STRING) @Column(name = "number_type", nullable = false, length = 30) private NumberType numberType;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private NumberStatus status = NumberStatus.AVAILABLE;
    @Column(length = 50) private String exchange;
    @Column(name = "reservation_id", length = 36) private String reservationId;
    @Column(name = "reservation_expiry") private Instant reservationExpiry;
    @Column(name = "assigned_to", length = 36) private String assignedTo;
    @Column(name = "created_at") private Instant createdAt = Instant.now();
    @Column(name = "updated_at") private Instant updatedAt = Instant.now();
    public enum NumberType { MSISDN, FIXED_LINE, SHORT_CODE, TOLL_FREE }
    public enum NumberStatus { AVAILABLE, RESERVED, ASSIGNED, BLOCKED }
}
