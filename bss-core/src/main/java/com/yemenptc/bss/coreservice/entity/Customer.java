package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.TmfCharacteristics;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends TmfCharacteristics {

    @Column(name = "external_id", unique = true, length = 50)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 20)
    private CustomerType customerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @Column(name = "national_id", length = 50)
    private String nationalId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "primary_phone", nullable = false, length = 20)
    private String primaryPhone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String street;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String governorate;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 50)
    private String country = "YE";

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_level", length = 20)
    private KycLevel kycLevel = KycLevel.BASIC;

    @Column(name = "kyc_verified")
    private Boolean kycVerified = false;

    @Column(name = "churn_risk_score", precision = 5, scale = 4)
    private BigDecimal churnRiskScore;

    @Column(name = "lifetime_value", precision = 15, scale = 2)
    private BigDecimal lifetimeValue;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "total_accounts")
    private Integer totalAccounts = 0;

    @Column(name = "total_subscriptions")
    private Integer totalSubscriptions = 0;

    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    private BigDecimal outstandingBalance;

    @ElementCollection
    @CollectionTable(name = "customer_segments", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "segment_type")
    private List<String> segments;

    @Column(name = "preferred_language", length = 5)
    private String preferredLanguage = "ar";

    @Column(name = "preferred_currency", length = 3)
    private String preferredCurrency = "YER";

    @Column(name = "preferred_contact_method", length = 20)
    private String preferredContactMethod;

    @Column(name = "marketing_consent")
    private Boolean marketingConsent = false;

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v5/customer";
    }

    public enum CustomerType { RESIDENTIAL, BUSINESS, ENTERPRISE, GOVERNMENT }
    public enum CustomerStatus { ACTIVE, INACTIVE, SUSPENDED, TERMINATED }
    public enum KycLevel { BASIC, FULL, PREMIUM }
}
