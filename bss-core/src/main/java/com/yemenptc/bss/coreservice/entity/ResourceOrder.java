package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resource_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ResourceOrder extends BaseTmfEntity {

    @Column(name = "external_id", unique = true, length = 50)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 20)
    private ResourceOrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResourceOrderState state = ResourceOrderState.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private OrderPriority priority = OrderPriority.NORMAL;

    @Column(name = "requested_start_date")
    private LocalDateTime requestedStartDate;

    @Column(name = "requested_completion_date")
    private LocalDateTime requestedCompletionDate;

    @Column(name = "expected_completion_date")
    private LocalDateTime expectedCompletionDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "service_account_id", length = 50)
    private String serviceAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", length = 30)
    private ResourceType resourceType;

    @Column(name = "resource_quantity")
    private Integer resourceQuantity;

    @Column(name = "location_id", length = 50)
    private String locationId;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "installation_address", length = 500)
    private String installationAddress;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "resourceOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ResourceOrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "resourceOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WorkOrder> workOrders = new ArrayList<>();

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // TMF640 Extended Fields
    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "related_party", length = 500)
    private String relatedParty;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "state_history", columnDefinition = "TEXT")
    private String stateHistory;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @Column(name = "has_external_reference")
    private Boolean hasExternalReference;

    @Column(name = "external_reference_id", length = 100)
    private String externalReferenceId;

    // TMF648 Extended Fields for Usage Management
    @Column(name = "product_offering_id", length = 50)
    private String productOfferingId;

    @Column(name = "usage_context", length = 100)
    private String usageContext;

    @Column(name = "billing_cycle", length = 30)
    private String billingCycle;

    public enum ResourceOrderType {
        PROVISION, MODIFY, TERMINATE, SWAP, REPAIR, AUDIT, TRANSFER
    }

    public enum ResourceOrderState {
        PENDING, ACKNOWLEDGED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED, FAILED
    }

    public enum OrderPriority {
        CRITICAL, HIGH, NORMAL, LOW
    }

    public enum ResourceType {
        SIM_CARD, PHONE_NUMBER, MODEM, SET_TOP_BOX, FIBER_LINE, IP_ADDRESS, VLAN, OLT_PORT, PON_PORT, EQUIPMENT, ANTENNA
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/resourceOrderingManagement/v4/resourceOrder";
    }
}