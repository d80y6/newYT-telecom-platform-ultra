package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.TroubleTicket;
import com.yemenptc.bss.coreservice.service.TroubleTicketService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/troubleTicketManagement/v5/troubleTicket")
@RequiredArgsConstructor
public class TroubleTicketController {

    private final TroubleTicketService ticketService;

    @PostMapping
    public ResponseEntity<TmfResponse<TroubleTicket>> create(@RequestBody TroubleTicket request) {
        TroubleTicket ticket = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(ticket, "TroubleTicket"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TmfResponse<TroubleTicket>> getById(@PathVariable UUID id) {
        TroubleTicket ticket = ticketService.getTicket(id);
        return ResponseEntity.ok(TmfResponse.success(ticket, "TroubleTicket"));
    }

    @GetMapping
    public ResponseEntity<TmfResponse<TroubleTicket>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<TroubleTicket> result;
        if (status != null) {
            result = ticketService.findByStatus(TroubleTicket.TicketStatus.valueOf(status), PageRequest.of(page, size));
        } else if (severity != null) {
            result = ticketService.findBySeverity(TroubleTicket.TicketSeverity.valueOf(severity), PageRequest.of(page, size));
        } else {
            result = ticketService.listTickets(PageRequest.of(page, size));
        }
        
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "TroubleTicket"));
    }

    @GetMapping("/party/{partyId}")
    public ResponseEntity<TmfResponse<TroubleTicket>> getByParty(@PathVariable UUID partyId) {
        List<TroubleTicket> tickets = ticketService.getTicketsByParty(partyId);
        return ResponseEntity.ok(TmfResponse.list(
                tickets, tickets.size(), 0, tickets.size(), "TroubleTicket"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TmfResponse<TroubleTicket>> update(
            @PathVariable UUID id, @RequestBody TroubleTicket request) {
        TroubleTicket ticket = ticketService.updateTicket(id, request);
        return ResponseEntity.ok(TmfResponse.success(ticket, "TroubleTicket"));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TmfResponse<TroubleTicket>> assign(
            @PathVariable UUID id, @RequestBody Map<String, String> request) {
        String assignedTo = request.get("assignedTo");
        TroubleTicket ticket = ticketService.assignTicket(id, assignedTo);
        return ResponseEntity.ok(TmfResponse.success(ticket, "TroubleTicket"));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<TmfResponse<TroubleTicket>> updateStatus(
            @PathVariable UUID id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        TroubleTicket ticket = ticketService.updateStatus(id, TroubleTicket.TicketStatus.valueOf(status));
        return ResponseEntity.ok(TmfResponse.success(ticket, "TroubleTicket"));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<TmfResponse<TroubleTicket>> resolve(
            @PathVariable UUID id, @RequestBody Map<String, String> request) {
        String notes = request.get("resolutionNotes");
        TroubleTicket ticket = ticketService.addResolutionNotes(id, notes);
        ticket = ticketService.updateStatus(id, TroubleTicket.TicketStatus.RESOLVED);
        return ResponseEntity.ok(TmfResponse.success(ticket, "TroubleTicket"));
    }

    @PostMapping("/{id}/escalate")
    public ResponseEntity<TmfResponse<TroubleTicket>> escalate(@PathVariable UUID id) {
        TroubleTicket ticket = ticketService.escalateTicket(id);
        return ResponseEntity.ok(TmfResponse.success(ticket, "TroubleTicket"));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<TmfResponse<TroubleTicket>> close(
            @PathVariable UUID id, @RequestBody(required = false) Map<String, String> request) {
        String resolution = request != null ? request.get("resolutionNotes") : "Resolved";
        TroubleTicket ticket = ticketService.closeTicket(id, resolution);
        return ResponseEntity.ok(TmfResponse.success(ticket, "TroubleTicket"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
