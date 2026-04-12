package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "network_elements") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NetworkElement {
    @Id @Column(length = 36) private String id;
    @Column(name = "element_name", nullable = false, length = 100) private String name;
    @Column(name = "element_type", nullable = false, length = 50) private String type;
    @Column(length = 50) private String vendor;
    @Column(length = 100) private String model;
    @Column(name = "ip_address", length = 50) private String ipAddress;
    @Enumerated(EnumType.STRING) @Column(length = 20) private ElementStatus status = ElementStatus.ACTIVE;
    @Column(length = 200) private String location;
    @Column(name = "site_id", length = 50) private String siteId;
    @Column(name = "created_at") private Instant createdAt = Instant.now();
    @Column(name = "updated_at") private Instant updatedAt = Instant.now();
    public enum ElementStatus { ACTIVE, INACTIVE, MAINTENANCE }
}
