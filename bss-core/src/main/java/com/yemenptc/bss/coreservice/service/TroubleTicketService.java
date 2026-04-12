package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.TroubleTicket;
import com.yemenptc.bss.coreservice.repository.TroubleTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TroubleTicketService {

    private final TroubleTicketRepository ticketRepository;

    @Transactional
    public TroubleTicket createTicket(TroubleTicket request) {
        TroubleTicket ticket = TroubleTicket.builder()
            .ticketNumber("TT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .partyId(request.getPartyId())
            .serviceId(request.getServiceId())
            .alarmId(request.getAlarmId())
            .severity(request.getSeverity())
            .status(TroubleTicket.TicketStatus.SUBMITTED)
            .ticketType(request.getTicketType())
            .description(request.getDescription())
            .assignedTo(request.getAssignedTo())
            .slaDeadline(request.getSlaDeadline())
            .build();
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public TroubleTicket getTicket(UUID id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<TroubleTicket> listTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<TroubleTicket> getTicketsByParty(UUID partyId) {
        return ticketRepository.findByPartyId(partyId);
    }

    @Transactional(readOnly = true)
    public List<TroubleTicket> getTicketsByStatus(TroubleTicket.TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    @Transactional
    public TroubleTicket updateTicket(UUID id, TroubleTicket request) {
        TroubleTicket existing = getTicket(id);
        TroubleTicket updated = TroubleTicket.builder()
            .ticketNumber(existing.getTicketNumber())
            .partyId(request.getPartyId() != null ? request.getPartyId() : existing.getPartyId())
            .serviceId(request.getServiceId() != null ? request.getServiceId() : existing.getServiceId())
            .alarmId(request.getAlarmId() != null ? request.getAlarmId() : existing.getAlarmId())
            .severity(request.getSeverity() != null ? request.getSeverity() : existing.getSeverity())
            .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
            .ticketType(request.getTicketType() != null ? request.getTicketType() : existing.getTicketType())
            .description(request.getDescription() != null ? request.getDescription() : existing.getDescription())
            .assignedTo(request.getAssignedTo() != null ? request.getAssignedTo() : existing.getAssignedTo())
            .resolutionNotes(request.getResolutionNotes() != null ? request.getResolutionNotes() : existing.getResolutionNotes())
            .slaDeadline(request.getSlaDeadline() != null ? request.getSlaDeadline() : existing.getSlaDeadline())
            .resolvedAt(existing.getResolvedAt())
            .build();
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(Instant.now());
        updated.setVersion(existing.getVersion() + 1);
        return ticketRepository.save(updated);
    }

    @Transactional
    public TroubleTicket closeTicket(UUID id, String resolution) {
        TroubleTicket ticket = getTicket(id);
        ticket.setStatus(TroubleTicket.TicketStatus.CLOSED);
        ticket.setResolutionNotes(resolution);
        ticket.setResolvedAt(Instant.now());
        ticket.setUpdatedAt(Instant.now());
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(UUID id) {
        ticketRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<TroubleTicket> findByStatus(TroubleTicket.TicketStatus status, Pageable pageable) {
        return ticketRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TroubleTicket> findBySeverity(TroubleTicket.TicketSeverity severity, Pageable pageable) {
        return ticketRepository.findBySeverity(severity, pageable);
    }

    @Transactional
    public TroubleTicket assignTicket(UUID id, String assignedTo) {
        TroubleTicket ticket = getTicket(id);
        ticket.setAssignedTo(assignedTo);
        if (ticket.getStatus() == TroubleTicket.TicketStatus.SUBMITTED) {
            ticket.setStatus(TroubleTicket.TicketStatus.IN_PROGRESS);
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public TroubleTicket updateStatus(UUID id, TroubleTicket.TicketStatus newStatus) {
        TroubleTicket ticket = getTicket(id);
        ticket.setStatus(newStatus);
        if (newStatus == TroubleTicket.TicketStatus.RESOLVED) {
            ticket.setResolvedAt(Instant.now());
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public TroubleTicket addResolutionNotes(UUID id, String notes) {
        TroubleTicket ticket = getTicket(id);
        ticket.setResolutionNotes(notes);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public TroubleTicket escalateTicket(UUID id) {
        TroubleTicket ticket = getTicket(id);
        switch (ticket.getSeverity()) {
            case LOW -> ticket.setSeverity(TroubleTicket.TicketSeverity.MINOR);
            case MINOR -> ticket.setSeverity(TroubleTicket.TicketSeverity.MAJOR);
            case MAJOR -> ticket.setSeverity(TroubleTicket.TicketSeverity.CRITICAL);
            default -> {}
        }
        return ticketRepository.save(ticket);
    }
}
