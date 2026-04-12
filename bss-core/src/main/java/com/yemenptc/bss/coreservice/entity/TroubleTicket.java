package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trouble_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TroubleTicket extends BaseTmfEntity {

    @Column(name = "ticket_number", unique = true, nullable = false, length = 50)
    private String ticketNumber;

    @Column(name = "party_id", columnDefinition = "uuid")
    private UUID partyId;

    @Column(name = "service_id", columnDefinition = "uuid")
    private UUID serviceId;

    @Column(name = "alarm_id", columnDefinition = "uuid")
    private UUID alarmId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.SUBMITTED;

    @Column(name = "ticket_type", nullable = false, length = 50)
    private String ticketType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "sla_deadline")
    private Instant slaDeadline;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Override
    protected String getApiPath() {
        return "/tmf-api/troubleTicketManagement/v5/troubleTicket";
    }

    public enum TicketSeverity { CRITICAL, MAJOR, MINOR, LOW }
    public enum TicketStatus { SUBMITTED, IN_PROGRESS, WAITING_CUSTOMER, RESOLVED, CLOSED, CANCELLED }
}
