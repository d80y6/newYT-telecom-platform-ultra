package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agreements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agreement extends BaseTmfEntity {

    @Column(name = "agreement_number", unique = true, length = 50)
    private String agreementNumber;

    @Column(name = "agreement_type", length = 50)
    private String agreementType;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private AgreementStatus status = AgreementStatus.DRAFT;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "contract_value", length = 50)
    private String contractValue;

    @Column(name = "terms", columnDefinition = "TEXT")
    private String terms;

    public enum AgreementStatus { DRAFT, ACTIVE, SUSPENDED, TERMINATED, EXPIRED }

    @Override
    protected String getApiPath() {
        return "/tmf-api/agreementManagement/v5/agreement";
    }
}