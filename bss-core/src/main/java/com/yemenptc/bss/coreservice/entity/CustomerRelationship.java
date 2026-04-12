package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_relationships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerRelationship extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "related_customer_id")
    private String relatedCustomerId;

    @Column(name = "related_customer_name")
    private String relatedCustomerName;

    @Column(name = "relationship_type")
    private String relationshipType;

    @Column(name = "relationship_start_date")
    private LocalDateTime relationshipStartDate;

    @Column(name = "relationship_end_date")
    private LocalDateTime relationshipEndDate;

    @Column(name = "relationship_description")
    private String relationshipDescription;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customerRelationship";
    }
}