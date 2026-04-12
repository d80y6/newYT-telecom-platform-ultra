package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "party")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Party extends BaseTmfEntity {

    @Column(name = "party_id", nullable = false, unique = true, length = 50)
    private String partyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", length = 20)
    private PartyType partyType;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "trading_name", length = 200)
    private String tradingName;

    @Column(name = "birth_date")
    private Instant birthDate;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Column(name = "preferred_contact_method", length = 20)
    private String preferredContactMethod;

    @Column(name = "marketing_consent")
    private Boolean marketingConsent;

    @Column(name = "data_processing_consent")
    private Boolean dataProcessingConsent;

    public enum PartyType {
        INDIVIDUAL, ORGANIZATION
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/partyManagement/v5/party";
    }
}
