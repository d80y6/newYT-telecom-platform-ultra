package com.yemenptc.bss.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "product_prices") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductPrice {
    @Id @Column(length = 36) private String id;
    @Column(name = "product_offering_id", nullable = false, length = 36) private String productOfferingId;
    @Column(nullable = false, length = 100) private String name;
    @Enumerated(EnumType.STRING) @Column(name = "price_type", nullable = false, length = 20) private PriceType priceType;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal price;
    @Column(length = 10) private String currency = "YER";
    @Column(name = "valid_from") private Instant validFrom;
    @Column(name = "valid_to") private Instant validTo;
    @Column(name = "created_at") private Instant createdAt = Instant.now();
    public enum PriceType { ONE_TIME, RECURRING, USAGE }
}
