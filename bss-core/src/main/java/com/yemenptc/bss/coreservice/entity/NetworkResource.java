package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "resources", indexes = {
    @Index(name = "idx_resource_type", columnList = "resourceType"),
    @Index(name = "idx_resource_status", columnList = "resourceStatus"),
    @Index(name = "idx_resource_location", columnList = "siteId, rackId, slotId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkResource extends BaseTmfEntity {

    @Column(name = "resource_identifier", unique = true, nullable = false, length = 100)
    private String resourceIdentifier;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_status", nullable = false, length = 20)
    private ResourceStatus resourceStatus = ResourceStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ResourceCategory category;

    @Column(name = "site_id", length = 50)
    private String siteId;

    @Column(name = "rack_id", length = 50)
    private String rackId;

    @Column(name = "slot_id", length = 50)
    private String slotId;

    @Column(name = "port_id", length = 50)
    private String portId;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "capacity_mbps")
    private Integer capacityMbps;

    @Column(name = "utilization_percent")
    private Integer utilizationPercent;

    @Column(name = "parent_resource_id", columnDefinition = "uuid")
    private UUID parentResourceId;

    @Override
    protected String getApiPath() {
        return "/tmf-api/resourceInventoryManagement/v4/resource";
    }

    public enum ResourceStatus { AVAILABLE, RESERVED, ALLOCATED, FAULTY, DECOMMISSIONED, INVENTORY, DEPLOYED, MAINTENANCE }
    public enum ResourceCategory { PHYSICAL, LOGICAL, VIRTUAL }
}
