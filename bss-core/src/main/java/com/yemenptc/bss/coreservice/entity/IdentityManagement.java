package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "identity_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class IdentityManagement extends BaseTmfEntity {

    @Column(name = "identity_id", unique = true, nullable = false, length = 50)
    private String identityId;

    @Column(name = "party_id", columnDefinition = "uuid")
    private UUID partyId;

    @Column(name = "identity_type", nullable = false, length = 50)
    private String identityType;

    @Column(name = "identity_value", nullable = false, length = 200)
    private String identityValue;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private IdentityStatus status;

    @Column(name = "last_verified_at")
    private Instant lastVerifiedAt;

    public enum IdentityType {
        MSISDN, EMAIL, NATIONAL_ID, PASSPORT, IBAN, SIM_CARD
    }

    public enum IdentityStatus {
        PENDING, VERIFIED, SUSPENDED, EXPIRED, REVOKED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/identityManagement/v5/identity";
    }

    @PrePersist
    protected void onCreate() {
        if (identityId == null) {
            identityId = "IDM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (status == null) {
            status = IdentityStatus.PENDING;
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (isPrimary == null) {
            isPrimary = false;
        }
    }
}
