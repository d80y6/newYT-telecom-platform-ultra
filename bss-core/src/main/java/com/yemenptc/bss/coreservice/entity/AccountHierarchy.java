package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "account_hierarchy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AccountHierarchy extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id")
    private Customer360 customer360;

    @Column(name = "related_account_id", nullable = false, length = 50)
    private String relatedAccountId;

    @Column(name = "related_account_name", length = 100)
    private String relatedAccountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false, length = 30)
    private RelationshipType relationshipType;

    @Column(name = "hierarchy_level")
    private Integer hierarchyLevel;

    @Column(name = "billing_responsibility")
    private Boolean billingResponsibility;

    @Column(name = "credit_sharing", precision = 5, scale = 2)
    private BigDecimal creditSharing;

    public enum RelationshipType {
        PARENT, CHILD, SIBLING, GROUP_HEAD, SUBSIDIARY, AFFILIATE
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer";
    }
}