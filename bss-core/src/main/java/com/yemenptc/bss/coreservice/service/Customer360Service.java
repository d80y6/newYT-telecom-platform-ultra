package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.*;
import com.yemenptc.bss.coreservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class Customer360Service {

    private final Customer360Repository customer360Repository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ContactInformationRepository contactInformationRepository;
    private final AddressRepository addressRepository;
    private final UsageStatisticsRepository usageStatisticsRepository;
    private final BillingSummaryRepository billingSummaryRepository;
    private final CustomerPreferencesRepository customerPreferencesRepository;
    private final CustomerDashboardRepository customerDashboardRepository;
    private final ChurnRiskAssessmentRepository churnRiskAssessmentRepository;
    private final CreditProfileRepository creditProfileRepository;
    private final PaymentBehaviorRepository paymentBehaviorRepository;
    private final CustomerSegmentRepository customerSegmentRepository;
    private final RelationshipNodeRepository relationshipNodeRepository;
    private final RelationshipEdgeRepository relationshipEdgeRepository;

    @Transactional
    public Customer360 createCustomer360(UUID customerId) {
        log.info("Creating Customer360 for customer: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        // Create all sub-entities
        CustomerProfile profile = CustomerProfile.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .nationalId(customer.getNationalId())
                .languagePreference(customer.getPreferredLanguage() != null ? 
                        CustomerProfile.LanguagePreference.valueOf(customer.getPreferredLanguage().toUpperCase()) : 
                        CustomerProfile.LanguagePreference.EN)
                .build();
        profile = customerProfileRepository.save(profile);

        ContactInformation contactInfo = ContactInformation.builder()
                .primaryPhone(customer.getPrimaryPhone())
                .email(customer.getEmail())
                .preferredContactMethod(customer.getPreferredContactMethod() != null ? 
                        ContactInformation.PreferredContactMethod.valueOf(customer.getPreferredContactMethod()) : 
                        ContactInformation.PreferredContactMethod.PHONE)
                .build();
        contactInfo = contactInformationRepository.save(contactInfo);

        Address address = Address.builder()
                .street(customer.getStreet())
                .city(customer.getCity())
                .governorate(customer.getGovernorate())
                .postalCode(customer.getPostalCode())
                .country("YE")
                .build();
        address = addressRepository.save(address);

        UsageStatistics usageStats = UsageStatistics.builder()
                .period("CURRENT")
                .voiceMinutes(BigDecimal.ZERO)
                .dataUsageGB(BigDecimal.ZERO)
                .smsCount(0)
                .mmsCount(0)
                .internationalMinutes(BigDecimal.ZERO)
                .roamingDataGB(BigDecimal.ZERO)
                .build();
        usageStats = usageStatisticsRepository.save(usageStats);

        BillingSummary billingSummary = BillingSummary.builder()
                .currentBalance(BigDecimal.ZERO)
                .overdueAmount(BigDecimal.ZERO)
                .averageMonthlyBill(BigDecimal.ZERO)
                .build();
        billingSummary = billingSummaryRepository.save(billingSummary);

        CustomerPreferences preferences = CustomerPreferences.builder()
                .communicationChannel("OMNICHANNEL")
                .language(customer.getPreferredLanguage() != null ? customer.getPreferredLanguage() : "EN")
                .marketingConsent(customer.getMarketingConsent())
                .privacySettings("{}")
                .build();
        preferences = customerPreferencesRepository.save(preferences);

        CustomerDashboard dashboard = CustomerDashboard.builder()
                .customerId(customer.getExternalId())
                .summary(Map.of(
                        "activeServices", 0,
                        "totalAccounts", 0,
                        "currentBalance", BigDecimal.ZERO,
                        "monthlyUsage", Map.of(
                                "voiceMinutes", 0,
                                "dataGB", 0,
                                "smsCount", 0
                        )
                ))
                .alerts(List.of())
                .recommendations(List.of())
                .build();
        dashboard = customerDashboardRepository.save(dashboard);

        ChurnRiskAssessment churnRisk = ChurnRiskAssessment.builder()
                .customerId(customer.getExternalId())
                .churnRiskScore(BigDecimal.ZERO)
                .riskLevel(ChurnRiskAssessment.RiskLevel.LOW)
                .factors("[]")
                .predictionDate(LocalDateTime.now())
                .recommendedActions("[]")
                .build();
        churnRisk = churnRiskAssessmentRepository.save(churnRisk);

        Customer360 customer360 = Customer360.builder()
                .customer(customer)
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .profileCompleteScore(calculateProfileScore(customer))
                .engagementScore(0)
                .totalRevenue(BigDecimal.ZERO)
                .monthlyAvgRevenue(BigDecimal.ZERO)
                .priorityLevel("STANDARD")
                // TMF629 fields from customer
                .customerType(customer.getCustomerType() != null ? customer.getCustomerType().name() : null)
                .status(customer.getStatus() != null ? customer.getStatus().name() : null)
                .dateOfBirth(null) // Would need to be added to Customer entity
                .gender(null) // Would need to be added to Customer entity
                .nationality(customer.getNationalId() != null ? "YE" : null) // Simplified
                .occupation(null) // Would need to be added to Customer entity
                .employer(null) // Would need to be added to Customer entity
                .incomeLevel(null) // Would need to be added to Customer entity
                .languagePreference(customer.getPreferredLanguage())
                .preferredContactMethod(customer.getPreferredContactMethod())
                .marketingConsent(customer.getMarketingConsent())
                .creditScore(customer.getCreditScore())
                .creditLimit(customer.getCreditLimit())
                .profile(profile)
                .contactInformation(contactInfo)
                .address(address)
                .usageStatistics(usageStats)
                .billingSummary(billingSummary)
                .preferences(preferences)
                .dashboard(dashboard)
                .churnRiskAssessment(churnRisk)
                .build();

        Customer360 saved = customer360Repository.save(customer360);
        log.info("Customer360 created with ID: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Customer360 getCustomer360(UUID customerId) {
        return customer360Repository.findByCustomerId(customerId)
                .orElseGet(() -> createCustomer360(customerId));
    }

    @Transactional(readOnly = true)
    public Page<Customer360> listCustomers360(Pageable pageable) {
        return customer360Repository.findAll(pageable);
    }

    @Transactional
    public Customer360 updateEngagementScore(UUID customerId, Integer score) {
        Customer360 c360 = getCustomer360(customerId);
        c360.setEngagementScore(score);
        c360.setUpdatedAt(Instant.now());
        return customer360Repository.save(c360);
    }

    @Transactional
    public CustomerSegment assignSegment(UUID customerId, String segmentType, String segmentValue) {
        Customer360 c360 = getCustomer360(customerId);

        CustomerSegment segment = CustomerSegment.builder()
                .customer360(c360)
                .segmentType(segmentType)
                .segmentValue(segmentValue)
                .score(BigDecimal.valueOf(100))
                .confidence(BigDecimal.valueOf(85))
                .build();

        c360.getSegments().add(segment);
        customer360Repository.save(c360);

        return segment;
    }

    @Transactional
    public AccountHierarchy addAccountRelationship(UUID customerId, String relatedAccountId,
                                                   AccountHierarchy.RelationshipType relationshipType) {
        Customer360 c360 = getCustomer360(customerId);

        AccountHierarchy hierarchy = AccountHierarchy.builder()
                .customer360(c360)
                .relatedAccountId(relatedAccountId)
                .relationshipType(relationshipType)
                .hierarchyLevel(1)
                .billingResponsibility(false)
                .build();

        c360.getAccountHierarchy().add(hierarchy);
        customer360Repository.save(c360);

        return hierarchy;
    }

    @Transactional
    public CustomerRelationship addCustomerRelationship(UUID customerId, String relatedCustomerId,
                                                        String relationshipType) {
        Customer360 c360 = getCustomer360(customerId);

        CustomerRelationship relationship = CustomerRelationship.builder()
                .customer360(c360)
                .relatedCustomerId(relatedCustomerId)
                .relationshipType(relationshipType)
                .relationshipStartDate(LocalDateTime.now())
                .confidenceScore(90)
                .build();

        c360.getCustomerRelationships().add(relationship);
        customer360Repository.save(c360);

        return relationship;
    }

    @Transactional
    public Customer360 updateCreditProfile(UUID customerId, Integer creditScore,
                                           BigDecimal creditLimit, String creditStatus,
                                           String riskLevel) {
        Customer360 c360 = getCustomer360(customerId);
        c360.setCreditScore(creditScore);
        c360.setCreditLimit(creditLimit);
        c360.setAvailableCredit(creditLimit); // Simplified - would need to calculate based on usage
        c360.setCreditStatus(creditStatus);
        c360.setRiskLevel(riskLevel);
        c360.setLastCreditReviewDate(LocalDateTime.now());
        c360.setUpdatedAt(Instant.now());
        return customer360Repository.save(c360);
    }

    private BigDecimal calculateProfileScore(Customer customer) {
        int score = 0;
        if (customer.getNationalId() != null) score += 20;
        if (customer.getEmail() != null) score += 15;
        if (customer.getKycVerified()) score += 30;
        if (customer.getPreferredLanguage() != null) score += 10;
        if (customer.getPreferredContactMethod() != null) score += 10;
        if (customer.getMarketingConsent() != null) score += 15;
        return BigDecimal.valueOf(score);
    }
}