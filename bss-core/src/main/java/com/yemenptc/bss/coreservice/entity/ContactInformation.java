package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "contact_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ContactInformation extends BaseTmfEntity {

    @Column(name = "primary_phone", length = 20)
    private String primaryPhone;

    @Column(name = "secondary_phone", length = 20)
    private String secondaryPhone;

    @Column(name = "email", length = 100)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "preferred_contact_method")
    @Enumerated(EnumType.STRING)
    private PreferredContactMethod preferredContactMethod;

    @Column(name = "preferred_contact_time", length = 20)
    private String preferredContactTime;

    public enum PreferredContactMethod {
        PHONE, EMAIL, SMS, WHATSAPP
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/contact";
    }
}