package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import com.yemenptc.bss.sdk.entity.TmfCharacteristics;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_360")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Customer360 extends BaseTmfEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private CustomerProfile profile;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_info_id")
    private ContactInformation contactInformation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "usage_stats_id")
    private UsageStatistics usageStatistics;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_summary_id")
    private BillingSummary billingSummary;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "preferences_id")
    private CustomerPreferences preferences;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "dashboard_id")
    private CustomerDashboard dashboard;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "churn_risk_id")
    private ChurnRiskAssessment churnRiskAssessment;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_profile_id")
    private CreditProfile creditProfile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "profile_complete_score", precision = 5, scale = 2)
    private BigDecimal profileCompleteScore;

    @Column(name = "engagement_score")
    private Integer engagementScore;

    @Column(name = "nps_score")
    private Integer npsScore;

    @Column(name = "last_interaction_date")
    private LocalDateTime lastInteractionDate;

    @Column(name = "total_revenue", precision = 15, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "monthly_avg_revenue", precision = 15, scale = 2)
    private BigDecimal monthlyAvgRevenue;

    @Column(name = "contract_start_date")
    private LocalDateTime contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDateTime contractEndDate;

    @Column(name = "account_manager")
    private String accountManager;

    @Column(name = "priority_level")
    private String priorityLevel;

    // TMF629 Customer 360 View Fields
    @Column(name = "customer_type")
    private String customerType;

    @Column(name = "status")
    private String status;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "employer")
    private String employer;

    @Column(name = "income_level")
    private String incomeLevel;

    @Column(name = "language_preference")
    private String languagePreference;

    @Column(name = "preferred_contact_method")
    private String preferredContactMethod;

    @Column(name = "preferred_contact_time")
    private String preferredContactTime;

    @Column(name = "marketing_consent")
    private Boolean marketingConsent;

    @Column(name = "communication_preferences")
    private String communicationPreferences;

    @Column(name = "privacy_settings")
    private String privacySettings;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "available_credit", precision = 15, scale = 2)
    private BigDecimal availableCredit;

    @Column(name = "credit_status")
    private String creditStatus;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "last_credit_review_date")
    private LocalDateTime lastCreditReviewDate;

    @Column(name = "on_time_payment_rate")
    private BigDecimal onTimePaymentRate;

    @Column(name = "late_payment_count")
    private Integer latePaymentCount;

    @Column(name = "default_count")
    private Integer defaultCount;

    @Column(name = "days_past_due")
    private Integer daysPastDue;

    @ElementCollection
    @CollectionTable(name = "customer_segment_details", joinColumns = @JoinColumn(name = "customer_360_id"))
    @Column(name = "segment_detail")
    private List<String> segmentDetails = new ArrayList<>();

    @OneToMany(mappedBy = "customer360", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CustomerSegment> segments = new ArrayList<>();

    @OneToMany(mappedBy = "customer360", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AccountHierarchy> accountHierarchy = new ArrayList<>();

    @OneToMany(mappedBy = "customer360", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CustomerRelationship> customerRelationships = new ArrayList<>();

    @Column(name = "related_party_ids")
    private String relatedPartyIds;

    @Column(name = "preferred_products")
    private String preferredProducts;

    @Column(name = "usage_pattern")
    private String usagePattern;

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer";
    }
}