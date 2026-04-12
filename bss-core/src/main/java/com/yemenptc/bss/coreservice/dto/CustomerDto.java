package com.yemenptc.bss.coreservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerDto {
    private String id;
    private String externalId;
    private String customerType;
    private String status;
    private String nationalId;
    private String firstName;
    private String lastName;
    private String primaryPhone;
    private String email;
    private String city;
    private String governorate;
    private String kycLevel;
    private Boolean kycVerified;
    private BigDecimal churnRiskScore;
    private Instant createdAt;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class CustomerCreateRequest {
    private String customerType;
    private String nationalId;
    private String firstName;
    private String lastName;
    private String primaryPhone;
    private String secondaryPhone;
    private String email;
    private String street;
    private String city;
    private String governorate;
    private String postalCode;
    private LocalDate dateOfBirth;
    private String gender;
    private String languagePreference;
    private String preferredContactMethod;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class CustomerUpdateRequest {
    private String primaryPhone;
    private String secondaryPhone;
    private String email;
    private String street;
    private String city;
    private String governorate;
    private String postalCode;
    private String preferredContactMethod;
    private String languagePreference;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class CustomerSearchRequest {
    private String query;
    private String status;
    private String customerType;
    private String governorate;
    private int page = 0;
    private int size = 20;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class CustomerBalance {
    private String customerId;
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
class RechargeRequest {
    private String customerId;
    private String accountId;
    private String msisdn;
    private BigDecimal amount;
    private String paymentMethod;
    private String voucherCode;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class RechargeResult {
    private boolean success;
    private String transactionId;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private BigDecimal bonusAmount;
    private String message;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class KycVerificationRequest {
    private String documentType;
    private String documentNumber;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class KycVerificationResult {
    private boolean verified;
    private String level;
    private Instant verifiedAt;
    private String message;
}
