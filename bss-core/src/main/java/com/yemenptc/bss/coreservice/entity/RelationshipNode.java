package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "relationship_nodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class RelationshipNode extends BaseTmfEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_360_id", nullable = false)
    private Customer360 customer360;

    @Column(name = "node_id", length = 50)
    private String nodeId;

    @Column(name = "node_type", length = 50)
    private String nodeType;

    @Column(name = "node_name", length = 100)
    private String nodeName;

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerManagement/v4/customer/relationship/node";
    }
}