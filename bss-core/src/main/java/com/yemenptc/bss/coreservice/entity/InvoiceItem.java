package com.yemenptc.bss.coreservice.entity;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class InvoiceItem {
    private String id;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private Instant createdAt;
}
