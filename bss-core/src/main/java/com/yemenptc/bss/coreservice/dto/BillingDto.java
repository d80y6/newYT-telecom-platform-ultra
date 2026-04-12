package com.yemenptc.bss.coreservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BillingDto {
    private String accountId;
    private BigDecimal balance;
    private String currency;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AccountBalance {
    private String accountId;
    private BigDecimal mainBalance;
    private BigDecimal bonusBalance;
    private BigDecimal dataQuotaGB;
    private Integer voiceMinutes;
    private Integer smsCount;
    private LocalDate validityEndDate;
    private String currency;
    private Instant lastUpdated;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class BalanceAdjustmentRequest {
    private String accountId;
    private BigDecimal amount;
    private String adjustmentType;
    private String reason;
    private String referenceId;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class BalanceAdjustmentResult {
    private boolean success;
    private String transactionId;
    private BigDecimal newBalance;
    private String message;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class InvoiceDto {
    private String id;
    private String invoiceNumber;
    private String accountId;
    private String status;
    private BigDecimal subtotalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private Instant createdAt;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class InvoiceGenerationRequest {
    private String accountId;
    private String billingCycle;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class PaymentRequest {
    private String accountId;
    private String invoiceId;
    private BigDecimal amount;
    private String paymentMethod;
    private String referenceId;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class PaymentResult {
    private boolean success;
    private String paymentId;
    private String transactionId;
    private BigDecimal amount;
    private String message;
}
