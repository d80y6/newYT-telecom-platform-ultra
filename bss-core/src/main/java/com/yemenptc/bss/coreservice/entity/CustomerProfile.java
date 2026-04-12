package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "customer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerProfile extends BaseTmfEntity {

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "national_id", length = 50)
    private String nationalId;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "employer", length = 100)
    private String employer;

    @Column(name = "income_level")
    @Enumerated(EnumType.STRING)
    private IncomeLevel incomeLevel;

    @Column(name = "language_preference")
    @Enumerated(EnumType.STRING)
    private LanguagePreference languagePreference;

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum IncomeLevel {
        LOW, MEDIUM, HIGH
    }

    public enum LanguagePreference {
        AR, EN
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/profile";
    }
}