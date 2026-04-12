package com.yemenptc.bss.coreservice.common.event;

import lombok.*;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerEvent {
    private CustomerEventType eventType;
    private String customerId;
    private String externalId;
    private String nationalId;
    private String phone;
    private Instant timestamp;
}
