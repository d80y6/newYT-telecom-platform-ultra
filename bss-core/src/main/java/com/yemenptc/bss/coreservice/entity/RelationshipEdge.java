package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "relationship_edges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class RelationshipEdge extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "source_node_id", length = 50)
    private String sourceNodeId;

    @Column(name = "target_node_id", length = 50)
    private String targetNodeId;

    @Column(name = "relationship_type")
    @Enumerated(EnumType.STRING)
    private RelationshipType relationshipType;

    public enum RelationshipType {
        FAMILY, BUSINESS, REFERRAL, GUARANTOR, SUBSIDIARY
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/relationship/edge";
    }
}